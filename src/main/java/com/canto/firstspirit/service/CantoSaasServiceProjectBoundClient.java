package com.canto.firstspirit.service;

import com.canto.firstspirit.service.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.server.CantoSaasService;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.service.server.model.CantoServiceConnection;
import de.espirit.common.base.Logging;
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
  private CantoServiceConnection connection;

  @NotNull
  private final SpecialistsBroker broker;

  /**
   * Create ProjectBound Proxy to CantoServer Service, that manages the configuration from the ProjectApp
   *
   * @param broker project bound broker
   */
  public CantoSaasServiceProjectBoundClient(@NotNull SpecialistsBroker broker) {
    try {
      this.broker = broker;
      this.service = broker.requireSpecialist(ServicesBroker.TYPE)
          .getService(CantoSaasService.class);
      this.connection = getOrCreateConnection(false);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to create ProjectBoundClient from given broker " + broker, e);
    }
  }

  private CantoServiceConnection getOrCreateConnection(boolean forceNewConnection) {
    CantoConfiguration cantoConfiguration = CantoConfigurationFactory.fromProjectBroker(broker);

    if (forceNewConnection) {
      service.removeServiceConnection(connection);
      Logging.logInfo("Force creation of new Connection for Configuration " + cantoConfiguration, this.getClass());
    }

    return service.getServiceConnection(cantoConfiguration);

  }

  public List<@Nullable CantoAssetDTO> fetchAssetDTOs(List<CantoAssetIdentifier> identifiers) {
    List<@Nullable CantoAssetDTO> cantoAssetDTOS = service.fetchAssetsByIdentifiers(connection, identifiers);

    if (cantoAssetDTOS == null) {
      // null Result indicates invalid connection. Revalidate and try again once
      this.connection = getOrCreateConnection(true);
      cantoAssetDTOS = service.fetchAssetsByIdentifiers(connection, identifiers);
    }
    return cantoAssetDTOS;
  }

  public CantoSearchResultDTO fetchSearch(CantoSearchParams cantoSearchParams) {
    CantoSearchResultDTO cantoSearchResultDTO = service.fetchSearch(connection, cantoSearchParams);
    if (cantoSearchResultDTO == null) {
      // null Result indicates invalid connection. Revalidate and try again once
      this.connection = getOrCreateConnection(true);
      cantoSearchResultDTO = service.fetchSearch(connection, cantoSearchParams);
    }
    return cantoSearchResultDTO;

  }

  public String getUserScope() {
    String userScopeString = service.getUserScope(connection);

    if (userScopeString == null) {
      // null Result indicates invalid connection. Revalidate and try again once
      this.connection = getOrCreateConnection(true);
      userScopeString = service.getUserScope(connection);
    }
    return userScopeString;
  }

  public String fetchFolderStructure() {
    String folderStructureString = service.fetchFolderStructure(connection);

    if (folderStructureString == null) {
      // null Result indicates invalid connection. Revalidate and try again once
      this.connection = getOrCreateConnection(true);
      folderStructureString = service.fetchFolderStructure(connection);
    }
    return folderStructureString;
  }

}
