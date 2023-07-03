package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.canto.firstspirit.util.CantoScheme;
import de.espirit.common.base.Logging;
import org.jetbrains.annotations.Nullable;

public class CantoDAPFilter {

  private final @Nullable CantoScheme validScheme;
  
  CantoDAPFilter(@Nullable CantoScheme scheme) {
    this.validScheme = scheme;
  }

  @Nullable CantoScheme getValidScheme() {
    return validScheme;
  }

  boolean isValid(CantoDAPAsset dapAsset) {
    CantoScheme assetScheme = CantoScheme.fromString(dapAsset.getSchema());
    boolean valid = assetScheme != null && (validScheme == null || validScheme.equals(assetScheme));
    Logging.logInfo("Filter isValid : " + valid + ": " + dapAsset.getSchema() + " / " + this.validScheme, this.getClass());
    return valid;
  }

  @Override public String toString() {
    return "CantoDAPFilter{" + "validScheme=" + validScheme + '}';
  }

}
