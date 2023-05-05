package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.espirit.common.base.Logging;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                .host(this.tenant);
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

    private Optional<CantoAsset> fetchAssetById(String id) {

        HttpUrl url = getApiUrl()
                .addPathSegments("api/v1")
                .addPathSegments(id)
                .build();

        Logging.logInfo("[CantoApi][getAssetById] fetching " + url, LOGGER);

        CantoAsset asset = null;

        try (Response response = executeGetRequest(url)) {
            ResponseBody body = response.body();
            if (body == null) throw new IllegalStateException("Response Body was null for url " + url);
            asset = cantoAssetJsonAdapter.fromJson(body.source());
        } catch (Exception e) {
            Logging.logInfo("[CantoApi][getAssetById] Error occurred", e, LOGGER);
        }
        Logging.logInfo("[CantoApi][getAssetById] returning Asset " + (asset == null ? null : asset.getId()), LOGGER);
        return Optional.ofNullable(asset);
    }


    /**
     * Fetch multiple Assets based on a Collection of identifiers
     * @param identifiers
     * @return
     */
    public List<CantoAsset> fetchAssets(@NotNull List<String> identifiers) {
        // TODO: batch Post Request
        return identifiers.stream().map(id -> fetchAssetById(id).orElse(null))
                .collect(Collectors.toList());

    }

    /**
     * Search Assets based on keyword.
     * @param keyword passed as search filter
     * @return Wrapper with a list of fetched CantoAssets including some MetaData about the search
     */
    public CantoSearchResult fetchSearch(String keyword) {

        // Logging.logWarning("test", CantoApi.class);
        HttpUrl url = getApiUrl()
                .addPathSegments("api/v1/search")
                .addQueryParameter("scheme", "image")
                .addQueryParameter("keyword", keyword)
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
