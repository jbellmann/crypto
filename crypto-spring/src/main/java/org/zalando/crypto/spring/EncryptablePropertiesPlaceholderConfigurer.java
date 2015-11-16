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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.StringValueResolver;
import org.zalando.crypto.Decrypter;

/**
 * @author  jbellmann
 */
public class EncryptablePropertiesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    private static final String CRYTO_PREFIX = "crypto:";

    private Decrypter decrypter;
    private String cryptoPrefix;

    public EncryptablePropertiesPlaceholderConfigurer() {
        super();
    }

    public EncryptablePropertiesPlaceholderConfigurer(final Decrypter decrypter) {
        this(decrypter, CRYTO_PREFIX);
    }

    public EncryptablePropertiesPlaceholderConfigurer(final Decrypter decrypter, final String cryptoPrefix) {
        super();
        this.decrypter = decrypter;
        this.cryptoPrefix = cryptoPrefix;
    }

    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess,
            final ConfigurablePropertyResolver propertyResolver) throws BeansException {

        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);

        StringValueResolver valueResolver = new StringValueResolver() {
            @Override
            public String resolveStringValue(final String strVal) {
                String resolved = ignoreUnresolvablePlaceholders ? propertyResolver.resolvePlaceholders(strVal)
                                                                 : propertyResolver.resolveRequiredPlaceholders(strVal);

                if (resolved.startsWith(cryptoPrefix)) {

                    resolved = decrypter.decrypt(resolved.substring(cryptoPrefix.length()));
                }

                return (resolved.equals(nullValue) ? null : resolved);
            }
        };

        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    public void setDecrypter(final Decrypter decrypter) {
        this.decrypter = decrypter;
    }

    public void setCryptoPrefix(final String cryptoPrefix) {
        this.cryptoPrefix = cryptoPrefix;
    }
}
