package com.canto.firstspirit.config;

import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import de.espirit.firstspirit.module.ProjectEnvironment;

public class CantoProjectAppConfiguration extends GenericConfigPanel<ProjectEnvironment> {


  public static final String PARAM_TENANT = "tenant";
  public static final String PARAM_APP_ID = "appId";
  public static final String PARAM_APP_SECRET = "appSecret";
  public static final String PARAM_USER_ID = "userId";

  @Override protected void configure() {
    builder().text("Tenant", PARAM_TENANT, "", "Enter the name of the canto tenant, e.g. my-company.canto.global")
        .text("App Id", PARAM_APP_ID, "", "App Id")
        .text("App Secret", PARAM_APP_SECRET, "", "App Secret")
        .text("User Id", PARAM_USER_ID, "", "User Id bound to generated Access Token");
  }


}
