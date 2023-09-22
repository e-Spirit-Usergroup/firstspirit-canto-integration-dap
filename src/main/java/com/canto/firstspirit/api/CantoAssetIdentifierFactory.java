package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;

public class CantoAssetIdentifierFactory {

  public static CantoAssetIdentifier fromCantoAsset(CantoAsset asset) {
    return new CantoAssetIdentifier(asset.getScheme(), asset.getId());
  }


  public static CantoAssetIdentifier fromCantoAssetDTO(CantoAssetDTO dto) {
    return new CantoAssetIdentifier(dto.getSchema(), dto.getId());
  }

  public static CantoAssetIdentifier fromPath(String path) {
    String[] parts = path.split("/");
    return new CantoAssetIdentifier(parts[0], parts[1]);
  }

}
