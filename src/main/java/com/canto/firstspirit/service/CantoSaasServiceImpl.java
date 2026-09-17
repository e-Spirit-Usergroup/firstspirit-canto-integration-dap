package com.canto.firstspirit.service;

import static com.canto.firstspirit.service.CantoSaasServiceImpl.SERVICE_NAME;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.CantoSaasServiceConfigurable.ServiceConfiguration;
import com.canto.firstspirit.service.cache.CentralCache;
import com.canto.firstspirit.service.cache.ProjectBoundCacheAccess;
import com.canto.firstspirit.service.cache.model.CachePersistenceEntry;
import com.canto.firstspirit.service.factory.CantoAssetDTOFactory;
import com.canto.firstspirit.service.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.factory.CantoSearchResultDTOFactory;
import com.canto.firstspirit.service.server.CantoSaasService;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.service.server.model.CantoServiceConnection;
import com.espirit.moddev.components.annotations.ServiceComponent;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.agency.BrokerAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.io.FileHandle;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ServiceComponent(name = SERVICE_NAME, displayName = "CantoSaaS Connector Service", configurable = CantoSaasServiceConfigurable.class)
public class CantoSaasServiceImpl implements CantoSaasService, Service<CantoSaasService> {

  private ServerEnvironment serverEnvironment;
  private ServiceDescriptor serviceDescriptor;
  public static final String SERVICE_NAME = "CantoSaasService";

  private static final String CACHE_FILE_NAME = "canto-cache.json";
  private static final Moshi MOSHI = new Moshi.Builder().build();
  private static final Type CACHE_ENTRY_LIST_TYPE = Types.newParameterizedType(List.class, CachePersistenceEntry.class);
  private static final JsonAdapter<List<CachePersistenceEntry>> CACHE_ADAPTER = MOSHI.adapter(CACHE_ENTRY_LIST_TYPE);

  private Map<Integer, CantoApi> apiConnectionPool;

  private @Nullable CentralCache centralCache = null;

  private @Nullable RequestLimiter singleFetchRequestLimiter = null;
  private @Nullable RequestLimiter batchFetchRequestLimiter = null;
  private @Nullable RequestLimiter searchRequestLimiter = null;

  private ServiceConfiguration serviceConfiguration;

  public CantoServiceConnection getServiceConnection(@NotNull final CantoConfiguration config) {
    final CantoServiceConnection connection = CantoServiceConnection.fromConfig(config);
    if (!apiConnectionPool.containsKey(connection.getConnectionId())) {

      final CantoApi cantoApi = new CantoApi.Builder()
          .tenant(config.getTenant())
          .oAuthBaseUrl(config.getOAuthBaseUrl())
          .appId(config.getAppId())
          .appSecret(config.getAppSecret())
          .userId(config.getUserId())
          .singleFetchRequestLimiter(singleFetchRequestLimiter)
          .batchFetchRequestLimiter(batchFetchRequestLimiter)
          .searchRequestLimiter(searchRequestLimiter)
          .projectBoundCacheAccess(new ProjectBoundCacheAccess(centralCache))
          .rateLimitRetryCount(serviceConfiguration.rateLimitRetryCount)
          .build();

      apiConnectionPool.put(connection.getConnectionId(), cantoApi);

      Logging.logInfo("[getServiceConnection] New Connection for Configuration: " + config + ". Created ConnectionId: " + connection.getConnectionId() + ". ApiPoolSize=" + apiConnectionPool.size(), this.getClass());
    }

    return connection;
  }

  @Override public void removeServiceConnection(@Nullable CantoServiceConnection connection) {
    if (connection != null) {
      apiConnectionPool.remove(connection.getConnectionId());
    }
  }

