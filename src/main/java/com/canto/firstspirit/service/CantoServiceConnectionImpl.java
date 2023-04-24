package com.canto.firstspirit.service;

import com.canto.firstspirit.service.server.CantoConfiguration;
import com.canto.firstspirit.service.server.CantoServiceConnection;

public class CantoServiceConnectionImpl implements CantoServiceConnection {
    private final int connectionId;
    private static final long serialVersionUID = 1L;

    public CantoServiceConnectionImpl(final int connectionId) {
        this.connectionId = connectionId;
    }

    public static CantoServiceConnection fromConfig(final CantoConfiguration configuration) {
        return new CantoServiceConnectionImpl(configuration.hashCode());
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof CantoServiceConnection
                && connectionId == ((CantoServiceConnection) other).getConnectionId());
    }

    @Override
    public int getConnectionId() {
        return connectionId;
    }
}
