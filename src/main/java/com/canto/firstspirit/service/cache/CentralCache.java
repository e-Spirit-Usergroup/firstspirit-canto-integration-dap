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
 * central persistence of cache items
 * In charge of handling lifecycle of items and the cache itself.
 * Should <b>not</b> be used directly by Canto Api, instead use the ProjectBoundCacheAccess!
 */
public class CentralCache {

  protected final ConcurrentHashMap<String, CacheElement> cacheMap = new ConcurrentHashMap<>(5000);

  CacheUpdater cacheUpdater;

  public CentralCache(@Nullable CantoApi cantoApi) {
    if (cantoApi == null) {
      Logging.logInfo("No Api for Cache!", this.getClass());
    }
    cacheUpdater = new CacheUpdater(this, cantoApi);
  }


  public @Nullable CantoAsset getCantoAsset(CantoAssetIdentifier assetIdentifier) {
    return cacheMap.get(assetIdentifier.getPath()).asset;
  }

  @Nullable CacheElement getCacheElement(String assetPath) {
    return cacheMap.get(assetPath);
  }

  public void addElement(CantoAsset element) {
    String cacheId = CantoAssetIdentifierFactory.fromCantoAsset(element)
        .getPath();
    cacheMap.put(cacheId, new CacheElement(element));
    cacheUpdater.addToUpdateBatch(cacheId);
  }

  CantoAssetIdentifier updateElement(CantoAsset asset) {
    CantoAssetIdentifier cantoAssetIdentifier = CantoAssetIdentifierFactory.fromCantoAsset(asset);
    String cacheId = cantoAssetIdentifier.getPath();
    CacheElement cacheElement = cacheMap.get(cacheId);
    cacheElement.asset = asset;
    cacheElement.lastUpdatedTimestamp = System.currentTimeMillis();
    return cantoAssetIdentifier;
  }

  public void removeElement(String assetPath) {
    cacheMap.remove(assetPath);
  }

  public void removeElement(CantoAssetIdentifier assetIdentifier) {
    cacheMap.remove(assetIdentifier.getPath());
  }

  public void clear() {
    cacheMap.clear();
  }

  public void shutdown() {
    clear();
    cacheUpdater.shutdown();
  }

}
