package com.canto.firstspirit.util;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class MapUtils {

  /**
   * Retrieves a key from given map with default Value. Map may be null. If retrieved value is null or nonexistent, default Value is returned
   *
   * @param map          map
   * @param key          key to retrieve from map
   * @param defaultValue Returned if map[key] is null or nonexistent
   * @return value from map or null
   */
  public static Object mapGetOrDefault(@Nullable Map<String, Object> map, String key, Object defaultValue) {
    return map != null ? map.getOrDefault(key, defaultValue) : defaultValue;
  }

  /**
   * Retrieves a key from given map with default Value. Map may be null. If retrieved value is null or nonexistent or not a String, default Value is returned
   *
   * @param map          map
   * @param key          key to retrieve from map
   * @param defaultValue Returned if map[key] is null, nonexistent or not a String
   * @return value from map or null
   */
  public static String mapGetOrDefault(@Nullable Map<String, Object> map, String key, String defaultValue) {
    Object value = map != null ? map.getOrDefault(key, defaultValue) : defaultValue;
    if (value instanceof String) {
      return (String) value;
    }
    return null;
  }


}
