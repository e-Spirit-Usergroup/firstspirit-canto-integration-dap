package com.canto.firstspirit.service.server;


import java.util.Collection;
import java.util.List;

public interface CantoSaasServerService {

    List<CantoAssetDTO> getAssetDTOs(CantoServiceConnection connection, Collection<String> identifiers);
    CantoSearchResultDTO findAssetDTOs(CantoServiceConnection connection, CantoSearchParams params);

    CantoServiceConnection getConnection(final CantoConfiguration config);

}
