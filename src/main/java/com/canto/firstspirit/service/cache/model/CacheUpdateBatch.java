package com.canto.firstspirit.service.cache.model;

import java.util.HashSet;

public class CacheUpdateBatch {

  public final int batchSize;
  public final HashSet<String> batch = new HashSet<>();
  public final long createdTimestamp;
  private final long updateTimespan;

  public CacheUpdateBatch(int batchSize, long updateTimespan) {
    this.batchSize = batchSize;
    this.updateTimespan = updateTimespan;
    this.createdTimestamp = System.currentTimeMillis();
  }

  public boolean isStale() {
    return this.createdTimestamp + updateTimespan < System.currentTimeMillis();
  }

}
