package com.canto.firstspirit.service.cache.model;

import com.canto.firstspirit.api.model.CantoAsset;

/**
 * Wrapper for {@link CantoAsset} with info needed for managing the cache
 */
public class CacheElement {


  public CantoAsset asset;

  private final long validTimespan;
  private final long inUseTimespan;
  public long lastUsedTimestamp;
  public long lastUpdatedTimestamp;

  /**
   * Create a CacheElement to be used in {@link com.canto.firstspirit.service.cache.CentralCache}
   *
   * @param asset         asset
   * @param validTimespan validity timespan for this item
   * @param inUseTimespan inUse timespan for this item
   */
  public CacheElement(CantoAsset asset, long validTimespan, long inUseTimespan) {
    this.validTimespan = validTimespan;
    this.inUseTimespan = inUseTimespan;
    long currentTimestamp = System.currentTimeMillis();
    this.asset = asset;
    lastUpdatedTimestamp = currentTimestamp;
    lastUsedTimestamp = currentTimestamp;
  }

  /**
   * true, if the item has been requested within inUseTimespan
   *
   * @return true, if recently requested. False otherwise
   */
  public boolean isStillInUse() {
    return lastUsedTimestamp + inUseTimespan > System.currentTimeMillis();
  }

  /**
   * true, if item has been updated within validTimespan
   *
   * @return true, if recently updated. False otherwise
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted") public boolean isValid() {
    return lastUpdatedTimestamp + validTimespan > System.currentTimeMillis();
  }

  @Override public String toString() {
    return "CacheElement{" + "asset=" + asset.getScheme() + "/" + asset.getId() + ", lastUsedTimestamp=" + lastUsedTimestamp + ", lastUpdatedTimestamp=" + lastUpdatedTimestamp + '}';
  }
}
