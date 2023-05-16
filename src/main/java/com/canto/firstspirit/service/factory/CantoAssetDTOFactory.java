package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.CantoAssetUtils;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import org.jetbrains.annotations.Nullable;

public class CantoAssetDTOFactory {

    public static @Nullable CantoAssetDTO fromAsset(@Nullable CantoAsset asset) {
        if(asset == null) return null;

        return new CantoAssetDTO(
                asset.getId(),
                asset.getName(),
                CantoAssetUtils.getThumbnailUrl(asset),
                CantoAssetUtils.getPreviewUrl(asset),
                asset.getScheme(),
                asset.getDescription(),
                asset.getAdditional()
        );
    }
}
