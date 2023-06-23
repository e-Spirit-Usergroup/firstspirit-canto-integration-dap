package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

public class CantoSearchParams implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int start;
  private final int limit;
  private final String keyword;

  @Nullable
  private final String scheme;

  public CantoSearchParams(int start, int limit, String keyword, @Nullable String scheme) {
    this.start = start;
    this.limit = limit;
    this.keyword = keyword;
    this.scheme = scheme;
  }

  public String getKeyword() {
    return keyword;
  }

  public int getLimit() {
    return limit;
  }

  public int getStart() {
    return start;
  }

  public @Nullable String getScheme() {
    return scheme;
  }

  @Override public String toString() {
    return "CantoSearchParams {" + "start=" + start + ", limit=" + limit + ", keyword='" + keyword + "', scheme=" + scheme + '}';
  }

}
