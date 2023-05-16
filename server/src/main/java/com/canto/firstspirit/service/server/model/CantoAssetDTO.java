package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Map;

/**
 * This class represents the DTO created by the service based on a CantoAsset from the CantoApi
 */
public class CantoAssetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String thumbnailUrl;
    private final String previewUrl;
    private final String schema;
    private final String description;
    private final Map<String, String> additionalInfo;


    public CantoAssetDTO(String id, String name, String thumbnailUrl, String previewUrl, String schema, String description, Map<String, String> additionalInfo){
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.previewUrl = previewUrl;
        this.schema = schema;
        this.description = description;
        this.additionalInfo = additionalInfo;
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

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}