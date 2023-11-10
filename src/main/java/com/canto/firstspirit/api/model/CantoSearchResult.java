package com.canto.firstspirit.api.model;

import java.util.List;

@SuppressWarnings("unused")
public class CantoSearchResult {

  private String sortBy;

  private String sortDirection;

  private Long found;

  private Long limit;

  private List<CantoAsset> results;

  public CantoSearchResult(String sortBy, String sortDirection, Long found, Long limit, List<CantoAsset> results) {
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.found = found;
    this.limit = limit;
    this.results = results;
  }

  public CantoSearchResult() {
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
  }

  public Long getFound() {
    return found;
  }

  public void setFound(Long found) {
    this.found = found;
  }

  public Long getLimit() {
    return limit;
  }

  public void setLimit(Long limit) {
    this.limit = limit;
  }

  public List<CantoAsset> getResults() {
    return results;
  }

  public void setResults(List<CantoAsset> results) {
    this.results = results;
  }
}
