package com.canto.firstspirit.service.cache;

import com.canto.firstspirit.api.CantoAssetIdentifierFactory;
import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

public class CentralCache {

  private final ConcurrentHashMap<String, CantoAsset> cacheMap = new ConcurrentHashMap<>();

  public CentralCache() {

  }

  public @Nullable CantoAsset getElement(CantoAssetIdentifier assetIdentifier) {
    return cacheMap.get(assetIdentifier.getPath());
  }

  public void addElement(CantoAsset element) {
    cacheMap.put(CantoAssetIdentifierFactory.fromCantoAsset(element)
                     .getPath(), element);
  }
  
}
