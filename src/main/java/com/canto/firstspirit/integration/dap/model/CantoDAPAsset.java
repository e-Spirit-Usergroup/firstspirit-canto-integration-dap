package com.canto.firstspirit.integration.dap.model;

import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class CantoDAPAsset {

  private static final String MDC_URL_KEY = "MDC Image URL";

  private final String title;
  private final String thumbnailUrl;
  private final String previewUrl;
  private final String description;
  private final CantoAssetIdentifier assetIdentifier;
  @Nullable
  private final Map<String, String> additionalInfo;

  @NotNull public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
    return new CantoDAPAsset(cantoAssetDTO.getSchema(),
                             cantoAssetDTO.getId(),
                             cantoAssetDTO.getName(),
                             cantoAssetDTO.getThumbnailUrl(),
                             cantoAssetDTO.getPreviewUrl(),
                             cantoAssetDTO.getDescription(),
                             cantoAssetDTO.getAdditionalInfo());
  }


  public CantoDAPAsset(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description,
      @Nullable Map<String, String> additionalInfo) {
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

  /**
   * returns PreviewURL. Default size 800
   *
   * @return url as String
   */
  public String getPreviewUrl() {
    return this.previewUrl;
  }

  /**
   * get MDC Url. Logs Warning if MDC is not available. You can check availability via {@link #isMDCAvailable()} Throws
   *
   * @return MDC Url or empty String if not available.
   */
  public String getMDCUrl() {
    if (!isMDCAvailable()) {
      Logging.logWarning(this + "Requested MDCUrl not available.", this.getClass());
    }
    return this.additionalInfo != null ? this.additionalInfo.getOrDefault(MDC_URL_KEY, "") : "";
  }

  /**
   * returns MDC URL if available, PreviewUrl otherwise
   *
   * @return url as String
   */
  public String getUrl() {
    if (isMDCAvailable()) {
      return getMDCUrl();
    }
    return this.getPreviewUrl();
  }

  public String getId() {
    return assetIdentifier.getId();
  }

  public String getSchema() {
    return assetIdentifier.getSchema();
  }

  /**
   * Adds AdditionalData to the Assets identifier. This data will be serialized in the DAP. AdditionalData is a String key-value Map. Setting the
   * value to null removes the key from the Map
   *
   * @param key   key for Map
   * @param value String to save
   */
  public void setAdditionalDataEntry(@NotNull String key, @Nullable String value) {
    this.assetIdentifier.setAdditionalDataEntry(key, value);
  }

  /**
   * Gets Value from AdditionalData for given key. May return null, if no value is set for the key
   *
   * @param key Lookup Key
   * @return value from AdditionalData or null
   */
  public @Nullable String getAdditionalDataEntry(@NotNull String key) {
    return this.assetIdentifier.getAdditionalDataEntry(key);
  }

  public Map<String, String> getAdditionalData() {
    return this.assetIdentifier.getAdditionalData();
  }

  /**
   * returns 'additional' from CantoApi
   *
   * @return Map containing additional Info from Canto Api, like MDC Urls
   */
  public @Nullable Map<String, String> getAdditionalInfo() {
    return additionalInfo;
  }

  /**
   * true iff Mdc URL is filled and not empty
   *
   * @return indicator if MDC is Available
   */
  public boolean isMDCAvailable() {
    final @Nullable String mdcUrl = additionalInfo != null ? additionalInfo.get(MDC_URL_KEY) : null;
    return mdcUrl != null && !mdcUrl.isBlank();
  }

  @Override public String toString() {
    return "[CantoDAPAsset: " + this.assetIdentifier + "]";
  }

}
