package com.canto.firstspirit.api.model;

import static com.canto.firstspirit.util.MapUtils.mapGetOrDefault;

import com.squareup.moshi.Json;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class CantoAsset {

  final static String META_FILE_EXTENSION = "File Type Extension";
  final static String DEFAULT_COPYRIGHT = "Copyright";
  private String name;

  private String ownerName;

  private String time;

  private Long width;

  private Long height;

  private Double dpi;

  @Json(name = "url")
  private CantoUrls cantoUrls;

  private String id;

  private String scheme;

  private String owner;

  private String description;

  private Long size;

  private List<CantoVersionHistory> versionHistory;

  @Nullable
  private Map<String, Object> additional;

  @Nullable
  private Map<String, Object> metadata;


  @Nullable
  @Json(name = "default")
  private Map<String, Object> defaultData;

  public CantoAsset() {
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getTime() {
    return time;
  }

  public Long getWidth() {
    return width;
  }

  public Long getHeight() {
    return height;
  }

  public Number getDpi() {
    return dpi;
  }

  public CantoUrls getCantoUrls() {
    return cantoUrls;
  }


  public String getId() {
    return id;
  }

  public String getScheme() {
    return scheme;
  }


  public String getOwner() {
    return owner;
  }


  public String getDescription() {
    return description;
  }


  public Long getSize() {
    return size;
  }

  public List<CantoVersionHistory> getVersionHistory() {
    return versionHistory;
  }

  public String getName() {
    return name;
  }

  public @Nullable Map<String, Object> getAdditional() {
    return additional;
  }

  public @Nullable Map<String, Object> getMetadata() {
    return metadata;
  }

  public String getFileExtension() {
    return mapGetOrDefault(metadata, META_FILE_EXTENSION, "");
  }

  public String getCopyright() {
    return mapGetOrDefault(defaultData, DEFAULT_COPYRIGHT, "");
  }


  @Override public String toString() {
    return "[CantoAsset: " + this.scheme + "/" + this.id + "]";
  }
}
