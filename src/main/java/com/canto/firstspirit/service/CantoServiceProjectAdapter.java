package com.canto.firstspirit.service;

import com.canto.firstspirit.service.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.server.*;
import com.canto.firstspirit.service.server.model.*;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CantoServiceProjectAdapter {
    @NotNull
    private final CantoSaasServerService service;

    @NotNull
    private final CantoServiceConnection connection;

    private CantoServiceProjectAdapter(@Nullable CantoSaasServerService service, @Nullable CantoServiceConnection connection) {
        if(service == null || connection == null) {
            throw new IllegalArgumentException("Null Arguments passed: "
                    + (service == null ? "service is null. " : "")
                    + (connection == null ? "connection is null. " : ""));
        }
        this.service = service;
        this.connection = connection;
    }

    /**
     * get (or create) an instance to be used on client side
     *
     * @param broker project bound broker
     * @return CantoClientApi Instance based on ProjectApp Configuration
     */
    public static CantoServiceProjectAdapter fromProjectBroker(SpecialistsBroker broker) {
        //todo: error handling
        CantoConfiguration cantoConfiguration = CantoConfigurationFactory.fromProjectBroker(broker);
        CantoSaasServerService service = broker.requireSpecialist(ServicesBroker.TYPE).getService(CantoSaasServerService.class);
        CantoServiceConnection connection = service.getConnection(cantoConfiguration);
        return new CantoServiceProjectAdapter(service, connection);
    }

    public List<@Nullable CantoAssetDTO> getAssets(List<String> identifiers) {
        return service.fetchAssetDTOs(connection, identifiers);
    }

    public CantoSearchResultDTO findAssets (CantoSearchParams cantoSearchParams) {
        return service.fetchSearchAssetDTOs(connection, cantoSearchParams);
    }


}
