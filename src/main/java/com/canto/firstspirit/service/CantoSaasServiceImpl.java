package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.factory.CantoAssetDTOFactory;
import com.canto.firstspirit.service.factory.CantoSearchResultDTOFactory;
import com.canto.firstspirit.service.server.*;
import com.canto.firstspirit.service.server.model.*;
import com.espirit.moddev.components.annotations.ServiceComponent;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@ServiceComponent(name = "CantoSaasService", displayName = "CantoSaaS Connector Service")
public class CantoSaasServiceImpl implements CantoSaasService, Service<CantoSaasService> {

    private Map<Integer, CantoApi> apiConnectionPool;

    public CantoServiceConnection getConnection(@NotNull final CantoConfiguration config) {
        final CantoServiceConnection connection = CantoServiceConnection.fromConfig(config);
        if (!apiConnectionPool.containsKey(connection.getConnectionId())) {
            final CantoApi cantoApi = new CantoApi(config.getTenant(), config.getToken(), config.getMDCDomain(), config.getMDCAccountId());

            apiConnectionPool.put(connection.getConnectionId(), cantoApi);

            Logging.logInfo("New Connection requested for Tenant: " + config.getTenant() + ". Created ConnectionId: " + connection.getConnectionId() + ". ApiPoolSize=" + apiConnectionPool.size(), this.getClass());


        }
        return connection;
    }
    @NotNull
    private CantoApi getApiInstance(final CantoServiceConnection connection) {
        if(connection == null) throw new IllegalArgumentException("Requested Api Instance with null Connection");
        CantoApi cantoApi = apiConnectionPool.get(connection.getConnectionId());
        if(cantoApi == null) {
            throw new IllegalStateException("Requested Api Instance not found for ConnectionId: " + connection.getConnectionId());
        }
        return cantoApi;
    }

    @NotNull
    @Override
    public List<@Nullable CantoAssetDTO> fetchAssetDTOs(@NotNull final CantoServiceConnection connection, @NotNull final List<String> identifiers) {
        Logging.logInfo("getAssetDTO in Service with " + Strings.implode(identifiers, ", "), getClass());
        final CantoApi cantoApi = getApiInstance(connection);
        return cantoApi.fetchAssets(identifiers)
                .stream()
                .map(asset -> CantoAssetDTOFactory.fromAsset(asset, cantoApi))
                .collect(Collectors.toList());
    }

    @Override
    public CantoSearchResultDTO fetchSearchAssetDTOs(@NotNull final CantoServiceConnection connection, @NotNull final CantoSearchParams params) {
        Logging.logInfo("CantoSearch in Service with " + params, getClass());
        final CantoApi cantoApi = getApiInstance(connection);

        final CantoSearchResult cantoSearchResult = cantoApi.fetchSearch(params.getKeyword());
        //todo: implement paging

        return CantoSearchResultDTOFactory.fromCantoSearchResult(params, cantoSearchResult, cantoApi);
    }

    @Override
    public void start() {
        //todo: initialize
        apiConnectionPool = new HashMap<>();
        Logging.logInfo("CantoSaasServerService started", this.getClass());
    }

    @Override
    public void stop() {
        //apiConnectionPool.forEach((key, value) -> value.close());
        apiConnectionPool = null;
        Logging.logInfo("CantoSaasServerService stopped", this.getClass());
    }

    @Override
    public boolean isRunning() {
        return apiConnectionPool != null;
    }

    @Override
    public Class<? extends CantoSaasService> getServiceInterface() {
        return CantoSaasService.class;
    }

    @Override
    public Class<? extends ServiceProxy<CantoSaasService>> getProxyClass() {
        return null;
    }

    @Override
    public void init(final ServiceDescriptor serviceDescriptor, final ServerEnvironment serverEnvironment) {
		// stub
    }

    @Override
    public void installed() {
		// stub
    }

    @Override
    public void uninstalling() {
		// stub
    }

    @Override
    public void updated(final String s) {
		// stub
    }
}
