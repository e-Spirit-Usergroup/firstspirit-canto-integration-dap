package com.canto.firstspirit.service;


import java.util.Collection;
import java.util.List;

public interface CantoSaasServerService {


    CantoServiceConnection getConnection(CantoConfiguration config);

    List<CantoAssetDTO> getAssetDTOs(CantoServiceConnection connection, Collection<String> identifiers);
    CantoSearchResultDTO findAssetDTOs(CantoServiceConnection connection, CantoSearchParams params);


}
