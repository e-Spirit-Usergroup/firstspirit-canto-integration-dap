package com.canto.firstspirit.config;

import com.espirit.ps.psci.genericconfiguration.GenericConfigPanel;
import de.espirit.firstspirit.module.ProjectEnvironment;

public class CantoProjectAppConfiguration extends GenericConfigPanel<ProjectEnvironment> {


    public static final String PARAM_TENANT = "tenant";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_MDC_DOMAIN = "mdc_domain";
    public static final String PARAM_MDC_ACCOUNT_ID = "mdc_account_id";


    @Override
    protected void configure() {
        builder()
                .text("Tenant",PARAM_TENANT,"","Enter the name of the canto tenant, e.g. my-company.canto.global")
                .text("Token", PARAM_TOKEN, "", "Canto client credentials token")
                .text("MDC Domain",PARAM_MDC_DOMAIN,"","Enter the name of the MDC domain, e.g. xyz1742.cloudfront.net")
                .text("MDC Account ID",PARAM_MDC_ACCOUNT_ID,"","Enter the MDC account ID, e.g. your AWS Account ID");
                /*
                .text("Client ID", PARAM_CLIENTID, "", "Canto Client ID")
                .text("Client Secret", PARAM_CLIENTSECRET, "", "Canto Client Secret")
                ;
                 */
    }
}
