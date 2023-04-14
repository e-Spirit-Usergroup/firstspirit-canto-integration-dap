package com.canto.firstspirit.api.model;

@SuppressWarnings("unused")
public class CantoUrls {

    private String preview;
    private String detail;
    private String download;
    private String metadata;
    private String HighJPG;
    private String PNG;
    private String LowJPG;

    public CantoUrls(String preview, String detail, String download, String metadata, String highJPG, String png, String lowJPG) {
        this.preview = preview;
        this.detail = detail;
        this.download = download;
        this.metadata = metadata;
        HighJPG = highJPG;
        PNG = png;
        LowJPG = lowJPG;
    }

    public CantoUrls() {
    }

    public String getPreview() {
        return preview;
    }

    public String getDetail() {
        return detail;
    }

    public String getDownload() {
        return download;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getHighJPG() {
        return HighJPG;
    }

    public String getPNG() {
        return PNG;
    }

    public String getLowJPG() {
        return LowJPG;
    }
}
