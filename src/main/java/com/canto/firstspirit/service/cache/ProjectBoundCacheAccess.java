package com.canto.firstspirit.service.cache;


import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * ProjectBoundCacheAccess offers access to the central cache, based on Project Specific access restrictions.<br>
 * <p>
 * This class Does not  persist cached data itself. Only works as a "gate" to make sure,
 * projects only access Cache Objects they have access to with regards to Canto Access Authorization
 */
public class ProjectBoundCacheAccess {

  private final @Nullable CentralCache centralCache;
  private final ConcurrentHashMap<String, String> allowedCacheIds = new ConcurrentHashMap<>();


  public ProjectBoundCacheAccess(@Nullable CentralCache centralCache) {
    this.centralCache = centralCache;
  }


  /**
   * get an item from Central Cache
   *
   * @param assetIdentifier identifier
   * @return CantoAsset or null, if not in cache or restricted access
   */
  public @Nullable CantoAsset retrieveFromCache(CantoAssetIdentifier assetIdentifier) {
    // Check cache
    if (centralCache != null) {
      CantoAsset cachedAsset = centralCache.getCantoAsset(assetIdentifier);
      // If asset is in cache, make sure we have access to it by verifying modified date.
      // If it was changed since last access, deny cache and force refetch
      if (cachedAsset != null && cachedAsset.getLastModified()
          .equals(allowedCacheIds.get(assetIdentifier.getPath()))) {
        Logging.logDebug("[ProjectBoundCacheAccess] Cache hit: [" + assetIdentifier + "]", this.getClass());
        return cachedAsset;
      }
    }
    Logging.logDebug("[ProjectBoundCacheAccess] Cache miss: [" + assetIdentifier + "]", this.getClass());
    return null;
  }

  /**
   * add element to central cache. Also grants access to that element via {@link #retrieveFromCache(CantoAssetIdentifier)}
   *
   * @param assetIdentifier identifier
   * @param cantoAsset      cantoAsset
   */
  public void addToCache(CantoAssetIdentifier assetIdentifier, CantoAsset cantoAsset) {
    if (this.centralCache != null && cantoAsset != null) {
      this.allowedCacheIds.put(assetIdentifier.getPath(), cantoAsset.getLastModified());
      this.centralCache.addElement(cantoAsset);
      Logging.logDebug("[ProjectBoundCacheAccess] added/updated to cache:" + assetIdentifier, this.getClass());
    }
  }

  /**
   * add element to central cache. Also grants access to that element via {@link #retrieveFromCache(CantoAssetIdentifier)}
   *
   * @param cantoAsset asset
   */
  public void addToCache(CantoAsset cantoAsset) {
    if (cantoAsset != null) {
      CantoAssetIdentifier assetIdentifier = CantoAssetIdentifierFactory.fromCantoAsset(cantoAsset);
      addToCache(assetIdentifier, cantoAsset);
    }
  }

  /**
   * add elements to central cache. Also grants access to those elements via {@link #retrieveFromCache(CantoAssetIdentifier)}
   *
   * @param cantoAssetCollection collection of elements to add
   */
  public void addAllToCache(Collection<CantoAsset> cantoAssetCollection) {
    for (CantoAsset cantoAsset : cantoAssetCollection) {
      addToCache(cantoAsset);
    }
  }

  /**
   * update/add elements to central cache. Does *not* set last used.
   * Also grants access to those elements via {@link #retrieveFromCache(CantoAssetIdentifier)}
   *
   * @param cantoAssetCollection collection of elements to add
   */
  public void updateAllInCache(Collection<CantoAsset> cantoAssetCollection) {
    if (this.centralCache != null) {
      for (CantoAsset cantoAsset : cantoAssetCollection) {
        if (cantoAsset != null) {
          centralCache.updateElement(cantoAsset);
        }
      }
    }
  }

}
