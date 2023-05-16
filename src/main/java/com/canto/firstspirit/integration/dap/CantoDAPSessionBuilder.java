package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSessionBuilder implements DataAccessSessionBuilder<CantoDAPAsset> {
    private final SessionBuilderAspectMap sessionBuilderAspectMap = new SessionBuilderAspectMap();

    private CantoDAPSession cachedSession = null;

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
        // We only want to cache Sessions in the SiteArchitect to ensure separation of contexts on ServerSide.
        if(baseContext.is(BaseContext.Env.ARCHITECT)) {
            if(cachedSession == null) {
                Logging.logInfo("Creating cached Session for BaseContext=" + baseContext, this.getClass());
                cachedSession = new CantoDAPSession(baseContext);
            }
            Logging.logInfo("returning cached Session for BaseContext=" + baseContext, this.getClass());
            return cachedSession;
        }
        return new CantoDAPSession(baseContext);
    }
}
