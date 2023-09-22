package com.canto.firstspirit.service.cache;

import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.cache.model.CacheElement;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * central persistence of cache items
 * In charge of handling lifecycle of items and the cache itself.
 * Should <b>not</b> be used directly by Canto Api, instead use the ProjectBoundCacheAccess!
 */
public class CentralCache {

  protected final ConcurrentHashMap<String, CacheElement> cacheMap = new ConcurrentHashMap<>(5000);

  CacheUpdater cacheUpdater;

  public CentralCache() {
    cacheUpdater = new CacheUpdater();
  }


  public @Nullable CantoAsset getElement(CantoAssetIdentifier assetIdentifier) {
    return cacheMap.get(assetIdentifier.getPath()).asset;
  }

  public void addElement(CantoAsset element) {
    String cacheId = CantoAssetIdentifierFactory.fromCantoAsset(element)
        .getPath();
    cacheMap.put(cacheId, new CacheElement(element));
    cacheUpdater.addToUpdateBatch(cacheId);
  }

  public void clear() {
    cacheMap.clear();
  }

  public void shutdown() {
    clear();
    cacheUpdater.shutdown();
  }

}
