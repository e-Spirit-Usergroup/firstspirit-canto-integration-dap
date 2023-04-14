package com.canto.firstspirit.service;

import java.io.Serializable;
import java.util.List;

public interface CantoSearchResultDTO extends Serializable {
    int getTotal();

    List<CantoAssetDTO> getResults();
}
