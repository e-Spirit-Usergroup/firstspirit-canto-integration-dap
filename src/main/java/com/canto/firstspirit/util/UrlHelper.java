package com.canto.firstspirit.util;

import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UrlHelper {

  /**
   * returns url without last path part <br>
   * Returned URL always contains trailing slash. Example: <br>
   * "https://dummy.com/some/path" -> "https://dummy.com/some/
   * <p>
   * Does not modify input url
   *
   * @param url e.g. "https://dummy.com/some/path"
   * @return url up till and including last trailing slash. Empty String if url is null or empty
   */
  @SuppressWarnings("JavadocLinkAsPlainText") public static @NotNull String removeLastUrlPathPart(@Nullable final String url) {

    if (url == null || url.isBlank()) {
      return "";
    }

    final HttpUrl parsedUrl = HttpUrl.parse(url);
    if (parsedUrl == null) {
      throw new IllegalStateException("Unable to parse URL " + url);
    }

    String baseUrl = parsedUrl.newBuilder()
        .removePathSegment(parsedUrl.pathSize() - 1)
        .toString();

    return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
  }


}
