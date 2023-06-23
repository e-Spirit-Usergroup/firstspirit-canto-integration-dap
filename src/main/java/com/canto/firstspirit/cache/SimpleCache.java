package com.canto.firstspirit.cache;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

public class SimpleCache {

  static final int MAX_SIZE = 200;
  static final int CLEAR_SIZE = 100;

  static final int VALID_MS = 600 * 1000;

  Map<String, CacheItem> cacheMap = new ConcurrentHashMap<>();

  public void add(CantoAsset asset) {
    if (cacheMap.size() >= MAX_SIZE) {
      int i = 0;
      for (String identifier : cacheMap.keySet()) {
        cacheMap.remove(identifier);
        i++;
        if (i > CLEAR_SIZE) {
          break;
        }
      }
    }
    cacheMap.put(asset.getScheme() + "/" + asset.getId(), new CacheItem(asset));
  }

  public @Nullable CantoAsset get(CantoAssetIdentifier identifier) {
    final String key = identifier.getSchema() + "/" + identifier.getId();
    CacheItem cacheItem = cacheMap.get(key);
    if (cacheItem != null && cacheItem.getAsset() != null && cacheItem.isValid()) {
      return cacheItem.getAsset();
    }
    cacheMap.remove(key);
    return null;
  }


  public boolean has(CantoAssetIdentifier identifier) {
    return cacheMap.containsKey(identifier.getSchema() + "/" + identifier.getId());
  }

  public void clear() {
    cacheMap.clear();
  }


  private static class CacheItem {

    CacheItem(CantoAsset asset) {
      this.asset = asset;
      this.validUntilTimestampMs = System.currentTimeMillis() + VALID_MS;
    }

    private final CantoAsset asset;
    private final long validUntilTimestampMs;

    CantoAsset getAsset() {
      return asset;
    }

    public boolean isValid() {
      return validUntilTimestampMs > System.currentTimeMillis();
    }
  }

}
