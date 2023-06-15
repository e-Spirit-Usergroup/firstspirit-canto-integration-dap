package com.canto.firstspirit.config;

import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import de.espirit.firstspirit.module.ProjectEnvironment;

public class CantoProjectAppConfiguration extends GenericConfigPanel<ProjectEnvironment> {


  public static final String PARAM_TENANT = "tenant";
  public static final String PARAM_APP_ID = "appId";
  public static final String PARAM_APP_SECRET = "appSecret";
  public static final String PARAM_USER_ID = "userId";
  public static final String PARAM_OAUTH_BASE_URL = "OAuthBaseUrl";

  @Override protected void configure() {
    builder().text("Tenant (without https://)", PARAM_TENANT, "", "Enter the name of the canto tenant, e.g. my-company.canto.global")
        .text("OAuth Base URL (com,de,global)", PARAM_OAUTH_BASE_URL, "https://oauth.canto.<region>", "region is one of com/de/global")
        .text("App Id", PARAM_APP_ID, "", "App Id")
        .text("App Secret", PARAM_APP_SECRET, "", "App Secret")
        .text("User Id", PARAM_USER_ID, "", "User Id bound to generated Access Token");

  }


}
