package com.canto.firstspirit.service.cache;

import static com.canto.firstspirit.service.cache.model.CacheUpdateBatch.BATCH_SIZE;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.cache.model.CacheElement;
import com.canto.firstspirit.service.cache.model.CacheUpdateBatch;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

/**
 * The CacheUpdater periodically checks, if elements from CentralCache need revalidation. <br>
 * Revalidation is done in batches ({@link CacheUpdateBatch}), a batch is re-fetched based on its creation date and configured lifespan. <br><br>
 * Every {@link CacheElement} in a batch is newer than the batch itself. On revalidation, all elements in a batch that are still in use
 * (have been requested recently) are re-fetched with {@link CantoApi} and updated in the {@link CentralCache}.
 * Elements that have not been used recently are removed from the {@link CentralCache}.
 * <br>
 * The timespans for validation, fetching and usage checks can be configured in the
 * {@link com.canto.firstspirit.service.CantoSaasServiceConfigurable Service Configuration}
 * <br>
 * <br>
 * <b>The CacheUpdater manages a Thread for periodic checks. Remember to call {@link #shutdown()}, to ensure the Thread is stopped.</b>
 */
public class CacheUpdater {

  protected final List<CacheUpdateBatch> updateBatches = Collections.synchronizedList(new LinkedList<>());
  private final int maxCacheSize;
  ScheduledExecutorService executorService;

  final CentralCache centralCache;
  final @Nullable CantoApi cantoApi;
  private final long cacheUpdateTimespanMs;
  ScheduledFuture<?> scheduledUpdateTask;


  protected CacheUpdater(CentralCache centralCache, @Nullable CantoApi cantoApi, long cacheUpdateTimespanMs, int maxCacheSize) {
    this.centralCache = centralCache;
    this.cantoApi = cantoApi;
    this.cacheUpdateTimespanMs = cacheUpdateTimespanMs;
    this.maxCacheSize = maxCacheSize;
    startUpdaterTask();

  }


  /**
   * starts the periodic update thread. Restarts it if necessary
   * Main entry method to start the CacheUpdater
   */
  void startUpdaterTask() {
    startExecutorServiceIfNeeded();
    if (scheduledUpdateTask != null) {
      scheduledUpdateTask.cancel(false);
    }
    scheduledUpdateTask = executorService.scheduleAtFixedRate(() -> {
      try {
        // Check if Thread was interrupted while waiting
        if (Thread.currentThread()
            .isInterrupted()) {
          Logging.logInfo("[CacheUpdater] interrupted!", this.getClass());
          Thread.currentThread()
              .interrupt();
        }

        Logging.logInfo("[CacheUpdater] running...", this.getClass());

        // Process Update Batch if needed
        processUpdateBatch();
        // perform cache Cleanup if needed
        performCacheCleanUp();

        Logging.logInfo("[CacheUpdater] finished. ", this.getClass());

      } catch (Exception e) {
        Logging.logError("[CacheUpdater] Error during CacheUpdater Run", e, this.getClass());
      }
    }, 30, 30, TimeUnit.SECONDS);
  }

