package com.canto.firstspirit.service;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.CantoAssetDTO;
import com.canto.firstspirit.service.server.CantoSearchParams;
import com.canto.firstspirit.service.server.CantoSearchResultDTO;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.espirit.common.base.Logging;

public class CantoSearchResultDTOImpl implements CantoSearchResultDTO {
    private static final long serialVersionUID = 1L;

    private int total;

    private List<CantoAssetDTO> results;

    private CantoSearchParams searchParams;

    private CantoSearchResultDTOImpl() {
    }

    static CantoSearchResultDTO fromCantoSearchResult(CantoSearchParams cantoSearchParams, @NotNull CantoSearchResult cantoSearchResult, CantoApi cantoApi) {

        Logging.logDebug(cantoSearchResult.toString(), CantoSearchResultDTOImpl.class);
        CantoSearchResultDTOImpl cantoSearchResultDTO = new CantoSearchResultDTOImpl();
        cantoSearchResultDTO.results = cantoSearchResult.getResults().stream().map( r-> CantoAssetDTOImpl.fromAsset(r,cantoApi)).collect(Collectors.toList());
        cantoSearchResultDTO.total = cantoSearchResult.getFound().intValue();
        cantoSearchResultDTO.searchParams = cantoSearchParams;

        return cantoSearchResultDTO;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public List<CantoAssetDTO> getResults() {
        return Collections.unmodifiableList(results);
    }

}
