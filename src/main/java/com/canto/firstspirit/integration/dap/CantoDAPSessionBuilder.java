package com.canto.firstspirit.integration.dap;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import com.espirit.moddev.components.annotations.WebAppComponent;
import com.espirit.moddev.components.annotations.WebResource;
import static de.espirit.firstspirit.module.descriptor.WebAppDescriptor.WebAppScope.GLOBAL;


public class CantoDAPSessionBuilder implements DataAccessSessionBuilder<CantoDAPAsset> {
    private final BaseContext context;
    private SessionBuilderAspectMap sessionBuilderAspectMap = new SessionBuilderAspectMap();

    public CantoDAPSessionBuilder(BaseContext context) {
        this.context = context;
    }

    @Override
    public <A> A getAspect(SessionBuilderAspectType<A> sessionBuilderAspectType) {
        return this.sessionBuilderAspectMap.get(sessionBuilderAspectType);
    }

    @Override
    public DataAccessSession<CantoDAPAsset> createSession(BaseContext baseContext) {
        return new CantoDAPSession(baseContext);
    }
}
