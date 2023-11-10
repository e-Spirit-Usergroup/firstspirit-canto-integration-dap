package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.gom.GomConfigurableAspect;
import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.GomConfigurable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSessionBuilder implements DataAccessSessionBuilder<CantoDAPAsset> {

  private final SessionBuilderAspectMap sessionBuilderAspectMap = new SessionBuilderAspectMap();
  private final GomConfigurableAspect gomConfig = new GomConfigurableAspect();

  public CantoDAPSessionBuilder() {
    Logging.logDebug("CantoDAPSessionBuilder Created", this.getClass());
    sessionBuilderAspectMap.put(GomConfigurable.TYPE, gomConfig);
  }

  @Override public <A> A getAspect(@NotNull SessionBuilderAspectType<A> sessionBuilderAspectType) {
    return this.sessionBuilderAspectMap.get(sessionBuilderAspectType);
  }

  @NotNull @Override public DataAccessSession<CantoDAPAsset> createSession(@NotNull BaseContext baseContext) {
    CantoDAPFilter filter = new CantoDAPFilter(gomConfig.getScheme());
    return new CantoDAPSession(baseContext, filter);
  }

}
