package com.canto.firstspirit.service;

import com.canto.firstspirit.config.CantoProjectApp;
import com.canto.firstspirit.config.CantoProjectAppConfiguration;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import org.jetbrains.annotations.NotNull;


import java.util.Objects;

public class CantoConfigurationImpl implements CantoConfiguration {
    private String tenant;
    private String token;
    private String mdc_domain;
    private String mdc_account_id;
    private static final long serialVersionUID = 1L;

    @Override
    public String getTenant() {
        return tenant;
    }

    @Override
    public String getToken() {
        return token;
    }
    @Override
    public String getMDCDomain() { return mdc_domain; }
    @Override
    public String getMDCAccountId() { return mdc_account_id; }

    private CantoConfigurationImpl(@NotNull String tenant, @NotNull String token, @NotNull String mdc_domain, @NotNull String mdc_account_id ) {
        this.tenant = tenant;
        this.token = token;
        this.mdc_domain = mdc_domain;
        this.mdc_account_id = mdc_account_id;

    }

    /**
     * Get a configuration from a broker with project binding
     * @param broker
     * @return
     */
    @NotNull
    public static CantoConfigurationImpl fromProjectBroker(SpecialistsBroker broker){
        Values config = CantoProjectApp.getConfig(broker);
        String tenant = config.getString(CantoProjectAppConfiguration.PARAM_TENANT);
        String token = config.getString(CantoProjectAppConfiguration.PARAM_TOKEN);
        String mdc_domain = config.getString(CantoProjectAppConfiguration.PARAM_MDC_DOMAIN);
        String mdc_account_id = config.getString(CantoProjectAppConfiguration.PARAM_MDC_ACCOUNT_ID);
        return new CantoConfigurationImpl(tenant,token,mdc_domain,mdc_account_id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CantoConfigurationImpl that = (CantoConfigurationImpl) o;
        return tenant.equals(that.tenant) && token.equals(that.token) && mdc_domain.equals(that.mdc_domain) && mdc_account_id.equals(that.mdc_account_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, token, mdc_domain, mdc_account_id);
    }
}
