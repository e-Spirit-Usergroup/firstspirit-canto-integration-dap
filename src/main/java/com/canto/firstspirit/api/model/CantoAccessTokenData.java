package com.canto.firstspirit.api.model;

import org.jetbrains.annotations.NotNull;

public class CantoAccessTokenData {

  private final @NotNull String access_token;
  private final long expires_in;

  CantoAccessTokenData(@NotNull String accessToken, long expiresIn) {
    access_token = accessToken;
    expires_in = expiresIn;
  }

  public String getAccessToken() {
    return access_token;
  }

  public long getExpiresInMs() {
    return expires_in * 1000;
  }

  static public boolean isValid(CantoAccessTokenData data) {
    return data != null && data.getAccessToken() != null && !data.getAccessToken()
        .isBlank() && data.getExpiresInMs() > 0;
  }

  @Override public String toString() {
    //noinspection ConstantValue
    final String tokenPart = (access_token != null && !access_token.isBlank()) ? access_token.substring(0, 6) + "..." : "EMPTY";
    return "[AccessTokenData: Token: " + tokenPart + ", ExpiresIn: " + expires_in + "]";
  }

}
