# Dropwizard caffeine configurator [![Build Status](https://travis-ci.org/gabber12/dropwizard-caffeine-configurator.svg?branch=master)](https://travis-ci.org/gabber12/dropwizard-caffeine-configurator)
Dropwizard bundle for Caffeine cache bootstrapping

## Dependencies
* [caffeine](https://github.com/ben-manes/caffeine) 2.5.5

## Usage
The bundle simplifies caffeine spec mangement and also integrates with dropwizard metrics registry. 
### Build instructions
  - Clone the source:

        git clone github.com/gabber12/dropwizard-caffeine-configurator

  - Build

        mvn install

### Maven Dependency
Use the following repository:
```xml
<repository>
    <id>clojars</id>
    <name>Clojars repository</name>
    <url>https://clojars.org/repo</url>
</repository>
```
Use the following maven dependency:
```xml
<dependency>
    <groupId>io.dropwizard.cache</groupId>
    <artifactId>caffeine-configurator</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Using Configurator bundle

#### Bootstrap
```java
    private final CaffeineBundle caffeineBundle = new CaffeineBundle<CacheNames, AppConfiguration>() {
        @Override
        public CaffeineConfig getConfig(AppConfiguration configuration) {
            return configuration.getCacheConfig();
        }
    };
    
    ...

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(caffeineBundle);
    }

    public enum CacheNames {
        FIRST_CACHE
    }
```

#### Get a loading Cache
```java
    caffeineBundle.loadingCache(CacheNames.FIRST_CACHE, cacheLoader);
```

### Sample Configuration
```
cacheConfig:
  metricsPrefix: io.dropwizard.reporting
  defaultCacheConfig:
    metricsEnabled: true
    caffeineSpec: ""
  cache:
    FIRST_CACHE:
      metricsEnabled: true
      caffeineSpec: maximumSize=500, expireAfterAccess=30s
```

LICENSE
-------

Copyright 2018 Shubham Sharma <shubham.sha12@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

