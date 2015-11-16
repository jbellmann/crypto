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

import java.util.Properties;

import org.springframework.util.Assert;
import org.zalando.crypto.Decrypter;

/**
 * Inspired by JASYPT:
 * http://svn.code.sf.net/p/jasypt/code/trunk/jasypt/src/main/java/org/jasypt/properties/EncryptableProperties.java
 *
 * @author  jbellmann
 */
public class EncryptableProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_CRYPTO_PREFIX = "crypto:";

    private Decrypter decrypter;

    private String cryptoPrefix;

    public EncryptableProperties(final Properties defaults, final Decrypter decrypter) {
        this(defaults, decrypter, DEFAULT_CRYPTO_PREFIX);
    }

    public EncryptableProperties(final Properties defaults, final Decrypter decrypter, final String cryptoPrefix) {
        super(defaults);
        Assert.notNull(decrypter, "'decrypter' should never null.");
        Assert.hasText(cryptoPrefix, "'cryptoPrefix' should never be null or empty.");
        this.decrypter = decrypter;
        this.cryptoPrefix = cryptoPrefix;
    }

    @Override
    public String getProperty(final String key) {
        return decode(super.getProperty(key));
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        return decode(super.getProperty(key, defaultValue));
    }

    private synchronized String decode(final String property) {
        if (property == null) {
            return property;
        }

        if (!property.startsWith(cryptoPrefix)) {
            return property;
        } else {
            final String unPrefixed = property.substring(this.cryptoPrefix.length() + 1);
            return this.decrypter.decrypt(unPrefixed);
        }
    }

    @Override
    public synchronized Object get(final Object key) {

        Object oval = super.get(key);
        String sval = (oval instanceof String) ? (String) oval : null;
        sval = ((sval == null) && (defaults != null)) ? (String) defaults.getProperty(key.toString()) : sval;
        return decode(sval);
    }

}
