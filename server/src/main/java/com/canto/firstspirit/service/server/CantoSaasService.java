package com.canto.firstspirit.service.server;


import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.service.server.model.CantoServiceConnection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CantoSaasService {

  /**
   * Fetches Assets via Canto Api and returns result Preserves Order of Incoming Ids and matching identifiers If Assets have not been found, null values are inserted in their place
   *
   * @param connection  Project Specific Connection to fetch Data with
   * @param identifiers List of Identifiers to fetch of form {scheme}/{cantoId}
   * @return List of AssetDTOs, may contain null Values. Null Response indicates invalid connection
   */
  @Nullable List<@Nullable CantoAssetDTO> fetchAssetsByIdentifiers(@NotNull final CantoServiceConnection connection, @NotNull final List<CantoAssetIdentifier> identifiers);

  /**
   * Searches for Assets based on passed SearchParams
   *
   * @param connection Project Specific Connection to fetch Data with
   * @param params     SearchParams that define a keyword
   * @return SearchResult based on params. Null Response indicates invalid connection
   */
  @Nullable CantoSearchResultDTO fetchSearch(@NotNull final CantoServiceConnection connection, @NotNull final CantoSearchParams params);

  /**
   * get a Connection for fetching Assets and performing searches. the passed CantoConfig defines the cantoApi bound to this connection.
   *
   * @param config CantoConfig
   * @return cantoServiceConnetion to be used with {@link #fetchAssetsByIdentifiers(CantoServiceConnection, List)} and {@link #fetchSearch(CantoServiceConnection, CantoSearchParams)}
   */
  CantoServiceConnection getServiceConnection(@NotNull final CantoConfiguration config);

  /**
   * remove a Connection and corresponding cantoApi
   *
   * @param connection Connection to be removed
   */
  void removeServiceConnection(@NotNull final CantoServiceConnection connection);

  @Nullable String fetchUserScope(CantoServiceConnection connection, String userId);
}
