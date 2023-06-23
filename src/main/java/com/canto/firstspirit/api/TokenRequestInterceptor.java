package com.canto.firstspirit.api;


import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

/**
 * This interceptor compresses the HTTP request body. Many webservers can't handle this!
 */
final class TokenRequestInterceptor implements Interceptor {

  private final String token;

  public TokenRequestInterceptor(String token) {
    this.token = token;
  }

  @NotNull @Override public Response intercept(Interceptor.Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request authorizedRequest = originalRequest.newBuilder()
        .header("Authorization", "Bearer " + this.token)
        .build();
    return chain.proceed(authorizedRequest);
  }

}