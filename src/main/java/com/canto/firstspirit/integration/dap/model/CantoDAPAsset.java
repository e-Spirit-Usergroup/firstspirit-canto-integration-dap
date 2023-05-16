package com.canto.firstspirit.integration.dap.model;

import com.canto.firstspirit.integration.dap.custom.AdditionalDataHandler;
import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public class CantoDAPAsset {

    private final String title;
    private final String thumbnailUrl;
    private final String previewUrl;
    private final String description;
    private final CantoAssetIdentifier assetIdentifier;
    private final Map<String, String> additionalInfo;

    @NotNull
    public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
        //todo: handle dummy case
        return new CantoDAPAsset(cantoAssetDTO.getSchema(), cantoAssetDTO.getId(), cantoAssetDTO.getName(), cantoAssetDTO.getThumbnailUrl(), cantoAssetDTO.getPreviewUrl(), cantoAssetDTO.getDescription(), cantoAssetDTO.getAdditionalInfo());
    }


    public CantoDAPAsset(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description, Map<String, String> additionalInfo) {
        assetIdentifier = new CantoAssetIdentifier(schema, identifier);
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.previewUrl = previewUrl;
        this.description = description;
        this.additionalInfo = additionalInfo;
    }

    public String getDescription() {
        return description;
    }

    public String getJsonIdentifier() {
        return CantoAssetIdentifierSerializer.toJsonIdentifier(this.assetIdentifier);
    }

    public String getPath() {
        return assetIdentifier.getPath();
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPreviewUrl() {
        return AdditionalDataHandler.urlWithMdcOperations(this, this.previewUrl);
    }

    /**
     * Adds AdditionalData to the Assets identifier. This data will be serialized in the DAP.
     * AdditionalData is a String key-value Map.
     * Setting the value to null removes the key from the Map
     * @param key key for Map
     * @param value String to save
     */
    public void setAdditionalData(@NotNull String key, @Nullable String value) {
        this.assetIdentifier.setAdditionalData(key, value);
    }

    /**
     * Gets Value from AdditionalData for given key.
     * May return null, if no value is set for the key
     * @param key Lookup Key
     * @return value from AdditionalData or null
     */
    public @Nullable String getAdditionalData(@NotNull String key) {
        return this.assetIdentifier.getAdditionalData(key);
    }

    /**
     * returns additional from CantoApi
     * @return Map containing additional Info from Canto Api, like MDC Urls
     */
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
