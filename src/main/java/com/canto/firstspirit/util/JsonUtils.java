package com.canto.firstspirit.util;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.json.JsonArray;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonNullValue;
import de.espirit.firstspirit.json.values.JsonNumberValue;
import de.espirit.firstspirit.json.values.JsonStringValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

/**
 * Util class to transform Basic Java Classes to FirstSpirit JSON Elements <br> Supported Types: Number, String, Map&lt;String, ?&gt; Collection&lt;?&gt; <br> Values and Elements of Map and Collection must be one of the supported Types. <br> Map keys must always be Strings.
 */
public class JsonUtils {


  /**
   * Get JsonElement for Number. passed number may be null
   * <p>
   * Detects Circles, but Logs a Warning, as FirstSpirit JSON does not support Circles itself. Circles are replaced with String Value '[~circle]' on revisit.
   *
   * @param value Number, e.g. Long, Integer, Double
   * @return JsonElement Representing the number or JsonNull
   */
  public static JsonElement<?> getNullOrNumberJsonValue(@Nullable Number value) {
    if (value == null) {
      return JsonNullValue.NULL;
    }
    return JsonNumberValue.of(value);
  }

  /**
   * recursively transform Map to JsonObject.
   * <p>
   * Detects Circles, but Logs a Warning, as FirstSpirit JSON does not support Circles itself. Circles are replaced with String Value '[~circle]' on revisit.
   *
   * @param map keys must be String, values may be String, Number, Collections or Maps
   * @return Nested JsonObject representing the map
   */
  public static JsonElement<?> getMapAsJsonObject(Map<?, ?> map) {
    return getMapAsJsonObject(map, new HashSet<>());
  }

  /**
   * recursively transform Collection to JsonObject.
   *
   * @param collection Elements may be String, Number, Collections or Maps
   * @return Nested JsonObject representing the map
   */
  public static JsonElement<?> getCollectionAsJsonObject(Collection<?> collection) {
    return getCollectionAsJsonObject(collection, new HashSet<>());
  }

  private static JsonElement<?> getMapAsJsonObject(Map<?, ?> map, Collection<Object> seen) {
    if (map == null) {
      return JsonNullValue.NULL;
    }
    // Iterate over all entries and recursively create JSON Elements according to value class.
    // all keys must be Strings
    JsonObject result = JsonObject.create();
    for (Entry<?, ?> entry : map.entrySet()) {
      try {
        var key = (String) entry.getKey();
        var value = entry.getValue();

        if (value == null) {
          result.put(JsonPair.of(key, JsonNullValue.NULL));
        } else if (value instanceof String) {
          result.put(JsonPair.of(key, JsonStringValue.ofNullable(value)));
        } else if (value instanceof Number) {
          result.put(JsonPair.of(key, getNullOrNumberJsonValue((Number) value)));
        } else if (checkIfCircle(seen, value)) {
          Logging.logWarning("Circular JSON not Supported. replacing circle ref with [~circle]", JsonUtils.class);
          result.put(JsonPair.of(key, JsonStringValue.of("[~circle]")));
        } else if (value instanceof Collection) {
          result.put(JsonPair.of(key, getCollectionAsJsonObject((Collection<?>) value, seen)));
        } else if (value instanceof Map) {
          result.put(JsonPair.of(key, getMapAsJsonObject((Map<?, ?>) value, seen)));
        } else {
          Logging.logWarning("Unsupported Type for JSON: [" + value.getClass()
              .getName() + ": " + value + "]", JsonUtils.class);
        }
      } catch (Exception e) {
        Logging.logError("Error creating Json for Map", e, JsonUtils.class);
      }

    }

    return result;
  }

  private static JsonElement<?> getCollectionAsJsonObject(Collection<?> collection, Collection<Object> seen) {
    if (collection != null) {
      JsonArray result = JsonArray.create();

      for (Object element : collection) {
        if (element == null) {
          result.add(JsonNullValue.NULL);
        } else if (element instanceof String) {
          result.add(JsonStringValue.ofNullable(element));
        } else if (element instanceof Number) {
          result.add(getNullOrNumberJsonValue((Number) element));
        } else if (checkIfCircle(seen, element)) {
          Logging.logWarning("Circular JSON not Supported. replacing circle ref with [~circle]", JsonUtils.class);
          result.add(JsonStringValue.of("[~circle]"));
        } else if (element instanceof Collection<?>) {
          result.add(getCollectionAsJsonObject((Collection<?>) element));
        } else if (element instanceof Map<?, ?>) {
          result.add(getMapAsJsonObject((Map<?, ?>) element));
        } else {
          Logging.logWarning("Unsupported Type for JSON: [" + element.getClass()
              .getName() + ": " + element + "]", JsonUtils.class);
        }
      }

      return result;
    }
    return JsonNullValue.NULL;
  }

  private static boolean checkIfCircle(Collection<Object> seen, Object element) {
    return !seen.add(element);
  }

}
