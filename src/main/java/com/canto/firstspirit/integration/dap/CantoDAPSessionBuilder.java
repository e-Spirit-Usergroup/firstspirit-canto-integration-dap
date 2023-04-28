package com.canto.firstspirit.integration.dap;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSessionBuilder implements DataAccessSessionBuilder<CantoDAPAsset> {
    private final SessionBuilderAspectMap sessionBuilderAspectMap = new SessionBuilderAspectMap();

    public CantoDAPSessionBuilder() {
        Logging.logInfo("CantoDAPSessionBuilder Created", this.getClass());

    }

    @Override
    public <A> A getAspect(@NotNull SessionBuilderAspectType<A> sessionBuilderAspectType) {
        return this.sessionBuilderAspectMap.get(sessionBuilderAspectType);
    }

    @NotNull
    @Override
    public DataAccessSession<CantoDAPAsset> createSession(@NotNull BaseContext baseContext) {
        return new CantoDAPSession(baseContext);
    }
}
