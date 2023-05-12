package com.canto.firstspirit.service.server;


import com.canto.firstspirit.service.server.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CantoSaasService {
    /**
     * Fetches Assets via Canto Api and returns result
     * Preserves Order of Incoming Ids and matching identifiers
     * If Assets have not been found, null values are inserted in their place
     * @param connection Project Specific Connection to fetch Data with
     * @param identifiers List of Identifiers to fetch
     * @return List of AssetDTOs. May contain null Values
     */
    List<@Nullable CantoAssetDTO> fetchAssetDTOs(@NotNull final CantoServiceConnection connection, @NotNull final List<String> identifiers);
    CantoSearchResultDTO fetchSearchAssetDTOs(@NotNull final CantoServiceConnection connection, @NotNull final CantoSearchParams params);

    CantoServiceConnection getConnection(@NotNull final CantoConfiguration config);

}
