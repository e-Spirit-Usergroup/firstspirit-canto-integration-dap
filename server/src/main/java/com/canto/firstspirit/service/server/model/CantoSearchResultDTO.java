package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CantoSearchResultDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int total;

  private final List<CantoAssetDTO> results;

  private final CantoSearchParams searchParams;

  public CantoSearchResultDTO(int total, List<CantoAssetDTO> results, CantoSearchParams searchParams) {
    this.total = total;
    this.results = results;
    this.searchParams = searchParams;
  }


  public int getTotal() {
    return total;
  }

  public List<CantoAssetDTO> getResults() {
    return Collections.unmodifiableList(results);
  }

  public CantoSearchParams getSearchParams() {
    return searchParams;
  }

}
