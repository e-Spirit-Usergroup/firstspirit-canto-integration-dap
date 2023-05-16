package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoBatchResponse;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CantoApi {

    private final String tenant;
    private final String mdc_domain;
    private final String mdc_account_id;

    private final OkHttpClient client;
    private final Class<CantoApi> LOGGER = CantoApi.class;


    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<CantoSearchResult> cantoSearchResultJsonAdapter = moshi.adapter(CantoSearchResult.class);
    private final JsonAdapter<CantoAsset> cantoAssetJsonAdapter = moshi.adapter(CantoAsset.class);
    private final JsonAdapter<CantoBatchResponse> cantoBatchResponseJsonAdapter = moshi.adapter(CantoBatchResponse.class);

    public CantoApi(String tenant, String token, String mdc_domain, String mdc_account_id) {
        this.tenant = tenant;
        this.mdc_domain = mdc_domain;
        this.mdc_account_id = mdc_account_id;
        this.client = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(new TokenRequestInterceptor(token))
                .build();
    }

    private HttpUrl.Builder getApiUrl() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(this.tenant)
                .addPathSegments("api/v1");
    }

    private HttpUrl.Builder getMDCUrl() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(this.mdc_domain);
    }

    public String getMDCAssetBaseUrl(@NotNull CantoAsset asset) {
        HttpUrl url = getMDCUrl()
                .addPathSegment("asset")
                .addPathSegment(this.mdc_account_id)
                .addPathSegments(asset.getScheme() + "_" + asset.getId())
                .build();

        Logging.logInfo("getMDCAssetBaseUrl " + url, getClass());

        return url.toString();
    }

    public String getMDCRenditionBaseUrl(@NotNull CantoAsset asset) {
        HttpUrl url = getMDCUrl()
                .addPathSegment("image")
                .addPathSegment(this.mdc_account_id)
                .addPathSegments(asset.getScheme() + "_" + asset.getId())
                .build();

        Logging.logInfo("getMDCRenditionBaseUrl " + url, getClass());

        return url.toString();
    }


    private Optional<CantoAsset> fetchAssetById(CantoAssetIdentifier assetId) {

        HttpUrl url = getApiUrl()
                .addPathSegments(assetId.getPath())
                .build();

        Logging.logInfo("[CantoApi][getAssetById] fetching " + url, LOGGER);

        CantoAsset asset = null;

        try (Response response = executeGetRequest(url)) {
            ResponseBody body = response.body();
            if (body == null) throw new IllegalStateException("Response Body was null for url " + url);
            asset = cantoAssetJsonAdapter.fromJson(body.source());
        } catch (Exception e) {
            Logging.logError("[CantoApi][getAssetById] Error occurred", e, LOGGER);
        }
        Logging.logInfo("[CantoApi][getAssetById] returning Asset " + (asset == null ? null : asset.getId()), LOGGER);
        return Optional.ofNullable(asset);
    }


    /**
     * Fetch multiple Assets based on a Collection of identifiers.
     *
     * @param assetIdentifiers of form {scheme}/{cantoId}
     * @return List of CantoAssets in the same order as identifiers. Missing Assets are replaced by null
     */
    public List<CantoAsset> fetchAssets(@NotNull List<? extends CantoAssetIdentifier> assetIdentifiers) {
        if(assetIdentifiers.size() == 1) {
            Logging.logInfo("[CantoApi][fetchAssets] Single id fetch: " + Strings.implode(assetIdentifiers, ","), LOGGER);
            return Collections.singletonList(fetchAssetById(assetIdentifiers.get(0)).orElse(null));
        }

        Logging.logInfo("[CantoApi][fetchAssets] fetching ids: " + Strings.implode(assetIdentifiers, ","), LOGGER);

        List<Map<String, String>> requestList = assetIdentifiers.stream().map(cantoAssetIdentifier -> Map.of(
                "id", cantoAssetIdentifier.getId(),
                "scheme", cantoAssetIdentifier.getSchema()
        )).collect(Collectors.toList());

        String stringifiedJsonBody = moshi.adapter(List.class).toJson(requestList);

        HttpUrl url = getApiUrl()
                .addPathSegments("batch/content")
                .build();

        Logging.logInfo("[CantoApi][fetchAssets] url:  " + url, LOGGER);


        try (Response response = executePostRequest(url, stringifiedJsonBody)) {
            ResponseBody body = response.body();

            if (body == null) throw new IllegalStateException("Response Body was null for url " + url);
            CantoBatchResponse cantoBatchResponse = cantoBatchResponseJsonAdapter.fromJson(body.source());

            if (cantoBatchResponse == null)
                throw new IllegalStateException("Unable to parse Result to CantoBatchResponse for url " + url);

            Map<String, CantoAsset> fetchedAssets = cantoBatchResponse.getDocResult()
                    .stream()
                    .collect(Collectors.toMap(asset ->
                                    new CantoAssetIdentifier(asset.getScheme(), asset.getId()).getPath(),
                            Function.identity()
                    ));

            Logging.logInfo("[CantoApi][fetchAssets] " + cantoBatchResponse, LOGGER);


            // Ensure correct Order and replace missing Values with null
            return assetIdentifiers.stream()
                    .map(identifier -> fetchedAssets.get(identifier.getPath()))
                    .collect(Collectors.toList());


        } catch (IOException e) {
            Logging.logError("Error during bulk fetch of Ids" + Strings.implode(assetIdentifiers, ","), e, this.getClass());
            // Return list of nulls
            return assetIdentifiers.stream()
                    .map(id -> (CantoAsset) null)
                    .collect(Collectors.toList());
        }

    }

    /**
     * Search Assets based on keyword. Limits search to 100 elements
     *
     * @param keyword passed as search filter
     * @return Wrapper with a list of fetched CantoAssets including some MetaData about the search
     */
    public CantoSearchResult fetchSearch(String keyword) {
        return fetchSearch(keyword, 0, 100);
    }

    /**
     * Search Assets based on keyword.
     *
     * @param keyword passed as search filter
     * @return Wrapper with a list of fetched CantoAssets including some MetaData about the search
     */
    public CantoSearchResult fetchSearch(String keyword, int start, int limit) {

        // Logging.logWarning("test", CantoApi.class);
        HttpUrl url = getApiUrl()
                .addPathSegments("search")
                .addQueryParameter("scheme", "image")
                .addQueryParameter("keyword", keyword)
                .addQueryParameter("start", String.valueOf(start))
                .addQueryParameter("limit", String.valueOf(limit))
                .build();

        Logging.logInfo("search " + url, getClass());

        try (Response response = executeGetRequest(url)) {
            ResponseBody body = response.body();
            if (body == null) throw new IllegalStateException("Response Body was null for url " + url);
            CantoSearchResult cantoSearchResult = cantoSearchResultJsonAdapter.fromJson(body.source());

            Logging.logInfo("searchResult " + cantoSearchResult, getClass());

            if (cantoSearchResult != null && cantoSearchResult.getResults() == null) {
                cantoSearchResult.setResults(Collections.emptyList());
                cantoSearchResult.setFound(0L);
            }

            return cantoSearchResult;
        } catch (Exception e) {
            Logging.logError("searchResultException", e, this.getClass());

            CantoSearchResult result = new CantoSearchResult();
            result.setResults(Collections.emptyList());
            return result;
        }

    }


    /**
     * @param url Request URL
     * @return body source code
     * @throws IOException on failed request
     */
    private Response executeGetRequest(HttpUrl url) throws IOException {

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            response.close();
            throw new IOException("Unexpected code " + response.code());
        }

        return response;
/*
            ResponseBody body = response.body();
            if (body == null) throw new NullPointerException("Response body was null");
            return body.source();
*/
    }


    /**
     * @param url Request URL
     * @return body source code
     * @throws IOException on failed request
     */
    private Response executePostRequest(HttpUrl url, String jsonBody) throws IOException {

        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            response.close();
            throw new IOException("Unexpected code " + response.code());
        }

        return response;
/*
            ResponseBody body = response.body();
            if (body == null) throw new NullPointerException("Response body was null");
            return body.source();
*/
    }


}