  /**
   * Ensures that the serviceExecutor is ready for scheduling
   */
  private void startExecutorServiceIfNeeded() {
    if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
      executorService = Executors.newSingleThreadScheduledExecutor((Runnable runnable) -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
      });
    }
  }

  /**
   * Add the cacheId to the updateBatches.<br>
   * If the id is not yet present, it's appended to the last (newest) updateBatch, that is not full.<br>
   * Nothing is done, if it is part of some batch already. <br>
   * Automatically creates new batches if needed.
   *
   * @param cacheId must be valid assetPath, see {@link CantoAssetIdentifier#getPath()}
   */
  synchronized void addToUpdateBatch(String cacheId) {
    boolean isInAnyUpdateBatch = false;
    for (CacheUpdateBatch updateBatch : updateBatches) {
      HashSet<String> batchSet = updateBatch.batch;
      boolean inCurrentUpdateBatch = batchSet.contains(cacheId);
      if (!inCurrentUpdateBatch && batchSet.size() < BATCH_SIZE) {
        batchSet.add(cacheId);
        isInAnyUpdateBatch = true;
        Logging.logDebug("[CacheUpdater] add Element to Update Batch [" + cacheId + "]", this.getClass());
      }
      isInAnyUpdateBatch = isInAnyUpdateBatch || inCurrentUpdateBatch;

    }
    if (!isInAnyUpdateBatch) {
      Logging.logDebug("[CacheUpdater] Creating new Update Batch", this.getClass());
      CacheUpdateBatch cacheUpdateBatch = new CacheUpdateBatch(cacheUpdateTimespanMs);
      cacheUpdateBatch.batch.add(cacheId);
      updateBatches.add(cacheUpdateBatch);
    }
  }

  private @Nullable CacheUpdateBatch getStaleUpdateBatch() {
    if (!updateBatches.isEmpty()) {
      CacheUpdateBatch cacheUpdateBatch = updateBatches.get(0);
      if (cacheUpdateBatch.isStale()) {
        updateBatches.remove(0);
        return cacheUpdateBatch;
      }
    } else {
      Logging.logInfo("[CacheUpdater] no updateBatches queued", this.getClass());
    }
    return null;
  }

  private List<CantoAssetIdentifier> prefilterUpdateBatch(CacheUpdateBatch cacheUpdateBatch, boolean filterForElementLifeSpan) {
    Logging.logInfo("[CacheUpdater] prefilter UpdateBatch. filterForElementLifeSpan=" + filterForElementLifeSpan, this.getClass());

    // Filter unused assets from Batch, create List of assets to fetch
    List<CantoAssetIdentifier> remainingElements = new ArrayList<>(cacheUpdateBatch.batch.size());
    for (String path : cacheUpdateBatch.batch) {
      CacheElement cacheElement = this.centralCache.getCacheElement(path);
      // Element still in cache?
      if (cacheElement != null) {
        // remove Element if it is not used anymore or optionally if its stale
        boolean removeElement = !cacheElement.isStillInUse() || (filterForElementLifeSpan && !cacheElement.isValid());
        if (removeElement) {
          Logging.logDebug("[CacheUpdater] Removing item [" + path + "]", this.getClass());
          this.centralCache.removeElement(path);
        } else {
          // Otherwise add it to list to be re-fetched
          remainingElements.add(CantoAssetIdentifierFactory.fromCantoAsset(cacheElement.asset));
        }
      }
    }

    return remainingElements;
  }

  /**
   * Check for stale UpdateBatch and Process it
   */
  private void processUpdateBatch() {
    CacheUpdateBatch staleUpdateBatch = getStaleUpdateBatch();
    if (staleUpdateBatch != null) {
      Logging.logInfo("[CacheUpdater] UpdateBatch stale. Updating...", this.getClass());

      if (cantoApi == null) {
        Logging.logWarning("[CacheUpdater] No CantoApi available => Cannot re-fetch Items.", this.getClass());
        prefilterUpdateBatch(staleUpdateBatch, true);
      } else {
        List<CantoAssetIdentifier> identifiersToFetch = prefilterUpdateBatch(staleUpdateBatch, false);
        // fetch Assets
        List<CantoAsset> fetchedAssets = cantoApi.fetchAssets(identifiersToFetch);

        // Requested Assets that have been found --> Refresh in Cache
        for (CantoAsset cantoAsset : fetchedAssets) {
          CantoAssetIdentifier cantoAssetIdentifier = this.centralCache.updateElement(cantoAsset);
          staleUpdateBatch.batch.remove(cantoAssetIdentifier.getPath());
        }

        // Requested Assets, that have not been found --> Remove from Cache
        for (String cantoAssetPath : staleUpdateBatch.batch) {
          this.centralCache.removeElement(cantoAssetPath);
        }
        Logging.logInfo("[CacheUpdater] update done. updated Items: " + fetchedAssets.size() + " - removed Items: " + staleUpdateBatch.batch.size(),
                        this.getClass());
      }
    } else {
      Logging.logInfo("[CacheUpdater] updateBatches fresh", this.getClass());
    }

  }

  /**
   * check cache load and perform cleanup if necessary
   */
  private void performCacheCleanUp() {

    double cacheLoad = (centralCache.cacheMap.size() / (double) maxCacheSize);
    Logging.logInfo("[CacheUpdater] Cache load " + cacheLoad * 100 + "%", this.getClass());

    if (cacheLoad > 0.95) {
      // Cache is close to full, start soft cleanup, then hard cleanup
      ConcurrentHashMap<String, CacheElement> cacheMap = centralCache.cacheMap;
      Logging.logInfo("[CacheUpdater] Cache load high. Start Cleanup", this.getClass());
      // we want to bring the cache load down to 85%
      int cleanupTarget = centralCache.cacheMap.size() - (int) (0.85 * maxCacheSize);
      int cleanedUpElements = 0;

      Enumeration<String> keys = cacheMap.keys();
      while (keys.hasMoreElements()) {
        String identifier = keys.nextElement();
        CacheElement cacheElement = cacheMap.get(identifier);
        if (!cacheElement.isStillInUse() || !cacheElement.isValid()) {
          cleanedUpElements++;
          cacheMap.remove(identifier);
        }
      }
      Logging.logInfo("[CacheUpdater] Soft Cleanup done. removed Elements: " + cleanedUpElements, this.getClass());

      // Soft cleanup did not suffice ->  hard cleanup based on updateBatches
      // Oldest batch has heuristically the oldest elements
      while (cleanedUpElements < cleanupTarget && !updateBatches.isEmpty()) {
        CacheUpdateBatch cacheUpdateBatch = updateBatches.get(0);
        for (String identifier : cacheUpdateBatch.batch) {
          cleanedUpElements++;
          cacheMap.remove(identifier);
        }
        updateBatches.remove(0);
      }
      Logging.logInfo(
          "[CacheUpdater] Hard Cleanup done. Total removed Elements (soft + hard): " + cleanedUpElements + " - New Cache load after Cleanup: "
              + cacheMap.size() + " (" + (cacheMap.size() / (double) maxCacheSize) * 100 + "%)", this.getClass());
    }
  }


  /**
   * clears UpdateBatches, stops the thread and scheduler
   * <b>Must be called, if CentralCache is stopped!</b>
   */
  void shutdown() {
    Logging.logInfo("[CacheUpdater] shutting down...", this.getClass());
    updateBatches.clear();
    scheduledUpdateTask.cancel(false);
    executorService.shutdown();
  }

}
