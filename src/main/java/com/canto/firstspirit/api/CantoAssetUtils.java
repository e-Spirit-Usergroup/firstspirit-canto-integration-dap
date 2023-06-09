package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

public class CantoAssetUtils {

  private static String getPreviewUrlByResolution(@NotNull CantoAsset asset, int resolution) {
    final HttpUrl baseUrl = HttpUrl.parse(asset.getUrl()
                                              .getDirectUrlPreview());
    final HttpUrl urlWithResolution = baseUrl != null ? baseUrl.resolve(String.valueOf(resolution)) : null;
    if (urlWithResolution == null) {
      throw new IllegalStateException("Unable to parse URL " + asset.getUrl()
          .getDirectUrlPreview() + " with resolution " + resolution);
    }

    return urlWithResolution.toString();
  }

  public static String getPreviewUrl(@NotNull CantoAsset asset) {
    return getPreviewUrlByResolution(asset, 800);
  }

  public static String getThumbnailUrl(@NotNull CantoAsset asset) {
    return getPreviewUrlByResolution(asset, 100);
  }

}
