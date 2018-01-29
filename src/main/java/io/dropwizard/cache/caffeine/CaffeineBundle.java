/*
 * Copyright 2018 Shubham Sharma <shubham.sha12@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.dropwizard.cache.caffeine;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Strings;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.cache.caffeine.config.CacheConfig;
import io.dropwizard.cache.caffeine.config.CaffeineConfig;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class CaffeineBundle<K extends Enum<?>, T extends Configuration> implements ConfiguredBundle<T> {

    private final ConcurrentHashMap<K, Caffeine> caffeineRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, CacheLoader> loadersRegistry = new ConcurrentHashMap<>();
    private CaffeineFactory<K> caffeineFactory;
    private CacheConfig defaultCacheConfig;


    @Override
    public void run(T configuration, Environment environment) throws Exception {
        CaffeineConfig<K> config = getConfig(configuration);
        defaultCacheConfig = config.getDefaultCacheConfig();
        caffeineFactory = new CaffeineFactory<>(environment.metrics(), config.getMetricsPrefix());
        initializeCaffeineRegistry(config.getCache());
    }

    private void initializeCaffeineRegistry(Map<K, CacheConfig> cacheConfigList) {
        if (null == cacheConfigList) {
            return;
        }
        cacheConfigList.forEach((key, value) -> {
            if (value == null || Strings.isNullOrEmpty(value.getCaffeineSpec())) {
                return;
            }
            log.info("Initializing Registry Key {}, Spec {}", key, value.getCaffeineSpec());
            Caffeine caffeine = caffeineFactory.fromConfig(value, key);
            caffeineRegistry.put(key, caffeine);
        });

    }

    public void addLoaders(Map<K, CacheLoader> loaders) {
        loadersRegistry.putAll(loaders);
    }

    public Caffeine getCaffeineInstance(K cacheName) {
        return caffeineRegistry.computeIfAbsent(cacheName, (cacheNameInt) -> caffeineFactory.fromConfig(defaultCacheConfig, cacheNameInt));
    }

    public <C, V> LoadingCache<C, V> loadingCache(K cacheName, CacheLoader<C, V> loader) {
        Caffeine<C, V> caffeine = getCaffeineInstance(cacheName);
        return caffeine.build(loader);
    }

    public <C, V> LoadingCache<C, V> loadingCache(K cacheName) {
        if (!loadersRegistry.containsKey(cacheName)) {
            log.debug("Unbounded loader {}", cacheName);
            throw new RuntimeException("No Loaders provided for cacheName : " + cacheName.name());
        }
        CacheLoader<C, V> loader = loadersRegistry.get(cacheName);
        return loadingCache(cacheName, loader);
    }

    public abstract CaffeineConfig<K> getConfig(T configuration);

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
}
