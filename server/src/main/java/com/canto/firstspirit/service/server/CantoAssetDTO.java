package com.canto.firstspirit.service.server;

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


    public CantoAssetDTO(String id, String name, String thumbnailUrl, String previewUrl, String schema, String description, String mdcAssetBaseUrl, String mdcRenditionBaseUrl){
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.previewUrl = previewUrl;
        this.schema = schema;
        this.description = description;
        this.mdcAssetBaseUrl = mdcAssetBaseUrl;
        this.mdcRenditionBaseUrl = mdcRenditionBaseUrl;
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