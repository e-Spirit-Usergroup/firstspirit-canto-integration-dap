package com.canto.firstspirit.service;

import com.canto.firstspirit.service.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.server.CantoSaasService;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.service.server.model.CantoServiceConnection;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Simple Client to use the CantoSaasService in a project. Binds all Call made with this client to the current Project
 */
public class CantoSaasServiceProjectBoundClient {

  @NotNull
  private final CantoSaasService service;

  @NotNull
  private final CantoServiceConnection connection;

  private CantoSaasServiceProjectBoundClient(@Nullable CantoSaasService service, @Nullable CantoServiceConnection connection) {
    if (service == null || connection == null) {
      throw new IllegalArgumentException(
          "Null Arguments passed: " + (service == null ? "service is null. " : "") + (connection == null ? "connection is null. " : ""));
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
  public static CantoSaasServiceProjectBoundClient fromProjectBroker(SpecialistsBroker broker) {
    //todo: error handling
    CantoConfiguration cantoConfiguration = CantoConfigurationFactory.fromProjectBroker(broker);
    CantoSaasService service = broker.requireSpecialist(ServicesBroker.TYPE)
        .getService(CantoSaasService.class);
    CantoServiceConnection connection = service.getServiceConnection(cantoConfiguration);
    return new CantoSaasServiceProjectBoundClient(service, connection);
  }

  public List<@Nullable CantoAssetDTO> fetchAssetDTOs(List<CantoAssetIdentifier> identifiers) {
    return service.fetchAssetsByIdentifiers(connection, identifiers);
  }

  public CantoSearchResultDTO fetchSearch(CantoSearchParams cantoSearchParams) {
    return service.fetchSearch(connection, cantoSearchParams);
  }

}
