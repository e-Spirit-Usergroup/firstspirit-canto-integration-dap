package com.canto.firstspirit.service;

import de.espirit.common.base.Logging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Thread Safe implementation.
 * Makes sure, that Api Limits are retained via {@link #delayRequestIfNecessary()}
 */
public class RequestLimiter {

  private static final int MINUTE_IN_MILLISECONDS = 60 * 1000;
  private final int requestsWithoutDelay;

  private final long waitTimeInMillisBetweenCalls;

  private final List<Long> requestTimestampsList;
  ListIterator<Long> listIterator;

  RequestLimiter(int maxRequestsPerMinute, int requestsWithoutDelay, long timeBufferInMilliSeconds) {
    this.requestsWithoutDelay = requestsWithoutDelay;

    this.waitTimeInMillisBetweenCalls = (MINUTE_IN_MILLISECONDS + timeBufferInMilliSeconds) / (maxRequestsPerMinute - requestsWithoutDelay);

    long currentTimestamp = System.currentTimeMillis();

    this.requestTimestampsList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(maxRequestsPerMinute, currentTimestamp)));
    this.listIterator = requestTimestampsList.listIterator();
  }


  private int calculateApiCallsWithinLastMinute() {
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
    return requestWithinLastMinute;
  }

  /**
   * This function performs Thread.sleep if necessary to ensure
   * to stay within Api Call limits.
   * Synchronized to make sure all calls wait subsequent and not parallel
   */
  synchronized public void delayRequestIfNecessary() {
    int requestWithinLastMinute = calculateApiCallsWithinLastMinute();
    if (requestWithinLastMinute > requestsWithoutDelay) {
      Logging.logInfo("[delayRequestIfNecessary] Many Requests within last Minute. Wait for " + waitTimeInMillisBetweenCalls
                          + "ms between api calls to stay within defined API Limits. Num Calls: " + requestWithinLastMinute, this.getClass());

      try {
        Thread.sleep(waitTimeInMillisBetweenCalls);
      } catch (InterruptedException e) {
        throw new RuntimeException("delayRequestIfNecessary was interrupted", e);
      }

    }
  }
}
