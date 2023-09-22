package com.canto.firstspirit.service.cache;

import static com.canto.firstspirit.service.cache.model.CacheUpdateBatch.BATCH_SIZE;

import com.canto.firstspirit.service.cache.model.CacheUpdateBatch;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheUpdater {

  private final static long UPDATE_INTERVAL_MS = 4 * 60 * 60 * 1000;

  protected final List<CacheUpdateBatch> updateBatches = Collections.synchronizedList(new LinkedList<>());
  ScheduledExecutorService executorService;

  protected CacheUpdater() {
    executorService = Executors.newSingleThreadScheduledExecutor((Runnable runnable) -> {
      Thread thread = new Thread(runnable);
      thread.setDaemon(true);
      return thread;
    });

    executorService.scheduleAtFixedRate(() -> {
      if (!updateBatches.isEmpty()) {
        CacheUpdateBatch cacheUpdateBatch = updateBatches.get(0);

        if (cacheUpdateBatch.createdTimestamp > System.currentTimeMillis() - UPDATE_INTERVAL_MS) {

        }


      }


    }, 30, 30, TimeUnit.SECONDS);
  }


  synchronized void addToUpdateBatch(String cacheId) {
    boolean wasAdded = false;
    for (CacheUpdateBatch updateBatch : updateBatches) {
      HashSet<String> batchSet = updateBatch.batch;
      if (!batchSet.contains(cacheId) && batchSet.size() < BATCH_SIZE) {
        batchSet.add(cacheId);
        wasAdded = true;
      }
    }
    if (!wasAdded) {
      CacheUpdateBatch cacheUpdateBatch = new CacheUpdateBatch(System.currentTimeMillis());
      cacheUpdateBatch.batch.add(cacheId);
      updateBatches.add(cacheUpdateBatch);
    }
  }

  void shutdown() {
    updateBatches.clear();
    executorService.shutdown();
  }

}
