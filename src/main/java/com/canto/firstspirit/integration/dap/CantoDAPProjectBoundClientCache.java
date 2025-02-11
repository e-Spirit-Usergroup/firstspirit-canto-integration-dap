package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.service.CantoSaasServiceProjectBoundClient;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.ProjectAgent;
import java.util.HashMap;
import org.jetbrains.annotations.Nullable;

class CantoDAPProjectBoundClientCache {

  private final HashMap<Long, CachedProjectBoundClient> cachedProjectBoundClients = new HashMap<>();
  private final static long TIME_CACHE_VALID_MS = 10000;

  /**
   * Get ProjectBoundClient from Cache. New Client will be created and added to Cache, if cache missed
   *
   * @param context Project Specific Context
   * @return CantoSaasServiceProjectBoundClient
   */
  @Nullable CantoSaasServiceProjectBoundClient getProjectBoundClient(BaseContext context) {

    long projectId = context.requireSpecialist(ProjectAgent.TYPE)
        .getId();

    CachedProjectBoundClient cachedProjectBoundClient = cachedProjectBoundClients.get(projectId);

    if (cachedProjectBoundClient != null && System.currentTimeMillis() < cachedProjectBoundClient.validUntilTimestamp) {
      Logging.logDebug("ProjectBoundClient served from Cache for project " + projectId, getClass());
      return cachedProjectBoundClient.projectBoundClient;
    }

    return createProjectBoundClientAndAddToCache(context);
  }

  private CantoSaasServiceProjectBoundClient createProjectBoundClientAndAddToCache(BaseContext context) {
    CantoSaasServiceProjectBoundClient cantoSaasServiceProjectBoundClient = new CantoSaasServiceProjectBoundClient(context);
    long projectId = context.requireSpecialist(ProjectAgent.TYPE)
        .getId();

    Logging.logDebug("ProjectBoundClient created for project " + projectId, getClass());

    cachedProjectBoundClients.put(projectId, new CachedProjectBoundClient(cantoSaasServiceProjectBoundClient));
    return cantoSaasServiceProjectBoundClient;
  }


  private static class CachedProjectBoundClient {

    private final CantoSaasServiceProjectBoundClient projectBoundClient;
    private final long validUntilTimestamp;

    private CachedProjectBoundClient(CantoSaasServiceProjectBoundClient projectBoundClient) {
      this.projectBoundClient = projectBoundClient;
      this.validUntilTimestamp = System.currentTimeMillis() + TIME_CACHE_VALID_MS;
    }
  }
}
