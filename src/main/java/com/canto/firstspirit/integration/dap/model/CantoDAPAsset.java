package com.canto.firstspirit.integration.dap.model;

import com.canto.firstspirit.integration.dap.custom.AdditionalDataHandler;
import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class CantoDAPAsset {

    private final String _title;
    private final String _thumbnailUrl;
    private final String _previewUrl;
    private final String _description;
    private final CantoAssetIdentifier _assetIdentifier;
    private final String _mdc_rendition_baseurl;
    private final String _mdc_asset_baseurl;


    @NotNull
    public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
        //todo: handle dummy case
        return new CantoDAPAsset(cantoAssetDTO.getSchema(), cantoAssetDTO.getId(), cantoAssetDTO.getName(), cantoAssetDTO.getThumbnailUrl(), cantoAssetDTO.getPreviewUrl(), cantoAssetDTO.getDescription(), cantoAssetDTO.getMDCRenditionBaseUrl(), cantoAssetDTO.getMDCAssetBaseUrl());
    }


    public CantoDAPAsset(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description, String mdc_rendition_baseurl, String mdc_asset_baseurl) {
        _assetIdentifier = new CantoAssetIdentifier(schema, identifier);
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

    public String getJsonIdentifier() {
        return CantoAssetIdentifierSerializer.toJsonIdentifier(this._assetIdentifier);
    }

    public String getPath() {
        return _assetIdentifier.getPath();
    }

    public String getTitle() {
        return _title;
    }

    public String getThumbnailUrl() {
        return _thumbnailUrl;
    }

    public String getPreviewUrl() {
        return AdditionalDataHandler.urlWithMdcOperations(this, this._previewUrl);
    }

    /**
     * Adds AdditionalData to the Assets identifier. This data will be serialized in the DAP.
     * AdditionalData is a String key-value Map.
     * Setting the value to null removes the key from the Map
     * @param key key for Map
     * @param value String to save
     */
    public void setAdditionalData(@NotNull String key, @Nullable String value) {
        this._assetIdentifier.setAdditionalData(key, value);
    }

    /**
     * Gets Value from AdditionalData for given key.
     * May return null, if no value is set for the key
     * @param key Lookup Key
     * @return value from AdditionalData or null
     */
    public @Nullable String getAdditionalData(@NotNull String key) {
        return this._assetIdentifier.getAdditionalData(key);
    }


    public String getMDCRenditionBaseUrl() {
        return _mdc_rendition_baseurl;
    }

    public String getMDCAssetBaseUrl() {
        return _mdc_asset_baseurl;
    }
}
