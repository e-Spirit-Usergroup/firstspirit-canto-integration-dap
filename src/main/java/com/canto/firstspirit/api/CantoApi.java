package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAccessTokenData;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoBatchResponse;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.RequestLimiter;
import com.canto.firstspirit.service.cache.ProjectBoundCacheAccess;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.util.CantoScheme;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import okhttp3.*;
import okhttp3.HttpUrl.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Offers Access to the CantoApi. Meant to be used and managed through {@link com.canto.firstspirit.service.CantoSaasServiceImpl}.
 * <br> Manages its own accessToken based validity period. If used directly outside of service, access Token management
 * will likely not work as desired and create lots of access tokens!
 * <br>
 * <br>
 * <strong>Direct Use outside of Service not recommended. </strong>
 */
public class CantoApi {

  static private final Moshi moshi = new Moshi.Builder().build();
  static JsonAdapter<CantoAccessTokenData> cantoAccessTokenDataJsonAdapter = moshi.adapter(CantoAccessTokenData.class);
  private final String tenant;
  private final String oAuthBaseUrl;
  private final @Nullable RequestLimiter singleFetchRequestLimiter;
  private final @Nullable RequestLimiter batchFetchRequestLimiter;
  private final ProjectBoundCacheAccess projectBoundCacheAccess;
  private final long timeoutInSeconds;
  private final Class<CantoApi> LOGGER = CantoApi.class;
  private final String appId;
  private final String appSecret;
  private final String userId;
  private final JsonAdapter<CantoSearchResult> cantoSearchResultJsonAdapter = moshi.adapter(CantoSearchResult.class);
  private final JsonAdapter<CantoAsset> cantoAssetJsonAdapter = moshi.adapter(CantoAsset.class);
  private final JsonAdapter<CantoBatchResponse> cantoBatchResponseJsonAdapter = moshi.adapter(CantoBatchResponse.class);
  /**
   * <strong>!! Do not access directly. use {@link #getClient()} instead !! </strong>
   * <br> Using {@link #getClient()} ensures valid Access Token
   */
  private OkHttpClient _client;
  private long validUntilTimestamp;


  /**
   * Creates new CantoApi. Access Token will be generated via appId, appSecret and UserId. Each Api instantiation generates new Access Token. Instances meant to be managed by {@link com.canto.firstspirit.service.CantoSaasServiceImpl}
   * <br><strong>Direct Use outside of Service not recommended. </strong>
   *
   * @param tenant                    tenant
   * @param oAuthBaseUrl              url with correct region, matching the tenant
   * @param appId                     appId
   * @param appSecret                 appSecret
   * @param userId                    userId
   * @param singleFetchRequestLimiter singleFetchRequestLimiter to force Delay between single fetch request
   * @param batchFetchRequestLimiter  batchFetchRequestLimiter to force Delay between single fetch request
   * @param projectBoundCacheAccess   access to central cache
   */
  public CantoApi(String tenant, String oAuthBaseUrl, String appId, String appSecret, String userId, @Nullable RequestLimiter singleFetchRequestLimiter, @Nullable RequestLimiter batchFetchRequestLimiter, ProjectBoundCacheAccess projectBoundCacheAccess) {

    this(tenant, oAuthBaseUrl, appId, appSecret, userId, singleFetchRequestLimiter, batchFetchRequestLimiter, projectBoundCacheAccess, 20);

  }

  /**
   * Creates new CantoApi. Access Token will be generated via appId, appSecret and UserId. Each Api instantiation generates new Access Token. Instances meant to be managed by {@link com.canto.firstspirit.service.CantoSaasServiceImpl}
   * <br><strong>Direct Use outside of Service not recommended. </strong>
   *
   * @param tenant                    tenant
   * @param oAuthBaseUrl              url with correct region, matching the tenant
   * @param appId                     appId
   * @param appSecret                 appSecret
   * @param userId                    userId
   * @param singleFetchRequestLimiter singleFetchRequestLimiter to force Delay between single fetch request
   * @param batchFetchRequestLimiter  batchFetchRequestLimiter to force Delay between single fetch request
   * @param projectBoundCacheAccess   access to central cache
   * @param timeoutInSeconds          request timeout in seconds
   */
  public CantoApi(String tenant, String oAuthBaseUrl, String appId, String appSecret, String userId, @Nullable RequestLimiter singleFetchRequestLimiter, @Nullable RequestLimiter batchFetchRequestLimiter, ProjectBoundCacheAccess projectBoundCacheAccess, long timeoutInSeconds) {
    this.tenant = tenant;
    this.appId = appId;
    this.appSecret = appSecret;
    this.userId = userId;
    this.oAuthBaseUrl = oAuthBaseUrl;
    this.singleFetchRequestLimiter = singleFetchRequestLimiter;
    this.batchFetchRequestLimiter = batchFetchRequestLimiter;
    this.projectBoundCacheAccess = projectBoundCacheAccess;
    // Do not accept 0 or less timeout, as it would lead to waiting indefinitely.
    this.timeoutInSeconds = timeoutInSeconds <= 0 ? 20 : timeoutInSeconds;
  }

