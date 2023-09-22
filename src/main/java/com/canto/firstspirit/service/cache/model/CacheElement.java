package com.canto.firstspirit.service.cache.model;

import com.canto.firstspirit.api.model.CantoAsset;

public class CacheElement {

  public CantoAsset asset;
  long lastUsedTimestamp;
  long lastUpdatedTimestamp;


  public CacheElement(CantoAsset asset) {
    long currentTimestamp = System.currentTimeMillis();
    this.asset = asset;
    lastUpdatedTimestamp = currentTimestamp;
    lastUsedTimestamp = currentTimestamp;
  }

}
