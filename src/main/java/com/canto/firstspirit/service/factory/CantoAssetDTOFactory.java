package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.CantoAssetDTO;
import org.jetbrains.annotations.NotNull;

public class CantoAssetDTOFactory {

    public static CantoAssetDTO fromAsset(@NotNull CantoAsset asset, @NotNull CantoApi cantoApi) {

        return new CantoAssetDTO(
                asset.getId(),
                asset.getName(),
                cantoApi.getPreviewImageUrl(asset),
                cantoApi.getPreviewImageUrl(asset),
                asset.getScheme(),
                asset.getDescription(),
                cantoApi.getMDCAssetBaseUrl(asset),
                cantoApi.getMDCRenditionBaseUrl(asset)

        );
    }
}
