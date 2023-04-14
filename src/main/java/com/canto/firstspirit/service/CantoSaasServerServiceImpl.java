package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.espirit.moddev.components.annotations.ServiceComponent;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@ServiceComponent(name = "CantoSaasServerService", displayName = "CantoSaaS Connector Service")
public class CantoSaasServerServiceImpl implements CantoSaasServerService, Service<CantoSaasServerService> {

    private Map<CantoServiceConnection, CantoApi> apiConnectionPool;

    public CantoServiceConnection getConnection(final CantoConfiguration config) {
        final CantoServiceConnection connection = CantoServiceConnectionImpl.fromConfig(config);
        if (!apiConnectionPool.containsKey(connection)) {
            final CantoApi cantoApi = new CantoApi(config.getTenant(), config.getToken(), config.getMDCDomain(), config.getMDCAccountId());

            apiConnectionPool.put(connection, cantoApi);
        }
        return connection;
    }

    private CantoApi getApiInstance(final CantoServiceConnection connection){
        return Optional.ofNullable(apiConnectionPool.get(connection)).orElseThrow(()->new IllegalStateException("Requested connection not found."));
    }

    @NotNull
    @Override
    public List<CantoAssetDTO> getAssetDTOs(final CantoServiceConnection connection, final Collection<String> identifiers) {
        Logging.logInfo("getAssetDTO in Service with " + Strings.implode(identifiers, ", "), getClass());
        final CantoApi cantoApi = getApiInstance(connection);
        return cantoApi.getAssets(identifiers).stream().map(asset -> CantoAssetDTOImpl.fromAsset(asset, cantoApi))
                .collect(Collectors.toList());
    }

    @Override
    public CantoSearchResultDTO findAssetDTOs(final CantoServiceConnection connection, final CantoSearchParams params) {
        Logging.logInfo("CantoSearch in Service with " + params, getClass());
        final CantoApi cantoApi = getApiInstance(connection);

        final CantoSearchResult cantoSearchResult = cantoApi.search(params.getKeyword());
        //todo: implement paging

        return CantoSearchResultDTOImpl.fromCantoSearchResult(params, cantoSearchResult, cantoApi);
    }

    @Override
    public void start() {
        //todo: initialize
        apiConnectionPool = new HashMap<>();
    }

    @Override
    public void stop() {
        //apiConnectionPool.forEach((key, value) -> value.close());
        apiConnectionPool = null;
    }

    @Override
    public boolean isRunning() {
        return apiConnectionPool!=null;
    }

    @Override
    public Class<? extends CantoSaasServerService> getServiceInterface() {
        return CantoSaasServerService.class;
    }

    @Override
    public Class<? extends ServiceProxy<CantoSaasServerService>> getProxyClass() {
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
