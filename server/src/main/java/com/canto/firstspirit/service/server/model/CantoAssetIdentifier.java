package com.canto.firstspirit.service.server.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * minimal representation of Data to fetch Data via Canto API
 */
public class CantoAssetIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final String schema;
    protected final String id;

    public CantoAssetIdentifier(String schema, String id) {
        this.schema = schema;
        this.id = id;
    }

    public @NotNull String getSchema() {
        return schema;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getPath() {
        return schema + '/' + id;
    }


}
