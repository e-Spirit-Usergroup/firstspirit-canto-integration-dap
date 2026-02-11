package com.canto.firstspirit.config;

import static com.canto.firstspirit.service.CantoSaasServiceImpl.SERVICE_NAME;

import com.espirit.ps.psci.genericconfiguration.EventListener;
import com.espirit.ps.psci.genericconfiguration.EventType;
import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.ModuleAdminAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectEnvironment;

public class CantoProjectAppConfiguration extends GenericConfigPanel<ProjectEnvironment> {


  public static final String PARAM_USER_ID = "userId";
  public static final String PARAM_RESTART_SERVICE_ON_SAVE = "restartServiceOnSave";

  @Override protected void configure() {

    // Restart Canto Service on Config Change only if Restart On Save Checkbox is checked
    EventListener restartServiceAfterStoreListener = new EventListener() {

      @Override public void handleEvent(EventType eventType) {
        if (eventType.equals(EventType.AFTER_STORE)) {
          try {
            Boolean restartOnSave = getFormValue(PARAM_RESTART_SERVICE_ON_SAVE);
            if (restartOnSave != null && restartOnSave) {

              Logging.logInfo("ProjectApp Configuration changed. Restarting Canto Service", this.getClass());
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

    builder().text("User", PARAM_USER_ID, "", "User Id bound to generated Access Token")
        .checkbox("Restart Service on Config Save", PARAM_RESTART_SERVICE_ON_SAVE, false, "When activated, the Canto Service restarts after closing this window with 'OK'");

  }

}
