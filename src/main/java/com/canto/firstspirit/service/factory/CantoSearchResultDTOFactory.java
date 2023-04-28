package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.CantoApi;
import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.CantoSearchParams;
import com.canto.firstspirit.service.server.CantoSearchResultDTO;
import de.espirit.common.base.Logging;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CantoSearchResultDTOFactory {

    public static CantoSearchResultDTO fromCantoSearchResult(CantoSearchParams cantoSearchParams, @NotNull CantoSearchResult cantoSearchResult, CantoApi cantoApi) {

        Logging.logDebug(cantoSearchResult.toString(), CantoSearchResultDTO.class);
        final var total = cantoSearchResult.getFound().intValue();
        final var results = cantoSearchResult.getResults()
                .stream().map(
                r-> CantoAssetDTOFactory.fromAsset(r,cantoApi)).collect(Collectors.toList()
        );

        return new CantoSearchResultDTO(total,
                results,
                cantoSearchParams);
    }
}
