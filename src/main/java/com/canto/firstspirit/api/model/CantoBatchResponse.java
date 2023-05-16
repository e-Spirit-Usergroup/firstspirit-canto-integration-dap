package com.canto.firstspirit.api.model;

import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;

import java.util.ArrayList;
import java.util.List;

public class CantoBatchResponse {

    private final List<CantoAsset> docResult;

    private final List<CantoAssetIdentifier> failed;

    private CantoBatchResponse() {
        docResult = new ArrayList<>();
        failed = new ArrayList<>();
    }

    public List<CantoAsset> getDocResult() {
        return docResult;
    }

    @SuppressWarnings("unused")
    public List<CantoAssetIdentifier> getFailed() {
        return failed;
    }

    @Override
    public String toString() {
        return "[CantoBatchResponse: Found: " + this.docResult.size() + ", Missed: " + this.failed.size() + "]";
    }

}
