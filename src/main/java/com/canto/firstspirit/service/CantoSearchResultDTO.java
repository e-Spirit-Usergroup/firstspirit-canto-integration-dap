package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.espirit.common.base.Logging;

public class CantoSearchResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;

    private List<CantoAssetDTO> results;

    private CantoSearchParams searchParams;

    private CantoSearchResultDTO() {
    }

    static CantoSearchResultDTO fromCantoSearchResult(CantoSearchParams cantoSearchParams, @NotNull CantoSearchResult cantoSearchResult, CantoApi cantoApi) {

        Logging.logDebug(cantoSearchResult.toString(), CantoSearchResultDTO.class);
        CantoSearchResultDTO cantoSearchResultDTO = new CantoSearchResultDTO();
        cantoSearchResultDTO.results = cantoSearchResult.getResults().stream().map( r-> CantoAssetDTO.fromAsset(r,cantoApi)).collect(Collectors.toList());
        cantoSearchResultDTO.total = cantoSearchResult.getFound().intValue();
        cantoSearchResultDTO.searchParams = cantoSearchParams;

        return cantoSearchResultDTO;
    }

    public int getTotal() {
        return total;
    }

    public List<CantoAssetDTO> getResults() {
        return Collections.unmodifiableList(results);
    }

}
