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
   * @param broker project bound broker
   * @return Canto Configuration based on ProjectApp Config
   */
  @NotNull public static CantoConfiguration fromProjectBroker(SpecialistsBroker broker) {
    Values config = CantoProjectApp.getConfig(broker);
    String tenant = config.getString(CantoProjectAppConfiguration.PARAM_TENANT);
    String oAuthBaseUrl = config.getString(CantoProjectAppConfiguration.PARAM_OAUTH_BASE_URL);
    String appId = config.getString(CantoProjectAppConfiguration.PARAM_APP_ID);
    String appSecret = config.getString(CantoProjectAppConfiguration.PARAM_APP_SECRET);
    String userId = config.getString(CantoProjectAppConfiguration.PARAM_USER_ID);

    ProjectAgent projectAgent = broker.requireSpecialist(ProjectAgent.TYPE);
    String projectName = projectAgent.getName();

    if (tenant.isBlank() || appId.isBlank() || appSecret.isBlank() || userId.isBlank() || oAuthBaseUrl.isBlank()) {

      throw new IllegalStateException(
          "ProjectApp Configuration not correct. Please provide tenant, appId, appSecret and UserId Project: ['" + projectName + "', "
              + projectAgent.getId() + "]");
    }

    return new CantoConfiguration(tenant, oAuthBaseUrl, appId, appSecret, userId, projectName);
  }


}
