package com.canto.firstspirit.config;

import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import de.espirit.firstspirit.module.ProjectEnvironment;

public class CantoProjectAppConfiguration extends GenericConfigPanel<ProjectEnvironment> {


    public static final String PARAM_TENANT = "tenant";
    public static final String PARAM_TOKEN = "token";

    @Override
    protected void configure() {
        builder()
                .text("Tenant",PARAM_TENANT,"","Enter the name of the canto tenant, e.g. my-company.canto.global")
                .text("Token", PARAM_TOKEN, "", "Canto client credentials token");
    }
}
