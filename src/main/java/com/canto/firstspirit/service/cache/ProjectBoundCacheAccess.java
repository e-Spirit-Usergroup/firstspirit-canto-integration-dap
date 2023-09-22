package com.canto.firstspirit.service.cache;


import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * Does not  persist cached data itself. Only works as a "gate" to make sure,
 * projects only access Cache Objects they have access to with regards to Canto Access Authorization
 */
public class ProjectBoundCacheAccess {

  private final @Nullable CentralCache centralCache;
  private final ConcurrentHashMap<String, String> allowedCacheIds = new ConcurrentHashMap<>();


  public ProjectBoundCacheAccess(@Nullable CentralCache centralCache) {
    this.centralCache = centralCache;
  }


  public @Nullable CantoAsset retrieveFromCache(CantoAssetIdentifier assetIdentifier) {
    // Check cache
    if (centralCache != null) {
      CantoAsset cachedAsset = centralCache.getElement(assetIdentifier);
      // If asset is in cache, make sure we have access to it by verifying modified date.
      // If it was changed since last access, deny cache and force refetch
      if (cachedAsset != null && cachedAsset.getLastModified()
          .equals(allowedCacheIds.get(assetIdentifier.getPath()))) {
        Logging.logInfo("[ProjectBoundCacheAccess] Cache hit: " + assetIdentifier, this.getClass());
        return cachedAsset;
      }
    }
    return null;
  }


  public void addToCache(CantoAssetIdentifier assetIdentifier, CantoAsset cantoAsset) {
    if (this.centralCache != null && cantoAsset != null) {
      this.allowedCacheIds.put(assetIdentifier.getPath(), cantoAsset.getLastModified());
      this.centralCache.addElement(cantoAsset);
      Logging.logInfo("[ProjectBoundCacheAccess] added/updated to cache:" + assetIdentifier, this.getClass());
    }
  }

  public void addToCache(CantoAsset cantoAsset) {
    if (cantoAsset != null) {
      CantoAssetIdentifier assetIdentifier = CantoAssetIdentifierFactory.fromCantoAsset(cantoAsset);
      addToCache(assetIdentifier, cantoAsset);
    }
  }

  public void addAllToCache(Collection<CantoAsset> cantoAssetCollection) {
    for (CantoAsset cantoAsset : cantoAssetCollection) {
      addToCache(cantoAsset);
    }
  }

}
