package com.canto.firstspirit.api.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CantoAsset {

    private String name;

    private String ownerName;

    private String time;

    private Long width;

    private Long height;

    private Long dpi;

    private CantoUrls url;

    private String id;

    private String scheme;

    private String owner;

    private String description;

    private Long size;

    private List<CantoVersionHistory> versionHistory;

    @Nullable
    private Map<String, String> additional;

    private boolean isDummy = false;

    public CantoAsset(String name, String ownerName, String time, Long width, Long height, Long dpi, CantoUrls url, String id, String scheme, String owner, String description, Long size, List<CantoVersionHistory> versionHistory, @Nullable Map<String, String> additional) {
        this.name = name;
        this.ownerName = ownerName;
        this.time = time;
        this.width = width;
        this.height = height;
        this.dpi = dpi;
        this.url = url;
        this.id = id;
        this.scheme = scheme;
        this.owner = owner;
        this.description = description;
        this.size = size;
        this.versionHistory = versionHistory;
        this.additional = additional;
    }

    public CantoAsset() {
    }

    public static CantoAsset createDummyAsset(String id){
        CantoAsset cantoAsset = new CantoAsset();
        cantoAsset.id = id;
        cantoAsset.isDummy = true;
        cantoAsset.name = "Deleted";
        cantoAsset.description = "Deleted";
        return cantoAsset;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTime() {
        return time;
    }

    public Number getWidth() {
        return width;
    }

    public Number getHeight() {
        return height;
    }

    public Number getDpi() {
        return dpi;
    }

    public CantoUrls getUrl() {
        return url;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public void setDpi(Long dpi) {
        this.dpi = dpi;
    }

    public void setUrl(CantoUrls url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public List<CantoVersionHistory> getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(List<CantoVersionHistory> versionHistory) {
        this.versionHistory = versionHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable Map<String, String> getAdditional() {
        return additional;
    }
    public void setAdditional(@Nullable Map<String, String> additional) {
        this.additional = additional;
    }
}
