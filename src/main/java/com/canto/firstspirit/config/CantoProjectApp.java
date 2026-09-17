package com.canto.firstspirit.config;

import com.espirit.moddev.components.annotations.ProjectAppComponent;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

@ProjectAppComponent(name = "CantoSaasConfiguration", displayName = "Canto Project Configuration", configurable = CantoProjectAppConfiguration.class)
public class CantoProjectApp implements ProjectApp {

  private ProjectAppDescriptor projectAppDescriptor;
  private ProjectEnvironment projectEnvironment;

  @Override public void init(final ProjectAppDescriptor projectAppDescriptor, final ProjectEnvironment projectEnvironment) {
    this.projectEnvironment = projectEnvironment;
    this.projectAppDescriptor = projectAppDescriptor;
  }

  @Override public void installed() {
    // load default settings
    CantoProjectAppConfiguration appConfig = new CantoProjectAppConfiguration();
    appConfig.init(projectAppDescriptor.getModuleName(), projectAppDescriptor.getName(), projectEnvironment);
    appConfig.load();
    appConfig.store();
  }

  @Override public void uninstalling() {
    // stub
  }

  @Override public void updated(final String s) {
    installed();
  }

  public static boolean isInstalled(final SpecialistsBroker broker) {
    return CantoProjectAppConfiguration.isInstalled(CantoProjectApp.class, broker);
  }

  public static Values getConfig(final SpecialistsBroker broker) {
    return CantoProjectAppConfiguration.values(broker, CantoProjectApp.class);
  }
}
