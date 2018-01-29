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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public final class MetricsStatsCounter implements StatsCounter {
    private final Meter hitCount;
    private final Meter missCount;
    private final Meter loadSuccessCount;
    private final Meter loadFailureCount;
    private final Timer totalLoadTime;
    private final Meter evictionCount;
    private final Meter evictionWeight;

    public MetricsStatsCounter(MetricRegistry registry, String metricsPrefix) {
        requireNonNull(metricsPrefix);
        hitCount = registry.meter(metricsPrefix + ".hitsCount");
        missCount = registry.meter(metricsPrefix + ".missesCount");
        totalLoadTime = registry.timer(metricsPrefix + ".loadsTime");
        loadSuccessCount = registry.meter(metricsPrefix + ".loadsSuccessCount");
        loadFailureCount = registry.meter(metricsPrefix + ".loadsFailureCount");
        evictionCount = registry.meter(metricsPrefix + ".evictionCount");
        evictionWeight = registry.meter(metricsPrefix + ".evictionWeight");
    }

    @Override
    public void recordHits(int count) {
        hitCount.mark(count);
    }

    @Override
    public void recordMisses(int count) {
        missCount.mark(count);
    }

    @Override
    public void recordLoadSuccess(long loadTime) {
        loadSuccessCount.mark();
        totalLoadTime.update(loadTime, TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordLoadFailure(long loadTime) {
        loadFailureCount.mark();
        totalLoadTime.update(loadTime, TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordEviction() {
        recordEviction(1);
    }

    @Override
    public void recordEviction(int weight) {
        evictionCount.mark();
        evictionWeight.mark(weight);
    }

    @Override
    public CacheStats snapshot() {
        return new CacheStats(
                hitCount.getCount(),
                missCount.getCount(),
                loadSuccessCount.getCount(),
                loadFailureCount.getCount(),
                totalLoadTime.getCount(),
                evictionCount.getCount(),
                evictionWeight.getCount());
    }

    @Override
    public String toString() {
        return snapshot().toString();
    }
}
