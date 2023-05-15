package com.canto.firstspirit.service.server.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * minimal representation of Data to fetch Data via Canto API
 */
public class CantoAssetIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String schema;
    private final String id;

    private final Map<String, String> additionalData;


    public CantoAssetIdentifier(String schema, String id) {
        this(schema, id, new HashMap<>());
    }

    public CantoAssetIdentifier(String schema, String id, Map<String, String> additionalData) {
        this.schema = schema;
        this.id = id;
        this.additionalData = additionalData != null ? additionalData : new HashMap<>();
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

    public void setAdditionalData(@NotNull String key, @Nullable String value) {
        if(value != null) {
            this.additionalData.put(key, value);
        } else {
            this.additionalData.remove(key);
        }
    }

    public @Nullable String getAdditionalData(@NotNull String key) {
        return this.additionalData.get(key);
    }

}
