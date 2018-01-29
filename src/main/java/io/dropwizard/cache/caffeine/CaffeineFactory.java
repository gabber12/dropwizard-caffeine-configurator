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

import com.codahale.metrics.MetricRegistry;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import io.dropwizard.cache.caffeine.config.CacheConfig;
import io.dropwizard.cache.caffeine.metrics.MetricsStatsCounter;

class CaffeineFactory<K extends Enum<?>> {
    private final MetricRegistry metrics;
    private final String prefix;

    public CaffeineFactory(MetricRegistry metrics, String prefix) {
        this.metrics = metrics;
        this.prefix = prefix;
    }

    public Caffeine fromConfig(CacheConfig config, K key) {
        Caffeine caffeine = Caffeine.from(CaffeineSpec.parse(config.getCaffeineSpec()));
        if (config.isMetricsEnabled()) {
            StatsCounter metricsStatsCounter = new MetricsStatsCounter(metrics, getCachePrefix(prefix, key));
            caffeine.recordStats(() -> metricsStatsCounter);
        }
        return caffeine;
    }

    private String getCachePrefix(String metricsPrefix, K cacheName) {
        return String.format("%s_%s_", metricsPrefix, cacheName.name());
    }
}
