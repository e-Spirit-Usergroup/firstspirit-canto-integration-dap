package com.canto.firstspirit.integration.dap.model;

import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import org.jetbrains.annotations.NotNull;

public class CantoDAPAsset {

    private final String _title;
    private final String _thumbnailUrl;
    private final String _previewUrl;
    private final String _description;
    private final CantoAssetPath _path;
    private final String _mdc_rendition_baseurl;
    private final String _mdc_asset_baseurl;


    @NotNull
    public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
        //todo: handle dummy case
        return new CantoDAPAsset(cantoAssetDTO.getSchema(), cantoAssetDTO.getId(), cantoAssetDTO.getName(), cantoAssetDTO.getThumbnailUrl(), cantoAssetDTO.getPreviewUrl(), cantoAssetDTO.getDescription(), cantoAssetDTO.getMDCRenditionBaseUrl(), cantoAssetDTO.getMDCAssetBaseUrl());
    }


    public CantoDAPAsset(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description, String mdc_rendition_baseurl, String mdc_asset_baseurl) {
        _path = new CantoAssetPath(schema, identifier);
        _title = title;
        _thumbnailUrl = thumbnailUrl;
        _previewUrl = previewUrl;
        _description = description;
        _mdc_rendition_baseurl = mdc_rendition_baseurl;
        _mdc_asset_baseurl = mdc_asset_baseurl;
    }

    public String getDescription() {
        return _description;
    }

    public String getIdentifier() {
        return _path.getIdentifier();
    }

    public String getPath() {
        return _path.getPath();
    }

    public String getTitle() {
        return _title;
    }

    public String getThumbnailUrl() {
        return _thumbnailUrl;
    }

    public String getPreviewUrl() {
        return _previewUrl;
    }

    public String getMDCRenditionBaseUrl() {
        return _mdc_rendition_baseurl;
    }

    public String getMDCAssetBaseUrl() {
        return _mdc_asset_baseurl;
    }
}
