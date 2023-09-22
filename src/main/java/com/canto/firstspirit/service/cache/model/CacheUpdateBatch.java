package com.canto.firstspirit.service.cache.model;

import java.util.HashSet;

public class CacheUpdateBatch {

  public final static int BATCH_SIZE = 100;

  public final HashSet<String> batch = new HashSet<>();
  public final long createdTimestamp;

  public CacheUpdateBatch(long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

}
