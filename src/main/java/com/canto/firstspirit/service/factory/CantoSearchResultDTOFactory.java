package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.api.model.CantoSearchResult;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import de.espirit.common.base.Logging;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CantoSearchResultDTOFactory {

  public static CantoSearchResultDTO fromCantoSearchResult(CantoSearchParams cantoSearchParams, @NotNull CantoSearchResult cantoSearchResult) {
    int total = 0;
    List<CantoAssetDTO> results = new ArrayList<>();

    if (cantoSearchResult.getFound() != null && cantoSearchResult.getResults() != null) {
      Logging.logDebug(cantoSearchResult.toString(), CantoSearchResultDTO.class);
      total = cantoSearchResult.getFound()
          .intValue();
      results = cantoSearchResult.getResults()
          .stream()
          .map(CantoAssetDTOFactory::fromAsset)
          .collect(Collectors.toList());
    }

    return new CantoSearchResultDTO(total, results, cantoSearchParams);
  }
}