  /**
   * <strong>!! Always use this method to access private _client member !!</strong>
   * <p>
   * Checks access Token validity and fetches new Token if needed. Returns client with valid access token
   *
   * @return client
   */
  private OkHttpClient getClient() {
    if (this._client == null || this.validUntilTimestamp < System.currentTimeMillis()) {
      // We need to fetch a new access token
      fetchClientWithNewToken();
    }

    return this._client;
  }

  /**
   * fetches new Access Token from CantoApi and sets client and validity. If not successful, sets client to null and validity to 0 and throws IllegalState Exception
   */
  private void fetchClientWithNewToken() {

    CantoAccessTokenData cantoAccessTokenData = generateAccessToken();

    if (cantoAccessTokenData == null) {
      Logging.logError("[fetchClientWithNewToken] CantoApi was unable to retrieve AccessToken", this.getClass());
      this._client = null;
      this.validUntilTimestamp = 0;
      throw new IllegalStateException("No client with valid Access Token available");

    } else {
      Logging.logInfo("[fetchClientWithNewToken] CantoApi Access Token refreshed", this.getClass());
      // Only use 90% of validity Period to ensure we don't get quirks at the end of the validity Period
      this.validUntilTimestamp = System.currentTimeMillis() + Math.round(cantoAccessTokenData.getExpiresInMs() * 0.9);
      this._client = new OkHttpClient.Builder().callTimeout(timeoutInSeconds, TimeUnit.SECONDS)
          .connectTimeout(0, TimeUnit.SECONDS)
          .readTimeout(0, TimeUnit.SECONDS)
          .writeTimeout(0, TimeUnit.SECONDS)
          .addNetworkInterceptor(new TokenRequestInterceptor(cantoAccessTokenData.getAccessToken()))
          .build();

    }
  }

  /**
   * returns UrlBuilder with BaseURL for different API Endpoint
   *
   * @return URL Builder with base URL
   */
  private HttpUrl.Builder getApiUrl() {
    return new HttpUrl.Builder().scheme("https")
        .host(this.tenant)
        .addPathSegments("api/v1");
  }


  /**
   * fetch single Asset by Identifier
   *
   * @param assetId CantoAssetIdentifier
   * @return Optional of Asset
   */
  private @Nullable CantoAsset fetchAssetById(CantoAssetIdentifier assetId) {

    // Check cache
    CantoAsset cachedCantoAsset = projectBoundCacheAccess.retrieveFromCache(assetId);
    if (cachedCantoAsset != null && cachedCantoAsset.getMetadata() != null && !cachedCantoAsset.getMetadata().isEmpty()) {
      return cachedCantoAsset;
    } else {
      Logging.logDebug("[fetchAssetById] cache miss for asset: "+ assetId, LOGGER);
    }

    HttpUrl url = getApiUrl().addPathSegments(assetId.getPath())
        .build();

    Logging.logInfo("[getAssetById] fetching " + url, LOGGER);

    CantoAsset asset = null;
    if (singleFetchRequestLimiter != null) {
      singleFetchRequestLimiter.delayRequestIfNecessary();
    }

    try (Response response = executeGetRequest(url)) {
      ResponseBody body = response.body();
      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
      asset = cantoAssetJsonAdapter.fromJson(body.source());
    } catch (Exception e) {
      Logging.logError("[getAssetById] Error occurred", e, LOGGER);
    }
    Logging.logDebug("[getAssetById] returning Asset " + (asset == null ? null : asset.getId()), LOGGER);

    projectBoundCacheAccess.addToCache(assetId, asset);
    return asset;
  }

