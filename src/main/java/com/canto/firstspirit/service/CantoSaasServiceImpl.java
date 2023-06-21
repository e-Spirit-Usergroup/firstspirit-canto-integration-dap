package com.canto.firstspirit.service;

import static com.canto.firstspirit.service.CantoSaasServiceImpl.SERVICE_NAME;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.factory.CantoAssetDTOFactory;
import com.canto.firstspirit.service.factory.CantoSearchResultDTOFactory;
import com.canto.firstspirit.service.server.CantoSaasService;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.service.server.model.CantoServiceConnection;
import com.espirit.moddev.components.annotations.ServiceComponent;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ServiceComponent(name = SERVICE_NAME, displayName = "CantoSaaS Connector Service")
public class CantoSaasServiceImpl implements CantoSaasService, Service<CantoSaasService> {

  public static final String SERVICE_NAME = "CantoSaasService";
  private Map<Integer, CantoApi> apiConnectionPool;

  public CantoServiceConnection getServiceConnection(@NotNull final CantoConfiguration config) {
    final CantoServiceConnection connection = CantoServiceConnection.fromConfig(config);
    if (!apiConnectionPool.containsKey(connection.getConnectionId())) {

      final CantoApi cantoApi = new CantoApi(config.getTenant(),
                                             config.getOAuthBaseUrl(),
                                             config.getAppId(),
                                             config.getAppSecret(),
                                             config.getUserId());

      apiConnectionPool.put(connection.getConnectionId(), cantoApi);

      Logging.logInfo(
          "[getServiceConnection] New Connection for Tenant: " + config.getTenant() + ". Created ConnectionId: " + connection.getConnectionId()
              + ". ApiPoolSize=" + apiConnectionPool.size(), this.getClass());
    }

    return connection;
  }

  @NotNull private CantoApi getApiInstance(final CantoServiceConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Requested Api Instance with null Connection");
    }
    CantoApi cantoApi = apiConnectionPool.get(connection.getConnectionId());
    if (cantoApi == null) {
      throw new IllegalStateException("Requested Api Instance not found for ConnectionId: " + connection.getConnectionId() + "\n"
                                          + "This issue may be resolved by restarting your clients and Service");
    }
    return cantoApi;
  }

  @NotNull @Override public List<@Nullable CantoAssetDTO> fetchAssetsByIdentifiers(@NotNull final CantoServiceConnection connection,
      @NotNull final List<CantoAssetIdentifier> identifiers) {
    Logging.logInfo("[fetchAssetsByIdentifiers] " + Strings.implode(identifiers, ", "), getClass());
    final CantoApi cantoApi = getApiInstance(connection);
    return cantoApi.fetchAssets(identifiers)
        .stream()
        .map(CantoAssetDTOFactory::fromAsset)
        .collect(Collectors.toList());
  }

  @Override public CantoSearchResultDTO fetchSearch(@NotNull final CantoServiceConnection connection, @NotNull final CantoSearchParams params) {
    Logging.logInfo("[fetchSearch] " + params, getClass());
    final CantoApi cantoApi = getApiInstance(connection);

    final CantoSearchResult cantoSearchResult = cantoApi.fetchSearch(params);
    return CantoSearchResultDTOFactory.fromCantoSearchResult(params, cantoSearchResult);
  }

  @Override public void start() {
    apiConnectionPool = new HashMap<>();
    Logging.logInfo("[start] CantoSaasServerService started", this.getClass());
  }

  @Override public void stop() {
    //apiConnectionPool.forEach((key, value) -> value.close());
    apiConnectionPool = null;
    Logging.logInfo("[stop] CantoSaasServerService stopped", this.getClass());
  }

  @Override public boolean isRunning() {
    return apiConnectionPool != null;
  }

  @Override public Class<? extends CantoSaasService> getServiceInterface() {
    return CantoSaasService.class;
  }

  @Override public Class<? extends ServiceProxy<CantoSaasService>> getProxyClass() {
    return null;
  }

  @Override public void init(final ServiceDescriptor serviceDescriptor, final ServerEnvironment serverEnvironment) {
    // stub
  }

  @Override public void installed() {
    // stub
  }

  @Override public void uninstalling() {
    // stub
  }

  @Override public void updated(final String s) {
    // stub
  }
}
