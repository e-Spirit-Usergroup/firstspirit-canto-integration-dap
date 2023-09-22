package com.canto.firstspirit.service.cache;

import static com.canto.firstspirit.service.cache.model.CacheUpdateBatch.BATCH_SIZE;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.cache.model.CacheElement;
import com.canto.firstspirit.service.cache.model.CacheUpdateBatch;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class CacheUpdater {

  // private final static long UPDATE_INTERVAL_MS = 4 * 60 * 60 * 1000;
  private final static long UPDATE_INTERVAL_MS = 45 * 1000;

  protected final List<CacheUpdateBatch> updateBatches = Collections.synchronizedList(new LinkedList<>());
  ScheduledExecutorService executorService;

  final CentralCache centralCache;
  final @Nullable CantoApi cantoApi;

  protected CacheUpdater(CentralCache centralCache, @Nullable CantoApi cantoApi) {
    this.centralCache = centralCache;
    this.cantoApi = cantoApi;

    executorService = Executors.newSingleThreadScheduledExecutor((Runnable runnable) -> {
      Thread thread = new Thread(runnable);
      thread.setDaemon(true);
      return thread;
    });

    executorService.scheduleAtFixedRate(() -> {
      Logging.logInfo("[CacheUpdater] checking", this.getClass());
      if (!updateBatches.isEmpty()) {
        CacheUpdateBatch cacheUpdateBatch = updateBatches.get(0);

        if (cacheUpdateBatch.createdTimestamp > System.currentTimeMillis() - UPDATE_INTERVAL_MS) {
          Logging.logInfo("[CacheUpdater] updating...", this.getClass());

          updateBatches.remove(0);

          // Filter unused assets from Batch
          List<CantoAssetIdentifier> assetsToFetch = cacheUpdateBatch.batch.stream()
              .filter(path -> {
                CacheElement cacheElement = this.centralCache.getCacheElement(path);
                boolean isElementValid = (cacheElement != null) && cacheElement.isStillInUse();
                if (!isElementValid) {
                  this.centralCache.removeElement(path);
                }
                return isElementValid;
              })
              .map(CantoAssetIdentifierFactory::fromPath)
              .collect(Collectors.toList());

          // fetch Assets
          List<CantoAsset> fetchedAssets = cantoApi != null ? cantoApi.fetchAssets(assetsToFetch) : Collections.emptyList();

          // Use Set for better performance
          HashSet<CantoAssetIdentifier> assetsToFetchSet = new HashSet<>(assetsToFetch);

          // Requested Assets that have been found --> Refresh in Cache
          for (CantoAsset cantoAsset : fetchedAssets) {
            CantoAssetIdentifier cantoAssetIdentifier = this.centralCache.updateElement(cantoAsset);
            assetsToFetchSet.remove(cantoAssetIdentifier);
          }
          // Requested Assets, that have not been found --> Remove from Cache
          for (CantoAssetIdentifier cantoAssetIdentifier : assetsToFetchSet) {
            this.centralCache.removeElement(cantoAssetIdentifier);
          }
          Logging.logInfo("[CacheUpdater] update done! updated Items: " + fetchedAssets.size(), this.getClass());
        }
      }
    }, 30, 30, TimeUnit.SECONDS);
  }


  synchronized void addToUpdateBatch(String cacheId) {
    boolean isInAnyUpdateBatch = false;
    for (CacheUpdateBatch updateBatch : updateBatches) {
      HashSet<String> batchSet = updateBatch.batch;
      boolean inCurrentUpdateBatch = batchSet.contains(cacheId);
      if (!inCurrentUpdateBatch && batchSet.size() < BATCH_SIZE) {
        batchSet.add(cacheId);
        isInAnyUpdateBatch = true;
        Logging.logInfo("[CacheUpdater] add Element to Update Batch", this.getClass());
      }
      isInAnyUpdateBatch = isInAnyUpdateBatch || inCurrentUpdateBatch;

    }
    if (!isInAnyUpdateBatch) {
      Logging.logInfo("[CacheUpdater] Creating new Update Batch", this.getClass());
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
