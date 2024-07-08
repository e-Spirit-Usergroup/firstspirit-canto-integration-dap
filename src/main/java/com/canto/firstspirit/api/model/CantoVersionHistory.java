package com.canto.firstspirit.api.model;

@SuppressWarnings("unused")
public class CantoVersionHistory {
    /*
    "versionHistory": [
    {
        "no": "1",
            "ownerName": "Thomas Mockenhaupt",
            "created": "19700101010000000",
            "time": "20190212150025147",
            "versionId": "bGF0ZXN0X3ZlcnNpb25fdGFn",
            "uri": {
        "preview": "https://espirit.canto.global/api_binary/v1/image/js29l560ud229cfe3f79d0eg44/bGF0ZXN0X3ZlcnNpb25fdGFn/preview",
                "download": "https://espirit.canto.global/api_binary/v1/image/js29l560ud229cfe3f79d0eg44/bGF0ZXN0X3ZlcnNpb25fdGFn"
    }
    }
    ]*/

  private Long no;
  private String ownerName;
  private String created;
  private String time;
  private String versionId;
  private CantoUrls uri;

  public CantoVersionHistory() {
  }

  public CantoVersionHistory(Long no, String ownerName, String created, String time, String versionId, CantoUrls uri) {
    this.no = no;
    this.ownerName = ownerName;
    this.created = created;
    this.time = time;
    this.versionId = versionId;
    this.uri = uri;
  }

  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public CantoUrls getUri() {
    return uri;
  }

  public void setUri(CantoUrls uri) {
    this.uri = uri;
  }
}
