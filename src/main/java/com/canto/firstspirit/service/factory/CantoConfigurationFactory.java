package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.config.CantoProjectApp;
import com.canto.firstspirit.config.CantoProjectAppConfiguration;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import org.jetbrains.annotations.NotNull;

public class CantoConfigurationFactory {

  /**
   * Get a configuration from a broker with project binding
   *
   * @param broker          project bound broker
   * @param apiTenant
   * @param apiOAuthBaseUrl
   * @param apiAppId
   * @param apiAppSecret
   * @return Canto Configuration based on ProjectApp Config
   */
  @NotNull public static CantoConfiguration fromProjectBroker(SpecialistsBroker broker, String apiTenant, String apiOAuthBaseUrl, String apiAppId, String apiAppSecret) {
    Values config = CantoProjectApp.getConfig(broker);

    String userId = config.getString(CantoProjectAppConfiguration.PARAM_USER_ID);

    ProjectAgent projectAgent = broker.requireSpecialist(ProjectAgent.TYPE);
    String projectName = projectAgent.getName();

    if (apiTenant.isBlank() || apiAppId.isBlank() || apiAppSecret.isBlank() || userId.isBlank() || apiOAuthBaseUrl.isBlank()) {

      throw new IllegalStateException("ProjectApp Configuration not correct. Please provide tenant, appId, appSecret and UserId Project: ['" + projectName + "', " + projectAgent.getId() + "]");
    }

    return new CantoConfiguration(apiTenant, apiOAuthBaseUrl, apiAppId, apiAppSecret, userId, projectName);
  }


}
