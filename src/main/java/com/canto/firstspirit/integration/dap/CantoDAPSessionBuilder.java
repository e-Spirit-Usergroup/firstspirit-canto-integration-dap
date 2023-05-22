package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CantoDAPSessionBuilder implements DataAccessSessionBuilder<CantoDAPAsset> {
    private final SessionBuilderAspectMap sessionBuilderAspectMap = new SessionBuilderAspectMap();

    static private CantoDAPSession CACHED_SESSION = null;

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
        if (baseContext.is(BaseContext.Env.ARCHITECT)) {
            if (CACHED_SESSION == null) {
                Logging.logInfo("Creating cached Session for BaseContext=" + baseContext, this.getClass());
                CACHED_SESSION = new CantoDAPSession(baseContext);
            }
        }
        Logging.logInfo("Create Session Called. Returning " + (CACHED_SESSION != null ? "CACHED_SESSION" : "new CantoDAPSession"), this.getClass());
        return CACHED_SESSION != null ? CACHED_SESSION : new CantoDAPSession(baseContext);
    }

}
