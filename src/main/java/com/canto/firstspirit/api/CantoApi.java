package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.espirit.common.base.Logging;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
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
                .addPathSegments(asset.getScheme()+"_"+asset.getId())
                .build();

        Logging.logInfo("getMDCAssetBaseUrl " + url.toString(), getClass());

        return url.toString();
    }

    public String getMDCRenditionBaseUrl(@NotNull CantoAsset asset) {
        HttpUrl url = getMDCUrl()
                .addPathSegment("image")
                .addPathSegment(this.mdc_account_id)
                .addPathSegments(asset.getScheme()+"_"+asset.getId())
                .build();

        Logging.logInfo("getMDCRenditionBaseUrl " + url.toString(), getClass());

        return url.toString();
    }

    @PublicApi
    public String getImageUrl(String assetID) {
        /* https://{canto.tenant}/download/{SCHEME}/{ASSET_ID}/original */

        HttpUrl url = getApiUrl()
                .addPathSegment("download")
                .addPathSegments(assetID)
                .addPathSegment("original")
                .build();

        Logging.logInfo("getImage " + url.toString(), getClass());

        return url.toString();
    }

    @PublicApi
    public String getImageUrl(@NotNull CantoAsset asset) {
        return getImageUrl(asset.getScheme() + "/" + asset.getId());
    }

    @PublicApi
    public String getPreviewImageUrl(String assetID, int resolution) {

        /* https://{canto.tenant}/preview/{asset}/{res} */
        HttpUrl url = getApiUrl()
                .addPathSegment("preview")
                .addPathSegments(assetID)
                .addPathSegment(Integer.toString(resolution))
                .build();

        Logging.logInfo("preview " + url.toString(), getClass());

        return url.toString();
    }


    @PublicApi
    public String getThumbnailUrl(String assetID) {
        return getPreviewImageUrl(assetID, 100);
    }

    @PublicApi
    public String getThumbnailUrl(@NotNull CantoAsset asset) {
        return getPreviewImageUrl(asset, 100);
    }

    @PublicApi
    public String getPreviewImageUrl(String assetID) {
        return getPreviewImageUrl(assetID, 800);
    }

    @PublicApi
    public String getPreviewImageUrl(@NotNull CantoAsset asset) {

        return getPreviewImageUrl(asset.getScheme() + "/" + asset.getId());
    }

    @PublicApi
    public String getPreviewImageUrl(@NotNull CantoAsset asset, int resolution) {
        return getPreviewImageUrl(asset.getScheme() + "/" + asset.getId(), resolution);
    }


    public List<CantoAsset> getAssets(@NotNull Collection<String> identifiers) {

        /*return identifiers.stream().map(id -> getAssetById(id).orElseGet(() -> CantoAsset.createDummyAsset(id)))
                .collect(Collectors.toList());*/
        return identifiers.stream().map(id -> getAssetById(id).orElseGet(null))
                .collect(Collectors.toList());

    }

    /**
     * @param url Request URL
     * @return body source code
     * @throws IOException on failed request
     */
    private BufferedSource getRequest(HttpUrl url) throws IOException {

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        try {
            return response.body().source();
        } catch (NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }


    private Optional<CantoAsset> getAssetById(String id) {

        HttpUrl url = getApiUrl()
                .addPathSegments("api/v1")
                .addPathSegments(id)
                .build();

        Logging.logInfo("getAsset " + url.toString(), getClass());

        CantoAsset asset = null;

        try {
            asset = cantoAssetJsonAdapter.fromJson(getRequest(url));
        } catch (IOException e) {

        }

        return Optional.ofNullable(asset);
    }

    public CantoSearchResult search(String keyword) {

        // Logging.logWarning("test", CantoApi.class);
        HttpUrl url = getApiUrl()
                .addPathSegments("api/v1/search")
                .addQueryParameter("scheme", "image")
                .addQueryParameter("keyword", keyword)
                .build();

        Logging.logInfo("search " + url.toString(), getClass());

        try {
            CantoSearchResult cantoSearchResult = cantoSearchResultJsonAdapter.fromJson(getRequest(url));

            Logging.logInfo("searchResult " + cantoSearchResult.toString(), getClass());

            if (cantoSearchResult != null && cantoSearchResult.getResults() == null) {
                cantoSearchResult.setResults(Collections.emptyList());
                cantoSearchResult.setFound(0L);
            }

            return cantoSearchResult;
        } catch (Exception e) {
            CantoSearchResult result = new CantoSearchResult();
            result.setResults(Collections.emptyList());
            Logging.logInfo("searchResultException " + e.getMessage(), getClass());
            return result;
        }
    }
}