  @Nullable private CantoApi getApiInstance(final CantoServiceConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Requested Api Instance with null Connection");
    }
    CantoApi cantoApi = apiConnectionPool.get(connection.getConnectionId());
    if (cantoApi == null) {
      Logging.logWarning("Requested Api Instance not found for ConnectionId: " + connection.getConnectionId() + "\n" + "This issue may be resolved by restarting your clients and Service", this.getClass());
    }
    return cantoApi;
  }

  @Nullable @Override public List<@Nullable CantoAssetDTO> fetchAssetsByIdentifiers(@NotNull final CantoServiceConnection connection, @NotNull final List<CantoAssetIdentifier> identifiers) {
    Logging.logDebug("[fetchAssetsByIdentifiers] " + Strings.implode(identifiers, ", "), getClass());
    final CantoApi cantoApi = getApiInstance(connection);

    if (cantoApi == null) {
      // Connection is invalid. Return null, caller can try to revalidate Connection
      return null;
    }

    return cantoApi.fetchAssets(identifiers)
        .stream()
        .map(CantoAssetDTOFactory::fromAsset)
        .toList();
  }


  @Nullable @Override public CantoSearchResultDTO fetchSearch(@NotNull final CantoServiceConnection connection, @NotNull final CantoSearchParams params) {
    Logging.logDebug("[fetchSearch] " + params, getClass());
    final CantoApi cantoApi = getApiInstance(connection);

    if (cantoApi == null) {
      // Connection is invalid. Return null, caller can try to revalidate Connection
      return null;
    }

    final CantoSearchResult cantoSearchResult = cantoApi.fetchSearch(params);
    return CantoSearchResultDTOFactory.fromCantoSearchResult(params, cantoSearchResult);
  }

  @Nullable public String fetchFolderStructure(@NotNull final CantoServiceConnection connection) {
    final CantoApi cantoApi = getApiInstance(connection);

    if (cantoApi == null) {
      // Connection is invalid. Return null, caller can try to revalidate Connection
      return null;
    }

    return cantoApi.fetchFolderStructure();
  }

  @Override
  @NotNull public CantoConfiguration getConfiguration( final long projectId) {
    BrokerAgent brokerAgent = serverEnvironment.getBroker().requireSpecialist(BrokerAgent.TYPE);
    SpecialistsBroker brokerByProjectId = brokerAgent.getBrokerByProjectId(projectId);
    return CantoConfigurationFactory.fromProjectBroker(brokerByProjectId, serviceConfiguration.apiTenant, serviceConfiguration.apiOAuthBaseUrl, serviceConfiguration.apiAppId, serviceConfiguration.apiAppSecret);
  }

  @Override public void start() {
    apiConnectionPool = new HashMap<>();

    this.serviceConfiguration = ServiceConfiguration.fromServerEnvironment(serverEnvironment);

    singleFetchRequestLimiter = serviceConfiguration.useSingleFetchRequestLimiter
        ? new RequestLimiter(serviceConfiguration.singleFetchMaxRequestsPerMinute, serviceConfiguration.singleFetchRequestsWithoutDelay, serviceConfiguration.timeBufferInMs)
        : null;
    batchFetchRequestLimiter = serviceConfiguration.useBatchFetchRequestLimiter
        ? new RequestLimiter(serviceConfiguration.batchFetchMaxRequestsPerMinute, serviceConfiguration.batchFetchRequestsWithoutDelay, serviceConfiguration.timeBufferInMs)
        : null;
    searchRequestLimiter = serviceConfiguration.useSearchRequestLimiter
        ? new RequestLimiter(serviceConfiguration.searchMaxRequestsPerMinute, serviceConfiguration.searchRequestsWithoutDelay, serviceConfiguration.timeBufferInMs)
        : null;

    if (serviceConfiguration.useCache) {
      CantoApi cantoApi = getCantoApi();
      centralCache = new CentralCache(cantoApi, serviceConfiguration.cacheSize, serviceConfiguration.cacheUpdateTimespanMs, serviceConfiguration.cacheUpdateTimespanMs, serviceConfiguration.cacheItemInUseTimespanMs, serviceConfiguration.batchUpdateSize);
      loadPersistedCache();
    } else {
      centralCache = null;
    }

    Logging.logInfo("[start] CantoSaasServerService started. Using Configuration: " + serviceConfiguration, this.getClass());
  }

  private @Nullable CantoApi getCantoApi() {
    CantoApi cantoApi = null;
    if (!serviceConfiguration.apiTenant.isBlank() && !serviceConfiguration.apiOAuthBaseUrl.isBlank() && !serviceConfiguration.apiAppId.isBlank() && !serviceConfiguration.apiAppSecret.isBlank() && !serviceConfiguration.apiUserId.isBlank()) {

      cantoApi = new CantoApi.Builder()
          .tenant(serviceConfiguration.apiTenant)
          .oAuthBaseUrl(serviceConfiguration.apiOAuthBaseUrl)
          .appId(serviceConfiguration.apiAppId)
          .appSecret(serviceConfiguration.apiAppSecret)
          .userId(serviceConfiguration.apiUserId)
          .singleFetchRequestLimiter(singleFetchRequestLimiter)
          .batchFetchRequestLimiter(batchFetchRequestLimiter)
          .searchRequestLimiter(searchRequestLimiter)
          .projectBoundCacheAccess(new ProjectBoundCacheAccess(null))
          .timeoutInSeconds(50)
          .rateLimitRetryCount(serviceConfiguration.rateLimitRetryCount)
          .build();
    }
    return cantoApi;
  }

  @Override public void stop() {
    apiConnectionPool = null;

    if (centralCache != null) {
      persistCache();
      centralCache.shutdown();
    }
    centralCache = null;

    batchFetchRequestLimiter = null;
    singleFetchRequestLimiter = null;
    searchRequestLimiter = null;

    Logging.logInfo("[stop] CantoSaasServerService stopped", this.getClass());
  }

  private void persistCache() {
    if (centralCache == null) {
      return;
    }
    try {
      List<CachePersistenceEntry> entries = centralCache.getEntriesForPersistence();

      FileHandle cacheFile = obtainDataFileHandle(CACHE_FILE_NAME);
      try (OutputStream out = cacheFile.getOutputStream(false)) {
        out.write(CACHE_ADAPTER.toJson(entries).getBytes(StandardCharsets.UTF_8));
      }
      Logging.logInfo("[persistCache] Persisted " + entries.size() + " cache entries to " + cacheFile.getPath(), getClass());
    } catch (Exception e) {
      Logging.logWarning("[persistCache] Failed to persist cache", e, getClass());
    }
  }

  private void loadPersistedCache() {
    if (centralCache == null) {
      return;
    }
    try {
      FileHandle cacheFile = obtainDataFileHandle(CACHE_FILE_NAME);
      if (!cacheFile.exists() || !cacheFile.isFile()) {
        Logging.logInfo("[loadPersistedCache] No persisted cache file found, starting with empty cache.", getClass());
        return;
      }
      try (InputStream in = cacheFile.load()) {
        String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        List<CachePersistenceEntry> entries = CACHE_ADAPTER.fromJson(json);
        if (entries != null) {
          centralCache.loadPersistedEntries(entries);
        }
      }
    } catch (Exception e) {
      Logging.logWarning("[loadPersistedCache] Failed to load persisted cache, starting with empty cache", e, getClass());
    }
  }

  @SuppressWarnings("unchecked")
  private FileHandle obtainDataFileHandle(String name) throws IOException {
    return ((de.espirit.firstspirit.io.FileSystem<FileHandle>) serverEnvironment.getDataDir()).obtain(name);
  }

  @Override public boolean isRunning() {
    return apiConnectionPool != null;
  }

  @Override public @NotNull Class<? extends CantoSaasService> getServiceInterface() {
    return CantoSaasService.class;
  }

  @Override public Class<? extends ServiceProxy<CantoSaasService>> getProxyClass() {
    return null;
  }

  @Override public void init(final ServiceDescriptor serviceDescriptor, final ServerEnvironment serverEnvironment) {
    this.serverEnvironment = serverEnvironment;
    this.serviceDescriptor = serviceDescriptor;
  }

  @Override public void installed() {
    // load default settings
    CantoSaasServiceConfigurable cantoSaasServiceConfigurable = new CantoSaasServiceConfigurable();
    cantoSaasServiceConfigurable.init(serviceDescriptor.getModuleName(), serviceDescriptor.getName(), serverEnvironment);
    cantoSaasServiceConfigurable.load();
    cantoSaasServiceConfigurable.store();
  }

  @Override public void uninstalling() {
    // stub
  }

  @Override public void updated(final String s) {
    installed();
  }

  @Override
  public void logCacheStatus() {
    if (centralCache != null) {
      centralCache.logCacheStatus();
    }
  }
}
