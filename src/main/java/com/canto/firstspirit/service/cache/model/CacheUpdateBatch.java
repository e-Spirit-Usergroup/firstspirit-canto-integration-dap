package com.canto.firstspirit.service.cache.model;

import java.util.HashSet;

public class CacheUpdateBatch {

  public final static int BATCH_SIZE = 75;

  public final HashSet<String> batch = new HashSet<>();
  public final long createdTimestamp;
  private final long updateTimespan;

  public CacheUpdateBatch(long updateTimespan) {
    this.updateTimespan = updateTimespan;
    this.createdTimestamp = System.currentTimeMillis();
  }

  public boolean isStale() {
    return this.createdTimestamp + updateTimespan < System.currentTimeMillis();
  }

}
