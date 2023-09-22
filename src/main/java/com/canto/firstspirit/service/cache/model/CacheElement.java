package com.canto.firstspirit.service.cache.model;

import com.canto.firstspirit.api.model.CantoAsset;

public class CacheElement {

  final static long IN_USE_TIMESPAN_MS = 48 * 60 * 60 * 1000;

  public CantoAsset asset;
  public long lastUsedTimestamp;
  public long lastUpdatedTimestamp;


  public CacheElement(CantoAsset asset) {
    long currentTimestamp = System.currentTimeMillis();
    this.asset = asset;
    lastUpdatedTimestamp = currentTimestamp;
    lastUsedTimestamp = currentTimestamp;
  }


  public boolean isStillInUse() {
    return lastUsedTimestamp + IN_USE_TIMESPAN_MS > System.currentTimeMillis();
  }

}
