package com.canto.firstspirit.api;

import com.canto.firstspirit.service.cache.CentralCache;
import org.junit.jupiter.api.Test;

public class CentralCachingTest {

  @Test void testCacheHitsAndMisses() {

    CantoApi cantoApi = new CantoApi("TENANT", "OAUTHURL", "APP_ID", "APP_SECRET", "USER_ID", new CentralCache(), null);


  }


}
