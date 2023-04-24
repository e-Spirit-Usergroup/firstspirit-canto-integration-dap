package com.canto.firstspirit.service.server;

import java.io.Serializable;

public interface CantoConfiguration extends Serializable {
    String getTenant();

    String getToken();

    String getMDCDomain();

    String getMDCAccountId();
}
