package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.util.UrlHelper;
import org.jetbrains.annotations.Nullable;

public class CantoAssetDTOFactory {

  public static @Nullable CantoAssetDTO fromAsset(@Nullable CantoAsset asset) {
    if (asset == null) {
      return null;
    }
    try {
      return new CantoAssetDTO(asset.getId(),
                               asset.getName(),
                               UrlHelper.removeLastUrlPathPart(asset.getCantoUrls()
                                                                   .getDirectUrlPreview()),
                               asset.getCantoUrls()
                                   .getDirectUrlOriginal(),
                               asset.getScheme(),
                               asset.getDescription(),
                               asset.getWidth(),
                               asset.getHeight(),
                               asset.getSize(),
                               asset.getCopyright(),
                               asset.getFileExtension(),
                               asset.getAdditional());
    } catch (Exception e) {
      throw new RuntimeException("Unable to create DTO Object for " + asset, e);
    }

  }
}
