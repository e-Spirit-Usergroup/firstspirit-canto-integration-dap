package com.canto.firstspirit.integration.dap.custom;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;

/**
 * Template to handle AdditionalData that may be saves within the DAP
 * Implement custom Logic here, add method calls to {@link CantoDAPAsset} methods.
 */
public class AdditionalDataHandler {
    public static String urlWithMdcOperations(CantoDAPAsset asset, String url) {
        String mdcOperations = asset.getAdditionalDataEntry("mdcOperations");
        return mdcOperations != null ? url + "/" + mdcOperations : url;
    }

}
