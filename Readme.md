# Canto SaaS Integration

This module provides means to reference Canto media within FirstSpirit.

The JavaDoc documentation is a good place to start, when using this module as a Developer.

## Configuration

The Module requires Configuration on a server and Project level.
To enable the DAP for a project, the ProjectApp needs to be added and configured.

### Server Configuration

Install the module.
Open Service Configuration. Short Config Overview:

* __Use Request Limiter__: makes sure the server respects the API Limits imposed by Canto as configured.
* __Max Requests per Minute__: If you use this module across __different Servers__ for the same Canto Account, make sure to adjust _Max Requests per
  Minute_ accordingly, such
  that added up, all requests stay within your accounts' limit.
* __Requests without Delay__: Request Limiter delays Api Requests based on your Max Calls per Minute. The first X Calls are not delayed, the 60s
  Delayed is uniformly spread across the remaining requests. Hence, this value should be significantly lower than your max Requests.
* __UseCache__: Activate ServerSide Cache. Highly recommended, to alleviate Api Limits.
* __Cache Element Size__: Max Elements to Cache. This Limit is not strictly enforced and may be exceeded for a short duration (30 - 60 seconds), e.g.
  during Generations
* __Cache Item Lifespan in ms__: How long is an element valid in cache
* __Cache auto-update timespan in ms__: How long, until an element is auto updated in cache.
* __Cache Item In use Timespan in ms__: How long, after an element counts as "unused" and evicted from cache.
* __Batch Update Size__: BatchSize in which Cache elements are updated, max: 100. Careful, batch fetches to the Canto Api are slow! Timeout: 50seconds
* __Tenant, Oauth, AppId, AppSecret, User__: Credentials for auto Update fetches. Required for auto updating
* __Restart Service on Config Save__: Restart the CantoService when closing this Dialog with "OK", makes Service use the adjusted Config. Restart
  clears Cache!

## Project Config

* __Tenant, Oauth, AppId, AppSecret, User__: Credentials for fetching assets.
* __Restart Service on Config Save__: Restart the CantoService when closing this Dialog with "OK", makes Service use the adjusted Config. Restart
  clears Cache!
