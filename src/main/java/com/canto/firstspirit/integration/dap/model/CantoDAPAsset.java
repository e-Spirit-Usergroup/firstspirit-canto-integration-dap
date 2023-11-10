package com.canto.firstspirit.integration.dap.model;

import static com.canto.firstspirit.util.MapUtils.mapGetOrDefault;

import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.canto.firstspirit.util.UrlHelper;
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
  private final String imagePreviewBaseUrl;
  private final String description;
  private final Long width;
  private final Long height;
  private final Long byteSize;
  private final String copyright;

  private final String fileExtension;
  @Nullable
  private final Map<String, Object> additionalInfo;
  private final String directOriginalUrl;

  @NotNull public static CantoDAPAsset fromCantoAssetDTO(@NotNull final CantoAssetDTO cantoAssetDTO) {
    return new CantoDAPAsset(CantoAssetIdentifierFactory.fromCantoAssetDTO(cantoAssetDTO),
                             cantoAssetDTO.getName(),
                             cantoAssetDTO.getDirectImagePreviewBaseUrl(),
                             cantoAssetDTO.getDirectOriginalUrl(),
                             cantoAssetDTO.getDescription(),
                             cantoAssetDTO.getWidth(),
                             cantoAssetDTO.getHeight(),
                             cantoAssetDTO.getByteSize(),
                             cantoAssetDTO.getCopyright(),
                             cantoAssetDTO.getFileExtension(),
                             cantoAssetDTO.getAdditionalInfo());
  }


  private CantoDAPAsset(final CantoAssetIdentifier assetIdentifier, String title, String previewBaseUrl, String directOriginalUrl, String description,
      Long width, Long height, Long byteSize, String copyright, String fileExtension, @Nullable Map<String, Object> additionalInfo) {
    this.assetIdentifier = assetIdentifier;
    this.title = title;
    this.imagePreviewBaseUrl = previewBaseUrl;
    this.directOriginalUrl = directOriginalUrl;
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

  /**
   * returns Image PreviewURL. Default size 800. <br>
   * If available it's recommended to use MDC Urls instead, because direct URLs count towards your Canto-Api Limit. <br>
   * See {@link #getMDCImageUrl()}
   *
   * @return url with trailing slash as String without scaling parameter
   */
  public String getPreviewBaseUrl() {
    return this.imagePreviewBaseUrl;
  }

  /**
   * returns Image PreviewURL with specified size. <br>
   * If available, it's recommended to use MDC Urls.
   * See {@link #getMDCImageUrl()}
   * <p>
   * See <a href="https://api.canto.com/#112d6ca4-f22c-4e13-b4b3-ff72bd999b35">Canto Api Documentation</a> for valid Scaling parameters.
   *
   * @param size Supported scalings: 100, 240, 320, 500, 640, 800, 2000
   * @return url as String
   */
  public String getPreviewUrl(int size) {
    return this.getPreviewBaseUrl() + size;
  }

  /**
   * @return Image PreviewUrl scaled to 800
   */
  public String getPreviewUrl() {
    return this.getPreviewUrl(800);
  }

  /**
   * @return Image PreviewUrl scaled to 100
   */
  public String getThumbnailUrl() {
    return this.getPreviewUrl(100);
  }


  /**
   * returns AssetURL for Download. <br>
   * If available, it's recommended to use MDC Urls.
   * See {@link #getMDCAssetUrl(String)}
   * <p>
   *
   * @return url as String
   */
  public String getOriginalAssetUrl() {
    return this.directOriginalUrl;
  }

  /**
   * get MDC Url if available. <br>
   * Logs Warning if MDC is not available. <br>
   * You can check availability via {@link #isMDCAvailable()}
   *
   * @return MDC Url or empty String if not available.
   */
  public String getMDCImageUrl() {
    return getMDCImageUrl(null);
  }

  /**
   * Returns MDC Url with given Parameters.
   * MDC Parameters are not validated!
   * See <a href="https://doc.canto.solutions/mdc/index.html#_url_format">MDC Documentation</a> as reference <br>
   * <b>Important Note</b>: You must specify/append at least one Parameter for the URL to be valid!
   * e.g. specify the format (e.g. -FPNG) or a scale/crop (e.g. -S200x200).
   * <p>
   * Returns the URL *with* trailing slash
   *
   * @param mdcParameters MDC rendition url Params to append to mdc URL. May be null or Empty String
   * @return MDC Url with appended Parameters if passed.
   */
  public String getMDCImageUrl(@Nullable String mdcParameters) {
    try {

      // Retrieve MDC Url. Remove -FJPG default Parameter
      String mdcBaseUrl = mapGetOrDefault(this.additionalInfo, MDC_IMAGE_URL, "");

      String cleanMdcUrl = UrlHelper.removeLastUrlPathPart(mdcBaseUrl);

      if (cleanMdcUrl.isBlank()) {
        Logging.logWarning(this + "Requested MDCUrl not available.", this.getClass());
        return "";
      }
      return mdcParameters != null ? cleanMdcUrl + mdcParameters : cleanMdcUrl;
    } catch (Exception e) {
      Logging.logError("Unable to retrieve MDCImageUrl for " + this, e, this.getClass());
    }
    return "";
  }

  /**
   * Returns MDC Asset URL for File Downloads.
   *
   * @param fileName specify Filename for download. File Extension is automatically added and may be omitted.
   * @return URL for Asset Download e.g. PDFs
   */
  public String getMDCAssetUrl(@Nullable String fileName) {
    String mdcBaseUrl = mapGetOrDefault(this.additionalInfo, MDC_ASSET_URL, "");
    return fileName != null && !fileName.isBlank() ? mdcBaseUrl + "/" + fileName : mdcBaseUrl;
  }

  /**
   * Returns MDC Asset URL for File Downloads.
   *
   * @return URL for Asset Download e.g. PDFs
   */
  public String getMDCAssetUrl() {
    return getMDCAssetUrl(null);
  }


  /**
   * returns MDC Image URL with parameter -FJPG if available, Image PreviewUrl scaled to 800 otherwise
   *
   * @return url as String
   */
  public String getUrl() {
    if (isMDCAvailable()) {
      return getMDCImageUrl("-FJPG");
    }
    return this.getPreviewUrl();
  }


  /**
   * @return Width of original Asset/Image
   */
  public Long getWidth() {
    return width;
  }

  /**
   * @return Height of original Asset/Image
   */
  public Long getHeight() {
    return height;
  }

  /**
   * @return Size of original Asset/Image in Bytes
   */
  public Long getByteSize() {
    return byteSize;
  }

  /**
   * @return Copyright
   */
  public String getCopyright() {
    return copyright;
  }

  /**
   * @return file extension without dot e.g. <br>
   * pdf
   */
  public String getFileExtension() {
    return fileExtension;
  }

  /**
   * @return id of asset
   */
  public String getId() {
    return assetIdentifier.getId();
  }

  /**
   * @return scheme of asset e.g. document / image
   */
  public String getSchema() {
    return assetIdentifier.getSchema();
  }

  /**
   * Adds AdditionalData to the Assets identifier. This data will be serialized in the DAP. AdditionalData is a String key-value Map. Setting the
   * value to null removes the key from the Map
   * To serialize the data, the asset as to be readded to the index with the manipulated identifier
   *
   * @param key   key for Map
   * @param value String to save
   */
  public void setAdditionalIdentifierDataEntry(@NotNull String key, @Nullable String value) {
    this.assetIdentifier.setAdditionalDataEntry(key, value);
  }

  /**
   * Gets Value from AdditionalData for given key. May return null, if no value is set for the key
   *
   * @param key Lookup Key
   * @return value from AdditionalData or null
   */
  public @Nullable String getAdditionalIdentifierDataEntry(@NotNull String key) {
    return this.assetIdentifier.getAdditionalDataEntry(key);
  }


  /**
   * @return mutable Map of additionalData saved in Identifier
   */
  public Map<String, String> getAdditionalIdentifierData() {
    return this.assetIdentifier.getAdditionalData();
  }

  /**
   * returns 'additional' from CantoApi
   *
   * @return Map containing additional Info from Canto Api, like MDC Urls
   */
  public @Nullable Map<String, Object> getAdditionalInfo() {
    return additionalInfo;
  }

  /**
   * true iff Mdc URL is filled and not empty
   *
   * @return indicator if MDC is Available
   */
  public boolean isMDCAvailable() {
    final @Nullable String mdcUrl = additionalInfo != null ? mapGetOrDefault(additionalInfo, MDC_IMAGE_URL, "") : null;
    return mdcUrl != null && !mdcUrl.isBlank();
  }

  @Override public String toString() {
    return "[CantoDAPAsset: " + this.assetIdentifier + "]";
  }

}
