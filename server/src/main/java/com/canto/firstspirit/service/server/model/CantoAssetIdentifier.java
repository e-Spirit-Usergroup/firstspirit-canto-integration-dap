package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * minimal representation of Data to fetch Data via Canto API
 */
public class CantoAssetIdentifier implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String schema;
  private final String id;

  private final Map<String, String> additionalData;

  /**
   * Only needed for Moshi Defaults
   */
  @SuppressWarnings("unused") private CantoAssetIdentifier() {
    this("DUMMY_SCHEMA", "DUMMY_ID");
  }

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

  public void setAdditionalDataEntry(@NotNull String key, @Nullable String value) {
    if (value != null) {
      this.additionalData.put(key, value);
    } else {
      this.additionalData.remove(key);
    }
  }

  public @Nullable String getAdditionalDataEntry(@NotNull String key) {
    return this.additionalData.get(key);
  }

  public Map<String, String> getAdditionalData() {
    return additionalData;
  }

  /**
   * Compares id and scheme, does not regard additionalData entries. Identifiers are equal iff their identifier and scheme are equal
   *
   * @param other Object to compare against
   * @return true, if other is Instance of {@link CantoAssetIdentifier} and identifier and scheme are equal. False Otherwise
   */
  @Override public boolean equals(Object other) {
    if (other instanceof CantoAssetIdentifier) {
      return ((CantoAssetIdentifier) other).getId()
          .equals(this.getId()) && ((CantoAssetIdentifier) other).getSchema()
          .equals(this.getSchema());
    }
    return false;
  }

  @Override public String toString() {
    return "[CantoAssetIdentifier: " + this.getPath() + "]";
  }

}
