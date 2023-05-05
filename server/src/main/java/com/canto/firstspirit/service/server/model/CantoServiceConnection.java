package com.canto.firstspirit.service.server;

import java.io.Serializable;

public class CantoServiceConnection implements Serializable {
    private final int connectionId;
    private static final long serialVersionUID = 1L;

    public CantoServiceConnection(final int connectionId) {
        this.connectionId = connectionId;
    }

    public static CantoServiceConnection fromConfig(final CantoConfiguration configuration) {
        return new CantoServiceConnection(configuration.hashCode());
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof CantoServiceConnection
                && connectionId == ((CantoServiceConnection) other).getConnectionId());
    }

    public int getConnectionId() {
        return connectionId;
    }
}
