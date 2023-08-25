package com.canto.firstspirit.service;

import de.espirit.common.base.Logging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RequestLimiter {

  private static final int MINUTE_IN_MILLISECONDS = 60 * 1000;
  private final int requestsWithoutDelay;

  private final long waitTimeInMillisBetweenCalls;

  private final List<Long> requestTimestampsList;
  ListIterator<Long> listIterator;

  RequestLimiter(int maxRequestsPerMinute, int requestsWithoutDelay, long timeBufferInMilliSeconds) {
    this.requestsWithoutDelay = requestsWithoutDelay;

    this.waitTimeInMillisBetweenCalls = (MINUTE_IN_MILLISECONDS + timeBufferInMilliSeconds) / (maxRequestsPerMinute - requestsWithoutDelay);

    this.requestTimestampsList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(maxRequestsPerMinute, 0L)));
    this.listIterator = requestTimestampsList.listIterator();
  }

  public long getRequestDelay() {
    long currentTime = System.currentTimeMillis();
    if (!listIterator.hasNext()) {
      listIterator = requestTimestampsList.listIterator();
    }
    listIterator.next();

    // start with 1, the current request
    int requestWithinLastMinute = 1;
    for (Long timestamp : requestTimestampsList) {
      if (timestamp + (MINUTE_IN_MILLISECONDS) > currentTime) {
        requestWithinLastMinute++;
      }
    }
    listIterator.set(currentTime);

    if (requestWithinLastMinute > requestsWithoutDelay) {
      Logging.logInfo("[fetchAssetsByIdentifiers] Many Requests within last Minute. Wait for " + waitTimeInMillisBetweenCalls
                          + "ms between api calls to stay within defined API Limits. Num Calls: " + requestWithinLastMinute, this.getClass());
      return waitTimeInMillisBetweenCalls;

    }
    return 0;
  }
}
