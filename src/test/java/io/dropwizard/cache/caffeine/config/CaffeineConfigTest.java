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

package io.dropwizard.cache.caffeine.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.dropwizard.cache.caffeine.SampleConfiguration;
import io.dropwizard.cache.caffeine.SampleName;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class CaffeineConfigTest {
    @Test
    public void testSerialization() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_config_sample.yml");
        SampleConfiguration configuration = new YAMLMapper().getFactory().createParser(inputStream).readValueAs(SampleConfiguration.class);
        CaffeineConfig<SampleName> caffeineConfig = configuration.getCacheConfig();

        assertEquals("io.dropwizard.reporting", caffeineConfig.getMetricsPrefix());
        assertEquals(2, caffeineConfig.getCache().size());
        CacheConfig defaultConfig = caffeineConfig.getDefaultCacheConfig();
        assertNotNull(defaultConfig);
        assertEquals(true, defaultConfig.isMetricsEnabled());
        assertEquals("", defaultConfig.getCaffeineSpec());

        CacheConfig config = caffeineConfig.getCache().get(SampleName.CACHE_FIRST);
        assertEquals("maximumSize=500, expireAfterAccess=30s", config.getCaffeineSpec());
        assertTrue(config.isMetricsEnabled());

        config = caffeineConfig.getCache().get(SampleName.CACHE_SECOND);
        assertEquals("maximumSize=500", config.getCaffeineSpec());
        assertFalse(config.isMetricsEnabled());


    }

}