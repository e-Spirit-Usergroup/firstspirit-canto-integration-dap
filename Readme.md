# Canto SaaS Integration

This module provides means to reference Canto media within FirstSpirit.

The JavaDoc documentation is a good place to start, when using this module as a Developer.

## Configuration

The module requires configuration on a server and project level.  
To enable the DAP for a project, the ProjectApp needs to be added and configured.

### Server Configuration

Install the module.  
Open **Service Configuration**. Short config overview:

* __Use Request Limiter__:  
  Makes sure the server respects the API limits imposed by Canto.

* __Max Requests per Minute__:  
  If you use this module across __different servers__ for the same Canto account, make sure to adjust _Max Requests per Minute_ accordingly, such that added up, all requests stay within your account limit.

* __Requests without Delay__:  
  The request limiter delays API requests based on your max calls per minute.  
  The first X calls are not delayed, the remaining calls are uniformly spread over 60 seconds.  
  This value should be significantly lower than the max requests per minute.

* __UseCache__:  
  Activates server-side cache. Highly recommended to reduce API load.

* __Cache Element Size__:  
  Maximum number of elements in the cache.  
  This limit is not strictly enforced and may be exceeded temporarily (30–60 seconds), e.g. during generations.

* __Cache Item Lifespan in ms__:  
  Defines how long a cache entry is considered valid.

* __Cache auto-update timespan in ms__:  
  Interval after which cache entries are automatically refreshed.

* __Cache Item In-use Timespan in ms__:  
  Time after which unused cache entries are evicted.

* __Batch Update Size__:  
  Batch size used when updating cache entries (max: 100).  
  **Note:** Batch fetches to the Canto API are slow (timeout: 50 seconds).

* __Tenant, OAuth URL, AppId, AppSecret__:  
  OAuth credentials used for authentication and automatic cache updates.  
  These values are configured **exclusively in the Service Configuration** and act as the single source of truth.

* __Restart Service on Config Save__:  
  Restarts the Canto service when closing the dialog with “OK”.  
  **Note:** Restarting clears the cache.

### Project Config

* __Canto User__:  
  Project-specific Canto user used for accessing assets.

* __Restart Service on Config Save__:  
  Restarts the Canto service when closing the dialog with “OK”.  
  **Note:** Restarting clears the cache.