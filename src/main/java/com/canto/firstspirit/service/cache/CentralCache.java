package com.canto.firstspirit.service.cache;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.cache.model.CacheElement;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * Central persistence of cache items Handling of Lifecycle of elements and Cache Load handling is internally delegated to {@link CacheUpdater}. Configurable via {@link com.canto.firstspirit.service.CantoSaasServiceConfigurable Service Configuration}
 * <br>
 * <br>
 * Should <b>not</b> be used directly by Canto Api, instead use the {@link ProjectBoundCacheAccess}!
 */
public class CentralCache {

  final long cacheItemLifespanMs;
  final long cacheUpdateTimespanMs;
  final long cacheItemInUseTimespanMs;

  final int maxCacheSize;

  protected final ConcurrentHashMap<String, CacheElement> cacheMap;

  CacheUpdater cacheUpdater;

  public CentralCache(@Nullable CantoApi cantoApi, int maxCacheSize, long cacheItemLifespanMs, long cacheUpdateTimespanMs, long cacheItemInUseTimespanMs, int batchUpdateSize) {
    this.cacheItemLifespanMs = cacheItemLifespanMs;
    this.cacheUpdateTimespanMs = cacheUpdateTimespanMs;
    this.cacheItemInUseTimespanMs = cacheItemInUseTimespanMs;
    this.cacheMap = new ConcurrentHashMap<>(maxCacheSize);
    if (cantoApi == null) {
      Logging.logInfo("No Api for Cache!", this.getClass());
    }
    this.maxCacheSize = maxCacheSize;
    cacheUpdater = new CacheUpdater(this, cantoApi, cacheUpdateTimespanMs, maxCacheSize, batchUpdateSize);

    Logging.logInfo("[CentralCache] created. " + this, this.getClass());
  }

  /**
   * request a CantoAsset from Cache. If an Element is found, its lastUsed Timestamp is refreshed.
   *
   * @param assetIdentifier AssetIdentifier
   * @return CantoAsset from cache, or null if not in cache or invalid
   */
  public @Nullable CantoAsset getCantoAsset(CantoAssetIdentifier assetIdentifier) {
    CacheElement cacheElement = cacheMap.get(assetIdentifier.getPath());
    // nothing cached
    if (cacheElement == null) {
      return null;
    }
    // element is stale
    if (!cacheElement.isValid()) {
      Logging.logDebug("[CentralCache] remove stale Element [" + assetIdentifier.getPath() + "]", this.getClass());
      cacheMap.remove(assetIdentifier.getPath());
      return null;
    }
    // cache hit, set last usage
    cacheElement.lastUsedTimestamp = System.currentTimeMillis();
    return cacheElement.asset;
  }

  /**
   * get CacheItem Wrapper. Using this method does not modifiy lastUsed Timestamp. Hence, it can be used for internal handling of cache e.g. by CacheUpdater
   *
   * @param assetPath assetPath
   * @return CacheElement wrapper
   */
  @Nullable CacheElement getCacheElement(String assetPath) {
    return cacheMap.get(assetPath);
  }

  /**
   * add Element to Cache. Overwrites existing item, hence resetting lastUsed and lastUpdated to now
   *
   * @param element element to add
   */
  public void addElement(CantoAsset element) {
    String cacheId = CantoAssetIdentifierFactory.fromCantoAsset(element)
        .getPath();
    cacheMap.put(cacheId, new CacheElement(element, cacheItemLifespanMs, cacheItemInUseTimespanMs));
    cacheUpdater.addToUpdateBatch(cacheId);
    Logging.logDebug("[CentralCache] Added Element [" + cacheId + "]", this.getClass());
  }

  /**
   * Update Existing Element, create if non-existent. Does <b>not</b> set lastUsed, hence can be used for internal purposes of CacheUpdater
   *
   * @param asset asset to update / create in cache
   * @return identifier of asset
   */
  CantoAssetIdentifier updateElement(CantoAsset asset) {
    CantoAssetIdentifier cantoAssetIdentifier = CantoAssetIdentifierFactory.fromCantoAsset(asset);
    String cacheId = cantoAssetIdentifier.getPath();

    CacheElement cacheElement = cacheMap.computeIfAbsent(cacheId, (key) -> {
      // Create Element, but set lastUsedTimestamp to 0, hence mark it as "not in use"
      CacheElement newElement = new CacheElement(asset, cacheItemLifespanMs, cacheItemInUseTimespanMs);
      newElement.lastUsedTimestamp = 0;
      return newElement;
    });

    cacheElement.asset = asset;
    cacheElement.lastUpdatedTimestamp = System.currentTimeMillis();
    cacheUpdater.addToUpdateBatch(cantoAssetIdentifier.getPath());

    Logging.logDebug("[CentralCache] Updated Element [" + cacheElement + "]", this.getClass());

    return cantoAssetIdentifier;
  }

  /**
   * Remove an Element from CentralCache.
   *
   * @param assetPath assetPath
   */
  public void removeElement(String assetPath) {
    Logging.logDebug("[CentralCache] removeElement [" + assetPath + "]", this.getClass());
    cacheMap.remove(assetPath);
  }

  /**
   * Remove an Element from CentralCache.
   *
   * @param assetIdentifier assetIdentifier
   */
  @SuppressWarnings("unused") public void removeElement(CantoAssetIdentifier assetIdentifier) {
    cacheMap.remove(assetIdentifier.getPath());
  }

  /**
   * Remove all CacheItems
   */
  public void clear() {
    cacheUpdater.clearUpdateBatches();
    cacheMap.clear();
  }

  public void shutdown() {
    Logging.logInfo("[CentralCache] shutting down.", this.getClass());
    clear();
    cacheUpdater.shutdown();
  }

  @Override public String toString() {
    return "CentralCache{" + "cacheItemLifespanMs=" + cacheItemLifespanMs + ", cacheUpdateTimespanMs=" + cacheUpdateTimespanMs + ", cacheItemInUseTimespanMs=" + cacheItemInUseTimespanMs + ", maxCacheSize=" + maxCacheSize + '}';
  }
}