  /**
   * Fetch multiple Assets based on a Collection of identifiers.
   *
   * @param assetIdentifiers CantoAssetIdentifier containing scheme and id
   * @return List of CantoAssets in the same order as identifiers. Missing Assets are replaced by null
   */
  public @NotNull List<@Nullable CantoAsset> fetchAssets(@NotNull List<? extends CantoAssetIdentifier> assetIdentifiers) {
    Logging.logInfo("[fetchAssets] fetching ids: " + Strings.implode(assetIdentifiers, ","), LOGGER);
    if (assetIdentifiers.isEmpty()) {
      Logging.logInfo("[fetchAssets] Identifier List empty, returning empty list", LOGGER);
      return Collections.emptyList();
    }
    if (assetIdentifiers.size() == 1) {
      return Collections.singletonList(fetchAssetById(assetIdentifiers.get(0)));
    }

    // Try to retrieve from Cache first
    Map<Integer, CantoAsset> cachedAssetsMap = new HashMap<>();
    List<CantoAssetIdentifier> identifiersToFetch = new ArrayList<>();

    for (int i = 0; i < assetIdentifiers.size(); i++) {
      CantoAssetIdentifier identifier = assetIdentifiers.get(i);
      CantoAsset cachedAsset = projectBoundCacheAccess.retrieveFromCache(identifier);
      if (cachedAsset != null) {
        cachedAssetsMap.put(i, cachedAsset);
      } else {
        identifiersToFetch.add(identifier);
        Logging.logDebug("[fetchAssets BULK] cache miss for asset: "+ identifier, LOGGER);
      }
    }

    if (identifiersToFetch.isEmpty()) {
      // all items in cache!
      return new ArrayList<>(cachedAssetsMap.values());
    }

    List<Map<String, String>> requestList = identifiersToFetch.stream()
        .map(cantoAssetIdentifier -> Map.of("id", cantoAssetIdentifier.getId(), "scheme", cantoAssetIdentifier.getSchema()))
        .collect(Collectors.toList());

    String stringifiedJsonBody = moshi.adapter(List.class)
        .toJson(requestList);

    HttpUrl url = getApiUrl().addPathSegments("batch/content")
        .build();

    if (batchFetchRequestLimiter != null) {
      batchFetchRequestLimiter.delayRequestIfNecessary();
    }

    Logging.logDebug("[fetchAssets] url:  " + url, LOGGER);
    try (Response response = executePostRequest(url, stringifiedJsonBody)) {
      ResponseBody body = response.body();

      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
      CantoBatchResponse cantoBatchResponse = cantoBatchResponseJsonAdapter.fromJson(body.source());

      if (cantoBatchResponse == null) {
        throw new IllegalStateException("Unable to parse Result to CantoBatchResponse for url " + url);
      }

      List<CantoAsset> docResult = cantoBatchResponse.getDocResult();
      Map<String, CantoAsset> newlyFetchedAssets = docResult.stream()
              .filter(Objects::nonNull)
              .collect(Collectors.toMap(asset -> CantoAssetIdentifierFactory.fromCantoAsset(asset)
              .getPath(), Function.identity()));

      // Add newly fetched assets to cache
      projectBoundCacheAccess.addAllToCache(docResult.stream().filter(Objects::nonNull).collect(Collectors.toList()));

      Logging.logDebug("[fetchAssets] " + cantoBatchResponse, LOGGER);

      // Ensure correct Order and replace missing Values with null
      List<CantoAsset> resultList = new ArrayList<>(Collections.nCopies(assetIdentifiers.size(), null));
      // fill in cached ones
      for (Map.Entry<Integer, CantoAsset> entry : cachedAssetsMap.entrySet()) {
        resultList.set(entry.getKey(), entry.getValue());
      }
      // fill in newly fetched ones
      for (int i = 0; i < assetIdentifiers.size(); i++) {
        if (!cachedAssetsMap.containsKey(i)) {
          CantoAssetIdentifier identifier = assetIdentifiers.get(i);
          resultList.set(i, newlyFetchedAssets.get(identifier.getPath()));
        }
      }

      return resultList;

    } catch (Exception e) {
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
   * @param searchParams SearchParams to configure Search request
   * @return Wrapper with a list of fetched CantoAssets including some MetaData about the search
   */
  public CantoSearchResult fetchSearch(CantoSearchParams searchParams) {
    return fetchSearch(searchParams.getKeyword(), searchParams.getScheme(), searchParams.getAlbum(), searchParams.getStart(), searchParams.getLimit(), searchParams.getApprovalStatus());
  }

  /**
   * Search Assets based on keyword.
   *
   * @param keyword passed as search filter
   * @param scheme  Filter for Scheme. If none passed, all Schemes are searched ("image", "video", "audio", "document", "presentation", "other")
   * @param start   offset for search results
   * @param limit   max elements to return
   * @return Wrapper with a list of fetched CantoAssets including some MetaData about the search
   */
  public CantoSearchResult fetchSearch(String keyword, @Nullable String scheme, @Nullable String albumId, int start, int limit, @Nullable String approvalStatus) {

    Builder urlBuilder = getApiUrl();

    if (albumId != null && !albumId.equals("--Folder--")) {
      urlBuilder.addPathSegments("album")
          .addPathSegments(albumId);
    } else {
      urlBuilder.addPathSegments("search");
    }

    urlBuilder.addQueryParameter("keyword", keyword)
        .addQueryParameter("start", String.valueOf(start))
        .addQueryParameter("limit", String.valueOf(limit));

    // Validate CantoScheme
    CantoScheme cantoScheme = CantoScheme.fromString(scheme);
    if (cantoScheme != null) {
      urlBuilder.addQueryParameter("scheme", cantoScheme.toString());
    } else {
      final String allSchemes = Arrays.stream(CantoScheme.values())
          .map(CantoScheme::toString)
          .collect(Collectors.joining("|"));
      urlBuilder.addQueryParameter("scheme", allSchemes);
    }
    if (approvalStatus != null) {
      urlBuilder.addQueryParameter("approval", approvalStatus.toLowerCase());
    }

    final HttpUrl url = urlBuilder.build();

    Logging.logInfo("[fetchSearch] " + url, getClass());

    try (Response response = executeGetRequest(url)) {
      ResponseBody body = response.body();
      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
      CantoSearchResult cantoSearchResult = cantoSearchResultJsonAdapter.fromJson(body.source());

      Logging.logDebug("searchResult " + cantoSearchResult, getClass());

      if (cantoSearchResult == null) {
        throw new IllegalStateException("CantoSearchResult was null. Returning empty Result as default");
      }

      List<CantoAsset> foundAssets = cantoSearchResult.getResults();

      if (foundAssets == null) {
        cantoSearchResult.setResults(Collections.emptyList());
        cantoSearchResult.setFound(0L);
      } else {
        projectBoundCacheAccess.updateAllInCache(foundAssets);
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
  Response executeGetRequest(HttpUrl url) throws IOException {

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


  /**
   * get body as String and close it. Helper method for error cases
   *
   * @param response response
   * @return body as String
   */
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
   * Generates a new Access Token for Canto API via appId and appSecret. Access Tokens are valid for 30 Days Returns null on error or invalid response
   *
   * @return CantoAccessTokenData on success, null otherwise
   */
  public @Nullable CantoAccessTokenData generateAccessToken() {

    Logging.logInfo("[generateAccessToken] Generating new Access Token", CantoApi.class);

    if (appId.isBlank() || appSecret.isBlank() || userId.isBlank()) {
      Logging.logError("Unable to generate Access Token; appId, appSecret and userId must be provided", CantoApi.class);
      return null;
    }

    HttpUrl baseUrl = HttpUrl.parse(oAuthBaseUrl);

    if (baseUrl == null) {
      throw new IllegalStateException("OAuthBaseUrl invalid, not parsable. Please check your configuration. " + oAuthBaseUrl);
    }

    final Builder urlBuilder = baseUrl.newBuilder();

    final String url = urlBuilder.addPathSegments("oauth/api/oauth2/compatible/token")
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
          Logging.logInfo("[generateAccessToken] Successfully generated new AccessToken: " + cantoAccessTokenData, CantoApi.class);
          return cantoAccessTokenData;
        } else {
          Logging.logError("Invalid Access Token Data: " + cantoAccessTokenData, CantoApi.class);
        }

      } else {
        String bodyText = getAndCloseResponseBodyAsString(response);
        Logging.logError("Unable to generate new Access Token. Response: " + response.code() + ", " + response.message() + "\n body: " + bodyText, CantoApi.class);
      }
    } catch (Exception e) {
      Logging.logError("Unable to generate Access Token. Error Occurred.", e, CantoApi.class);
    }

    return null;
  }

  /**
   * Fetches the folder structure from the API endpoint.
   *
   * @return A string representing the folder structure in JSON format as String, or null if an error occurs.
   */
  public @Nullable String fetchFolderStructure() {
    // Build the URL to fetch the folder structure
    HttpUrl url = getApiUrl().addPathSegments("tree?layer=-1")
        .build();

    // Log the URL being fetched
    Logging.logInfo("[fetchFolderStructure] fetching " + url, LOGGER);

    // Initialize folderStructureJsonString to null
    String folderStructureJsonString = null;

    // If rate limiting is enabled, delay the request if necessary
    if (singleFetchRequestLimiter != null) {
      singleFetchRequestLimiter.delayRequestIfNecessary();
    }

    try (Response response = executeGetRequest(url)) {
      ResponseBody body = response.body();
      // Check if response body is null
      if (body == null) {
        throw new IllegalStateException("Response Body was null for url " + url);
      }
      // Read the response body as a string
      folderStructureJsonString = body.string();
    } catch (Exception e) {
      // Log any errors that occur during the request
      Logging.logError("[fetchFolderStructure] Error occurred", e, LOGGER);
    }

    // Return the folder structure JSON string, or null if an error occurred
    return folderStructureJsonString;
  }
}
