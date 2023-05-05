package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import org.jetbrains.annotations.NotNull;

public class CantoAssetUtils {

    private static String getPreviewUrlByResolution(@NotNull CantoAsset asset, int resolution) {
        return asset.getUrl().getPreview() + "/" + resolution;
    }

    public static String getPreviewUrl(@NotNull CantoAsset asset) {
        return getPreviewUrlByResolution(asset, 800);
    }

    public static String getThumbnailUrl(@NotNull CantoAsset asset) {
        return getPreviewUrlByResolution(asset, 100);
    }

}
