package com.canto.firstspirit.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * represents available CantoSchemes
 */
public enum CantoScheme {
  IMAGE("image"), VIDEO("video"), AUDIO("audio"), DOCUMENT("document"), PRESENTATION("presentation"), OTHER("other");
  final private String scheme;
  private static final Map<String, CantoScheme> SCHEME_MAP;


  CantoScheme(String commandString) {
    this.scheme = commandString;
  }

  public String toString() {
    return scheme;
  }

  public String getDisplayName() {
    return scheme.substring(0, 1)
        .toUpperCase() + scheme.substring(1);
  }

  // Create Map to easily get Enum Value from String
  static {
    Map<String, CantoScheme> map = new ConcurrentHashMap<>();
    for (CantoScheme scheme : CantoScheme.values()) {
      map.put(scheme.toString()
                  .toLowerCase(), scheme);
    }
    SCHEME_MAP = Collections.unmodifiableMap(map);
  }

  /**
   * Returns CANTO_SCHEME value or null, if no match has been found. Ignores Case
   *
   * @param scheme requested CANTO_SCHEME name. May be null
   * @return Matching CANTO_SCHEME, or null if no match or scheme was null
   */
  public static @Nullable CantoScheme fromString(@Nullable String scheme) {
    return scheme != null ? SCHEME_MAP.getOrDefault(scheme.toLowerCase(), null) : null;
  }

}