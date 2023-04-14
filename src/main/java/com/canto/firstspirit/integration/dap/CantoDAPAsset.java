package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.service.CantoAssetDTO;
import org.jetbrains.annotations.NotNull;


public interface CantoDAPAsset {
    int THUMBNAIL_WIDTH = 100;

    String getIdentifier();
    String getPath();
    String getTitle();
    String getThumbnailUrl();
    String getPreviewUrl();
    String getDescription();
    String getMDCAssetBaseUrl();
    String getMDCRenditionBaseUrl();


    @NotNull
    static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAsset) {
        //todo: handle dummy case
        return new CantoDAPAssetImpl(cantoAsset.getSchema(), cantoAsset.getId(), cantoAsset.getName(), cantoAsset.getThumbnailUrl(), cantoAsset.getPreviewUrl(), cantoAsset.getDescription(), cantoAsset.getMDCRenditionBaseUrl(), cantoAsset.getMDCAssetBaseUrl());
    }

}
