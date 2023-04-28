package com.canto.firstspirit.service.server;

import java.io.Serializable;
import java.util.Objects;

public class CantoConfiguration implements Serializable {
    private final String tenant;
    private final String token;
    private final String mdc_domain;
    private final String mdc_account_id;
    private static final long serialVersionUID = 1L;

    public String getTenant() {
        return tenant;
    }
    public String getToken() {
        return token;
    }
    public String getMDCDomain() { return mdc_domain; }
    public String getMDCAccountId() { return mdc_account_id; }

    public CantoConfiguration(String tenant, String token, String mdc_domain, String mdc_account_id) {
        this.tenant = tenant;
        this.token = token;
        this.mdc_domain = mdc_domain;
        this.mdc_account_id = mdc_account_id;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CantoConfiguration that = (CantoConfiguration) o;
        return tenant.equals(that.tenant) && token.equals(that.token) && mdc_domain.equals(that.mdc_domain) && mdc_account_id.equals(that.mdc_account_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, token, mdc_domain, mdc_account_id);
    }
}
