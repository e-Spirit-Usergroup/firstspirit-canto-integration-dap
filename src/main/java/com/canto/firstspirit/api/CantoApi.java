package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAccessTokenData;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoBatchResponse;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CantoApi {

  private final String tenant;

  private OkHttpClient _client;
  private final Class<CantoApi> LOGGER = CantoApi.class;

  private final String appId;
  private final String appSecret;
  private final String userId;

  private long validUntilTimestamp;

  static private final Moshi moshi = new Moshi.Builder().build();
  private final JsonAdapter<CantoSearchResult> cantoSearchResultJsonAdapter = moshi.adapter(CantoSearchResult.class);
  private final JsonAdapter<CantoAsset> cantoAssetJsonAdapter = moshi.adapter(CantoAsset.class);
  private final JsonAdapter<CantoBatchResponse> cantoBatchResponseJsonAdapter = moshi.adapter(CantoBatchResponse.class);

  static JsonAdapter<CantoAccessTokenData> cantoAccessTokenDataJsonAdapter = moshi.adapter(CantoAccessTokenData.class);


  public CantoApi(String tenant, String appId, String appSecret, String userId) {
    this.tenant = tenant;
    this.appId = appId;
    this.appSecret = appSecret;
    this.userId = userId;
  }

  private OkHttpClient getClient() {
    if (this._client == null || this.validUntilTimestamp < System.currentTimeMillis()) {
      // We need to fetch a new access token
      fetchClientWithNewToken();
    }

    return this._client;
  }
  
  private void fetchClientWithNewToken() {

    CantoAccessTokenData cantoAccessTokenData = generateAccessToken(appId, appSecret, userId);

    if (cantoAccessTokenData == null) {
      Logging.logError("CantoApi was unable to retrieve AccessToken", this.getClass());
      this._client = null;
      this.validUntilTimestamp = 0;
      throw new IllegalStateException("No client with valid Access Token available");

    } else {
      Logging.logError("CantoApi Access Token refreshed", this.getClass());
      // Only use 90% of validity Period to ensure we don't get quirks at the end of the validity Period
      this.validUntilTimestamp = System.currentTimeMillis() + Math.round(cantoAccessTokenData.getExpiresInMs() * 0.9);
      this._client = new OkHttpClient.Builder().addNetworkInterceptor(new TokenRequestInterceptor(cantoAccessTokenData.getAccessToken()))
          .build();

    }
  }

  private HttpUrl.Builder getApiUrl() {
    return new HttpUrl.Builder().scheme("https")
        .host(this.tenant)
        .addPathSegments("api/v1");
  }

  private Optional<CantoAsset> fetchAssetById(CantoAssetIdentifier assetId) {

    HttpUrl url = getApiUrl().addPathSegments(assetId.getPath())
        .build();

    Logging.logInfo("[CantoApi][getAssetById] fetching " + url, LOGGER);

    CantoAsset asset = null;

    try (Response response = executeGetRequest(url)) {
      ResponseBody body = response.body();
      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
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
    if (assetIdentifiers.size() == 1) {
      Logging.logInfo("[CantoApi][fetchAssets] Single id fetch: " + Strings.implode(assetIdentifiers, ","), LOGGER);
      return Collections.singletonList(fetchAssetById(assetIdentifiers.get(0)).orElse(null));
    }

    Logging.logInfo("[CantoApi][fetchAssets] fetching ids: " + Strings.implode(assetIdentifiers, ","), LOGGER);

    List<Map<String, String>> requestList = assetIdentifiers.stream()
        .map(cantoAssetIdentifier -> Map.of("id", cantoAssetIdentifier.getId(), "scheme", cantoAssetIdentifier.getSchema()))
        .collect(Collectors.toList());

    String stringifiedJsonBody = moshi.adapter(List.class)
        .toJson(requestList);

    HttpUrl url = getApiUrl().addPathSegments("batch/content")
        .build();

    Logging.logInfo("[CantoApi][fetchAssets] url:  " + url, LOGGER);

    try (Response response = executePostRequest(url, stringifiedJsonBody)) {
      ResponseBody body = response.body();

      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
      CantoBatchResponse cantoBatchResponse = cantoBatchResponseJsonAdapter.fromJson(body.source());

      if (cantoBatchResponse == null) {
        throw new IllegalStateException("Unable to parse Result to CantoBatchResponse for url " + url);
      }

      Map<String, CantoAsset> fetchedAssets = cantoBatchResponse.getDocResult()
          .stream()
          .collect(Collectors.toMap(asset -> new CantoAssetIdentifier(asset.getScheme(), asset.getId()).getPath(), Function.identity()));

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
    HttpUrl url = getApiUrl().addPathSegments("search")
        .addQueryParameter("scheme", "image")
        .addQueryParameter("keyword", keyword)
        .addQueryParameter("start", String.valueOf(start))
        .addQueryParameter("limit", String.valueOf(limit))
        .build();

    Logging.logInfo("search " + url, getClass());

    try (Response response = executeGetRequest(url)) {
      ResponseBody body = response.body();
      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
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

    Request request = new Request.Builder().url(url)
        .build();
    Response response = getClient().newCall(request)
        .execute();

    if (!response.isSuccessful()) {
      String bodyText = getAndCloseResponseBodyAsString(response);
      throw new IOException("Unexpected code " + response.code() + ", " + response.message() + "\n" + bodyText);
    }

    return response;
  }


  /**
   * @param url Request URL
   * @return body source code
   * @throws IOException on failed request
   */
  private Response executePostRequest(HttpUrl url, String jsonBody) throws IOException {

    RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
    Request request = new Request.Builder().url(url)
        .post(requestBody)
        .build();
    Response response = getClient().newCall(request)
        .execute();

    if (!response.isSuccessful()) {
      response.close();
      throw new IOException("Unexpected code " + response.code());
    }

    return response;

  }


  private @Nullable String getAndCloseResponseBodyAsString(Response response) {
    ResponseBody body = response.body();
    String bodyText = null;
    if (body != null) {
      try {
        bodyText = body.source()
            .readUtf8();
      } catch (Exception e) {
        Logging.logError("Unable to read Response Body", this.getClass());
      } finally {
        body.close();
      }
    }
    return bodyText;
  }

  /**
   * Generates a new Access Token for Canto API via appId and appSecret. Access Tokens are valid for 30 Days Returns null on error or invalid
   * response
   *
   * @param appId     Canto appId
   * @param appSecret Canto appSecret
   * @param userId    User access Token is generated for
   * @return CantoAccessTokenData on success, null otherwise
   */
  public @Nullable CantoAccessTokenData generateAccessToken(String appId, String appSecret, String userId) {

    Logging.logInfo("Generating new Access Token", CantoApi.class);

    if (appId.isBlank() || appSecret.isBlank() || userId.isBlank()) {
      Logging.logError("Unable to generate Access Token; appId, appSecret and userId must be provided", CantoApi.class);
      return null;
    }

    String url = new HttpUrl.Builder().scheme("https")
        .host("oauth.canto.de")
        .addPathSegments("oauth/api/oauth2/compatible/token")
        .addQueryParameter("app_id", appId)
        .addQueryParameter("app_secret", appSecret)
        .addQueryParameter("grant_type", "client_credentials")
        .addQueryParameter("user_id", userId)
        .toString();

    OkHttpClient client = new OkHttpClient.Builder().build();

    RequestBody requestBody = RequestBody.create("", null);

    Request request = new Request.Builder().url(url)
        .post(requestBody)
        .build();

    try (Response response = client.newCall(request)
        .execute()) {
      if (response.isSuccessful() && response.body() != null) {

        CantoAccessTokenData cantoAccessTokenData = cantoAccessTokenDataJsonAdapter.fromJson(response.body()
                                                                                                 .source());
        if (CantoAccessTokenData.isValid(cantoAccessTokenData)) {
          Logging.logInfo("Successfully generated new AccessToken: " + cantoAccessTokenData, CantoApi.class);
          return cantoAccessTokenData;
        } else {
          Logging.logError("Invalid Access Token Data: " + cantoAccessTokenData, CantoApi.class);
        }

      } else {
        String bodyText = getAndCloseResponseBodyAsString(response);
        Logging.logError("Unable to generate new Access Token. Response: " + response.code() + ", " + response.message() + "\n body: " + bodyText,
                         CantoApi.class);
      }
    } catch (Exception e) {
      Logging.logError("Unable to generate Access Token. Error Occurred.", e, CantoApi.class);
    }

    return null;
  }

}
