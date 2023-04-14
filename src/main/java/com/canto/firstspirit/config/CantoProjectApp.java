package com.canto.firstspirit.config;

import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import com.espirit.moddev.components.annotations.ProjectAppComponent;
import com.espirit.ps.psci.genericconfiguration.Values;

@ProjectAppComponent(
        name = "CantoSaasConfiguration",
        displayName = "Canto Project Configuration",
        configurable = com.canto.firstspirit.config.CantoProjectAppConfiguration.class)
public class CantoProjectApp implements ProjectApp {

    @Override
    public void init(final ProjectAppDescriptor projectAppDescriptor, final ProjectEnvironment projectEnvironment) {
        // stub
    }

    @Override
    public void installed() {
        // stub
    }

    @Override
    public void uninstalling() {
        // stub
    }

    @Override
    public void updated(final String s) {

    }

    public static boolean isInstalled(final SpecialistsBroker broker){
        return CantoProjectAppConfiguration.isInstalled(CantoProjectApp.class, broker);
    }

    public static Values getConfig(final SpecialistsBroker broker){
        return CantoProjectAppConfiguration.values(broker, CantoProjectApp.class);
    }
}
