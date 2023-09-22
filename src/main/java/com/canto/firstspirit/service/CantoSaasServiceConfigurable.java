package com.canto.firstspirit.service;

import static com.canto.firstspirit.service.CantoSaasServiceConfigurable.ServiceConfiguration.defaultConfiguration;
import static com.canto.firstspirit.service.CantoSaasServiceImpl.SERVICE_NAME;

import com.espirit.ps.psci.genericconfiguration.EventListener;
import com.espirit.ps.psci.genericconfiguration.EventType;
import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.ModuleAdminAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ServerEnvironment;

public class CantoSaasServiceConfigurable extends GenericConfigPanel<ServerEnvironment> {


  public static final String PARAM_REQUESTS_WITHOUT_DELAY = "requestsWithoutDelay";
  public static final String PARAM_MAX_REQUESTS_PER_MINUTE = "maxRequestsPerMinute";
  public static final String PARAM_TIME_BUFFER_IN_MS = "timeBufferInMs";
  public static final String PARAM_USE_CACHE = "useCache";
  public static final String PARAM_USE_REQUEST_LIMITER = "useRequestLimiter";
  public static final String PARAM_RESTART_SERVICE_ON_SAVE = "restartServiceOnSave";
  public static final String PARAM_TENANT = "tenant";
  public static final String PARAM_APP_ID = "appId";
  public static final String PARAM_APP_SECRET = "appSecret";
  public static final String PARAM_USER_ID = "userId";
  public static final String PARAM_OAUTH_BASE_URL = "OAuthBaseUrl";

  @Override protected void configure() {

    // Restart Canto Service on Config Change only if Restart On Save Checkbox is checked
    EventListener restartServiceAfterStoreListener = new EventListener() {

      @Override public void handleEvent(EventType eventType) {
        if (eventType.equals(EventType.AFTER_STORE)) {
          try {
            Boolean restartOnSave = getFormValue(PARAM_RESTART_SERVICE_ON_SAVE);
            if (restartOnSave != null && restartOnSave) {

              Logging.logInfo("Service Configuration changed. Restarting Canto Service", this.getClass());
              SpecialistsBroker broker = getEnvironment().getBroker();

              ModuleAdminAgent moduleAdminAgent = broker.requireSpecialist(ModuleAdminAgent.TYPE);

              moduleAdminAgent.stopService(SERVICE_NAME);
              moduleAdminAgent.startService(SERVICE_NAME);
              Logging.logInfo("Restart Successful", this.getClass());
            }
          } catch (Exception e) {
            Logging.logError("Unable to restart CantoService after Config Change", e, this.getClass());
          }

        }
      }
    };

    addListener(restartServiceAfterStoreListener);

    builder().checkbox("Use Request Limiter (recommended)",
                       PARAM_USE_REQUEST_LIMITER,
                       defaultConfiguration.useRequestLimiter,
                       "Limits requests per Minute to set bounds, ensuring availability of api")
        .text("[RequestLimiter] Max Requests per Minute",
              PARAM_MAX_REQUESTS_PER_MINUTE,
              String.valueOf(defaultConfiguration.maxRequestsPerMinute),
              "use Canto Api Limit")
        .text("[RequestLimiter] Requests per Minute without delay",
              PARAM_REQUESTS_WITHOUT_DELAY,
              String.valueOf(defaultConfiguration.requestsWithoutDelay),
              "The first x requests are not delayed. All remaining requests are delayed as long as needed, to ensure staying within Max Requests per Minute")
        .hiddenString(PARAM_TIME_BUFFER_IN_MS, String.valueOf(defaultConfiguration.timeBufferInMs))
        .checkbox("Use Cache (recommended)", PARAM_USE_CACHE, defaultConfiguration.useCache, "Caching is crucial for performance")
        .text("Tenant (without https://)", PARAM_TENANT, "", "Enter the name of the canto tenant, e.g. my-company.canto.global")
        .text("OAuth Base URL (com,de,global)", PARAM_OAUTH_BASE_URL, "https://oauth.canto.<region>", "region is one of com/de/global")
        .text("App Id", PARAM_APP_ID, "", "App Id")
        .text("App Secret", PARAM_APP_SECRET, "", "App Secret")
        .text("User", PARAM_USER_ID, "", "User Id bound to generated Access Token")
        .checkbox("Restart Service on Config Save",
                  PARAM_RESTART_SERVICE_ON_SAVE,
                  defaultConfiguration.restartServiceOnSave,
                  "When activated, the Canto Service restarts after closing this window with 'OK'");

  }

