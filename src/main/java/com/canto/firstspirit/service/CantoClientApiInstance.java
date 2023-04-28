package com.canto.firstspirit.service;

import com.canto.firstspirit.integration.dap.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.server.*;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import java.util.Collection;
import java.util.List;

public class CantoClientApiInstance {
    private final CantoSaasServerService service;
    private final CantoServiceConnection connection;

    private CantoClientApiInstance(CantoSaasServerService service, CantoServiceConnection connection) {

        this.service = service;
        this.connection = connection;
    }

    /**
     * get (or create) an instance to be used on client side
     *
     * @param broker project bound broker
     * @return CantoClientApi Instance based on ProjectApp Configuration
     */
    public static CantoClientApiInstance fromProjectBroker(SpecialistsBroker broker) {
        //todo: error handling
        CantoConfiguration cantoConfiguration = CantoConfigurationFactory.fromProjectBroker(broker);
        CantoSaasServerService service = broker.requireSpecialist(ServicesBroker.TYPE).getService(CantoSaasServerService.class);
        CantoServiceConnection connection = service.getConnection(cantoConfiguration);
        return new CantoClientApiInstance(service, connection);
    }

    public List<CantoAssetDTO> getAssets(Collection<String> identifiers) {
        return service.getAssetDTOs(connection, identifiers);
    }

    public CantoSearchResultDTO findAssets (CantoSearchParams cantoSearchParams) {
        return service.findAssetDTOs(connection, cantoSearchParams);
    }


}
