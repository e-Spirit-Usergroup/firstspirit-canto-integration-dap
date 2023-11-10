package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Objects;

public class CantoConfiguration implements Serializable {

  private final String tenant;
  private final String oAuthBaseUrl;

  private final String appId;
  private final String appSecret;
  private final String userId;
  private static final long serialVersionUID = 2L;

  private final String projectName;


  public CantoConfiguration(String tenant, String oAuthBaseUrl, String appId, String appSecret, String userId, String projectName) {
    this.tenant = tenant;
    this.oAuthBaseUrl = oAuthBaseUrl;
    this.appId = appId;
    this.appSecret = appSecret;
    this.userId = userId;
    this.projectName = projectName;
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


  public String getOAuthBaseUrl() {
    return oAuthBaseUrl;
  }

  @Override public String toString() {
    return "CantoConfiguration[" + projectName + "]" + "{tenant='" + tenant + "', oAuthBaseUrl='" + oAuthBaseUrl + "', appId='" + appId.substring(0,
                                                                                                                                                  5)
        + "...', appSecret='" + appSecret.substring(0, 5) + "...', userId='" + userId + "'}";
  }

}
