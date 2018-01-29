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

package io.dropwizard.cache.caffeine.metrics;

import com.codahale.metrics.MetricRegistry;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import org.junit.Test;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

public class MetricsStatsCounterTest {
    private final StatsCounter metricsStatsCounter = new MetricsStatsCounter(new MetricRegistry(), "PREFIX");
    private final LoadingCache<String, String> cache = Caffeine.newBuilder()
            .recordStats(() -> metricsStatsCounter)
            .maximumSize(1)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@Nonnull String key) throws Exception {
                    return key;
                }
            });

    @Test
    public void testMetricsRegistryPublish() {
        CacheStats stats = metricsStatsCounter.snapshot();
        assertEquals(0, stats.loadCount());
        assertEquals(0, stats.missCount());
        assertEquals(0, stats.hitCount());
        cache.get("1");
        stats = metricsStatsCounter.snapshot();
        assertEquals(1, stats.loadCount());
        assertEquals(1, stats.missCount());
        assertEquals(0, stats.hitCount());


        cache.get("1");
        stats = metricsStatsCounter.snapshot();
        assertEquals(1, stats.loadCount());
        assertEquals(1, stats.missCount());
        assertEquals(1, stats.hitCount());

        cache.get("2");
        cache.get("3");
        stats = metricsStatsCounter.snapshot();
        assertEquals(3, stats.loadCount());
        assertEquals(3, stats.missCount());
        assertEquals(1, stats.hitCount());
    }
}