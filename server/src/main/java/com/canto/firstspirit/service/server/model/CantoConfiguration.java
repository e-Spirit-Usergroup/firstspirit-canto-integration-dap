package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Objects;

public class CantoConfiguration implements Serializable {

  private final String tenant;

  private final String appId;
  private final String appSecret;
  private final String userId;
  private static final long serialVersionUID = 2L;


  public CantoConfiguration(String tenant, String appId, String appSecret, String userId) {
    this.tenant = tenant;
    this.appId = appId;
    this.appSecret = appSecret;
    this.userId = userId;
  }


  public String getTenant() {
    return tenant;
  }

  public String getAppId() {
    return appId;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public String getUserId() {
    return userId;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CantoConfiguration that = (CantoConfiguration) o;
    return tenant.equals(that.tenant) && appId.equals(that.appId) && appSecret.equals(that.appSecret) && userId.equals(that.userId);
  }

  @Override public int hashCode() {
    return Objects.hash(tenant, appId, appSecret, userId);
  }


}
