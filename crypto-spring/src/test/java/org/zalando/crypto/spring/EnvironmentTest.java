/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.crypto.spring;

import java.io.IOException;
import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.crypto.ReverseCrypter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnvironmentTest {

    @Autowired
    private Environment environment;

    @Test
    public void testCrypto() {
        String value = environment.getProperty("one");
        Assertions.assertThat(value).isEqualTo("1");

        String cryptedValue = environment.getProperty("crypted");
        Assertions.assertThat(cryptedValue).isEqualTo("Guten_Morgen");
    }

    @Configuration
    static class TestConfig {

        @Bean
        public Environment environment() {

            Properties propsLoaded = new Properties();
            try {
                propsLoaded.load(getClass().getResourceAsStream("/withCrypto.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            MockEnvironment env = new MockEnvironment();
            PropertiesPropertySource source = new EncryptablePropertiesPropertySource("withCrypto",
                    new EncryptableProperties(propsLoaded, new ReverseCrypter(), "crypto"));

            env.getPropertySources().addFirst(source);

            return env;
        }
    }
}