  static class ServiceConfiguration {

    static final ServiceConfiguration defaultConfiguration = new ServiceConfiguration(true, true, 200, 30, 1000, true, "", "", "", "", "");

    final boolean useCache;
    final boolean useRequestLimiter;

    final int requestsWithoutDelay;
    final int maxRequestsPerMinute;
    final long timeBufferInMs;

    final boolean restartServiceOnSave;
    final String apiTenant;
    final String apiOAuthBaseUrl;
    final String apiAppId;
    final String apiAppSecret;
    final String apiUserId;

    private ServiceConfiguration(boolean useCache, boolean useRequestLimiter, int maxRequestsPerMinute, int requestsWithoutDelay, long timeBufferInMs,
        boolean restartServiceOnSave, String apiTenant, String apiOAuthBaseUrl, String apiAppId, String apiAppSecret, String apiUserId) {

      this.useCache = useCache;
      this.useRequestLimiter = useRequestLimiter;
      this.requestsWithoutDelay = requestsWithoutDelay;
      this.maxRequestsPerMinute = maxRequestsPerMinute;
      this.timeBufferInMs = timeBufferInMs;
      this.restartServiceOnSave = restartServiceOnSave;
      this.apiTenant = apiTenant;
      this.apiOAuthBaseUrl = apiOAuthBaseUrl;
      this.apiAppId = apiAppId;
      this.apiAppSecret = apiAppSecret;
      this.apiUserId = apiUserId;
    }

    static ServiceConfiguration fromServerEnvironment(ServerEnvironment serverEnvironment) {
      try {
        Values configValues = CantoSaasServiceConfigurable.values(serverEnvironment);
        return new ServiceConfiguration(configValues.getBoolean(PARAM_USE_CACHE, true),
                                        configValues.getBoolean(PARAM_USE_REQUEST_LIMITER, true),
                                        Integer.parseInt(configValues.getString(PARAM_REQUESTS_WITHOUT_DELAY, "30")),
                                        Integer.parseInt(configValues.getString(PARAM_MAX_REQUESTS_PER_MINUTE, "200")),
                                        Long.parseLong(configValues.getString(PARAM_TIME_BUFFER_IN_MS, "1000")),
                                        configValues.getBoolean(PARAM_RESTART_SERVICE_ON_SAVE, true),
                                        configValues.getString(PARAM_TENANT, ""),
                                        configValues.getString(PARAM_OAUTH_BASE_URL, ""),
                                        configValues.getString(PARAM_APP_ID, ""),
                                        configValues.getString(PARAM_APP_SECRET, ""),
                                        configValues.getString(PARAM_USER_ID, ""));

      } catch (Exception e) {
        Logging.logError("Unable to parse Server Configuration! Check set values. Using default Config as fallback", e, ServiceConfiguration.class);
        return defaultConfiguration;
      }

    }

    @Override public String toString() {
      final StringBuilder sb = new StringBuilder("ServiceConfiguration{");
      sb.append("useCache=")
          .append(useCache);
      sb.append(", useRequestLimiter=")
          .append(useRequestLimiter);
      sb.append(", requestsWithoutDelay=")
          .append(requestsWithoutDelay);
      sb.append(", maxRequestsPerMinute=")
          .append(maxRequestsPerMinute);
      sb.append(", timeBufferInMs=")
          .append(timeBufferInMs);
      sb.append(", restartServiceOnSave=")
          .append(restartServiceOnSave);
      sb.append('}');
      return sb.toString();
    }

  }

}
