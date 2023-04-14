package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoAsset;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class CantoAssetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String thumbnailUrl;
    private String previewUrl;
    private String schema;
    private String description;
    private String mdcAssetBaseUrl;
    private String mdcRenditionBaseUrl;


    private CantoAssetDTO(){}

    static CantoAssetDTO fromAsset(@NotNull CantoAsset asset, @NotNull CantoApi cantoApi) {
        CantoAssetDTO assetDTO = new CantoAssetDTO();
        assetDTO.id = asset.getId();
        assetDTO.name = asset.getName();
        assetDTO.previewUrl = cantoApi.getPreviewImageUrl(asset);
        assetDTO.thumbnailUrl = cantoApi.getPreviewImageUrl(asset);
        assetDTO.schema = asset.getScheme();
        assetDTO.description = asset.getDescription();
        assetDTO.mdcAssetBaseUrl = cantoApi.getMDCAssetBaseUrl(asset);
        assetDTO.mdcRenditionBaseUrl = cantoApi.getMDCRenditionBaseUrl(asset);

        return assetDTO;
    }

    public String getDescription() {
        return description;
    }

    public String getSchema() {
        return schema;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
    public String getMDCAssetBaseUrl() {
        return mdcAssetBaseUrl;
    }
    public String getMDCRenditionBaseUrl() {
        return mdcRenditionBaseUrl;
    }
}