package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.CantoAssetDTO;
import org.jetbrains.annotations.NotNull;

public class CantoAssetDTOImpl implements CantoAssetDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String thumbnailUrl;
    private String previewUrl;
    private String schema;
    private String description;
    private String mdcAssetBaseUrl;
    private String mdcRenditionBaseUrl;


    private CantoAssetDTOImpl(){}

    static CantoAssetDTO fromAsset(@NotNull CantoAsset asset, @NotNull CantoApi cantoApi) {
        CantoAssetDTOImpl assetDTO = new CantoAssetDTOImpl();
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

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public String getPreviewUrl() {
        return previewUrl;
    }
    @Override
    public String getMDCAssetBaseUrl() {
        return mdcAssetBaseUrl;
    }
    @Override
    public String getMDCRenditionBaseUrl() {
        return mdcRenditionBaseUrl;
    }
}