package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the DTO created by the service based on a CantoAsset from the CantoApi
 */
public class CantoAssetDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String id;
  private final String name;
  private final String schema;
  private final String description;

  private final Long width;

  private final Long height;

  private final Long byteSize;

  private final String copyright;

  private final String fileExtension;

  @Nullable
  private final Map<String, Object> additionalInfo;
  private final String directImagePreviewBaseUrl;
  private final String directOriginalUrl;
  private final String approvalStatus;


  public CantoAssetDTO(String id, String name, String previewBaseUrl, String directOriginalUrl, String schema, String description, Long width,
      Long height, long byteSize, String copyright, String fileExtension, @Nullable Map<String, Object> additionalInfo, String approvalStatus) {
    this.id = id;
    this.name = name;
    this.directImagePreviewBaseUrl = previewBaseUrl;
    this.directOriginalUrl = directOriginalUrl;
    this.schema = schema;
    this.description = description;
    this.width = width;
    this.height = height;
    this.byteSize = byteSize;
    this.copyright = copyright;
    this.fileExtension = fileExtension;
    this.additionalInfo = additionalInfo;
    this.approvalStatus = approvalStatus;
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

  public @Nullable Map<String, Object> getAdditionalInfo() {
    return additionalInfo;
  }

  public Long getWidth() {
    return width;
  }

  public Long getHeight() {
    return height;
  }

  public long getByteSize() {
    return byteSize;
  }

  public String getCopyright() {
    return copyright;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String getDirectImagePreviewBaseUrl() {
    return directImagePreviewBaseUrl;
  }

  public String getDirectOriginalUrl() {
    return directOriginalUrl;
  }

  public String getApprovalStatus() {
    return approvalStatus;
  }
}