package com.canto.firstspirit.service.server;

import java.io.Serializable;

public interface CantoAssetDTO extends Serializable {
    String getDescription();

    String getSchema();

    String getId();

    String getName();

    String getThumbnailUrl();

    String getPreviewUrl();

    String getMDCAssetBaseUrl();

    String getMDCRenditionBaseUrl();
}
