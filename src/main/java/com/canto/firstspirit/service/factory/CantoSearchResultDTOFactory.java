package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import de.espirit.common.base.Logging;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CantoSearchResultDTOFactory {

  public static CantoSearchResultDTO fromCantoSearchResult(CantoSearchParams cantoSearchParams, @NotNull CantoSearchResult cantoSearchResult) {

    Logging.logDebug(cantoSearchResult.toString(), CantoSearchResultDTO.class);
    final var total = cantoSearchResult.getFound()
        .intValue();
    final var results = cantoSearchResult.getResults()
        .stream()
        .map(CantoAssetDTOFactory::fromAsset)
        .collect(Collectors.toList());

    return new CantoSearchResultDTO(total, results, cantoSearchParams);
  }
}
