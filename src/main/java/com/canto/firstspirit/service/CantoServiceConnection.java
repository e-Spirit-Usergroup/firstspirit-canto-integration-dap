package com.canto.firstspirit.service;

import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CantoServiceConnection that = (CantoServiceConnection) o;
        return connectionId == that.connectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId);
    }
}
