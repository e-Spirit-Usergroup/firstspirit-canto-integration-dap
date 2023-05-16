package com.canto.firstspirit.service.server.model;

import java.io.Serializable;
import java.util.Objects;

public class CantoConfiguration implements Serializable {
    private final String tenant;
    private final String token;
    private static final long serialVersionUID = 1L;

    public String getTenant() {
        return tenant;
    }
    public String getToken() {
        return token;
    }
    public CantoConfiguration(String tenant, String token) {
        this.tenant = tenant;
        this.token = token;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CantoConfiguration that = (CantoConfiguration) o;
        return tenant.equals(that.tenant) && token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, token);
    }
}
