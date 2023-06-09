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

  private static final String MDC_IMAGE_URL = "MDC Image URL";
  private static final String MDC_ASSET_URL = "MDC Asset URL";


  private final CantoAssetIdentifier assetIdentifier;
  private final String title;
  private final String thumbnailUrl;
  private final String previewUrl;
  private final String description;
  private final Long width;
  private final Long height;
  private final Long byteSize;
  private final String copyright;

  private final String fileExtension;
  @Nullable
  private final Map<String, String> additionalInfo;

  @NotNull public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
    return new CantoDAPAsset(cantoAssetDTO.getSchema(),
                             cantoAssetDTO.getId(),
                             cantoAssetDTO.getName(),
                             cantoAssetDTO.getThumbnailUrl(),
                             cantoAssetDTO.getPreviewUrl(),
                             cantoAssetDTO.getDescription(),
                             cantoAssetDTO.getWidth(),
                             cantoAssetDTO.getHeight(),
                             cantoAssetDTO.getByteSize(),
                             cantoAssetDTO.getCopyright(),
                             cantoAssetDTO.getFileExtension(),
                             cantoAssetDTO.getAdditionalInfo());
  }


  private CantoDAPAsset(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description, Long width,
      Long height, Long byteSize, String copyright, String fileExtension, @Nullable Map<String, String> additionalInfo) {
    this.assetIdentifier = new CantoAssetIdentifier(schema, identifier);
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.previewUrl = previewUrl;
    this.description = description;
    this.additionalInfo = additionalInfo;
    this.width = width;
    this.height = height;
    this.byteSize = byteSize;
    this.copyright = copyright;
    this.fileExtension = fileExtension;
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
  public String getMDCImageUrl() {
    return getMDCImageUrl(null);
  }

  /**
   * Returns MDC Url with given Parameters.
   * MDC Parameters are not validated!
   * See <a href="https://doc.canto.solutions/mdc/index.html#_url_format">MDC Documentation</a> as reference
   * <br/><b>Important Note</b>: You must specify/append at least one Parameter for the URL to be valid!
   * e.g. specify the format (e.g. -FPNG) or a scale/crop (e.g. -S200x200).
   * <p>
   * Returns the URL *with* trailing slash
   *
   * @param mdcParameters MDC rendition url Params to append to mdc URL. May be null or Empty String
   * @return MDC Url with appended Parameters if passed.
   */
  public String getMDCImageUrl(@Nullable String mdcParameters) {

    // Retrieve MDC Url. Remove -FJPG default Parameter
    String mdcBaseUrl = this.additionalInfo != null ? this.additionalInfo.getOrDefault(MDC_IMAGE_URL, "") : "";
    int index = mdcBaseUrl.lastIndexOf("/");
    String cleanMdcUrl = index >= 0 ? mdcBaseUrl.substring(0, index + 1) : "";

    if (cleanMdcUrl.isBlank()) {
      Logging.logWarning(this + "Requested MDCUrl not available.", this.getClass());
      return "";
    }
    return mdcParameters != null ? cleanMdcUrl + mdcParameters : cleanMdcUrl;
  }

  /**
   * Returns MDC Asset URL for File Downloads.
   *
   * @param fileName specify Filename for download. File Extension is automatically added and may be omitted.
   * @return URL for Asset Download e.g. PDFs
   */
  public String getMDCAssetUrl(String fileName) {
    String mdcBaseUrl = this.additionalInfo != null ? this.additionalInfo.getOrDefault(MDC_ASSET_URL, "") : "";
    return fileName != null && fileName.isBlank() ? mdcBaseUrl : mdcBaseUrl + "/" + fileName;
  }


  /**
   * returns MDC Image URL with parameter -FJPG if available, PreviewUrl otherwise
   *
   * @return url as String
   */
  public String getUrl() {
    if (isMDCAvailable()) {
      return getMDCImageUrl("-FJPG");
    }
    return this.getPreviewUrl();
  }


  public Long getWidth() {
    return width;
  }

  public Long getHeight() {
    return height;
  }

  public Long getByteSize() {
    return byteSize;
  }

  public String getCopyright() {
    return copyright;
  }

  public String getFileExtension() {
    return fileExtension;
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
    final @Nullable String mdcUrl = additionalInfo != null ? additionalInfo.get(MDC_IMAGE_URL) : null;
    return mdcUrl != null && !mdcUrl.isBlank();
  }

  @Override public String toString() {
    return "[CantoDAPAsset: " + this.assetIdentifier + "]";
  }

}
