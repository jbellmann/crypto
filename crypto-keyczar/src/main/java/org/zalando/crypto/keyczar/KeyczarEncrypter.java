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
package org.zalando.crypto.keyczar;

import java.io.UnsupportedEncodingException;

import org.keyczar.KeyczarFileReader;
import org.keyczar.SessionCrypter;
import org.keyczar.exceptions.KeyczarException;
import org.keyczar.interfaces.KeyczarReader;
import org.keyczar.util.Base64Coder;
import org.zalando.crypto.Encrypter;

/**
 * @author  jbellmann
 */
public class KeyczarEncrypter implements Encrypter {

    private org.keyczar.Encrypter encrypter;
    private final KeyczarConfig keyczarConfig;
    private KeyczarReader keyczarReader;

    private final Object lock = new Object();

    public KeyczarEncrypter(final String publicKeyFileDirectory) {
        this(publicKeyFileDirectory, new KeyczarConfig(), false);
    }

    public KeyczarEncrypter(final String publicKeyFileDirectory, final boolean lazy) {
        this(publicKeyFileDirectory, new KeyczarConfig(), lazy);
    }

    public KeyczarEncrypter(final String publicKeyFileDirectory, final KeyczarConfig keyczarConfig) {
        this(publicKeyFileDirectory, keyczarConfig, false);
    }

    public KeyczarEncrypter(final String publicKeyFileDirectory, final KeyczarConfig keyczarConfig,
            final boolean lazy) {
        this(new KeyczarFileReader(publicKeyFileDirectory), keyczarConfig, lazy);
    }

    public KeyczarEncrypter(final KeyczarReader keyczarReader) {
        this(keyczarReader, new KeyczarConfig(), false);
    }

    public KeyczarEncrypter(final KeyczarReader keyczarReader, final KeyczarConfig keyczarConfig) {
        this(keyczarReader, keyczarConfig, false);
    }

    public KeyczarEncrypter(final KeyczarReader keyczarReader, final KeyczarConfig keyczarConfig, final boolean lazy) {
        if (lazy) {
            this.keyczarConfig = keyczarConfig;
            this.keyczarReader = keyczarReader;
        } else {
            try {
                this.encrypter = new org.keyczar.Encrypter(keyczarReader);
                this.keyczarConfig = keyczarConfig;
            } catch (KeyczarException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public String encrypt(final String text) {

        String msg;
        String key;
        try {
            final SessionCrypter sessionCrypter = new SessionCrypter(getEncrypter());
            msg = Base64Coder.encodeWebSafe(sessionCrypter.encrypt(text.getBytes(getConfig().getCharsetName())));
            key = Base64Coder.encodeWebSafe(sessionCrypter.getSessionMaterial());

            StringBuilder sb = new StringBuilder();
            sb.append(msg).append(getConfig().getSeparator()).append(key);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyczarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected KeyczarConfig getConfig() {
        return this.keyczarConfig;
    }

    protected org.keyczar.Encrypter getEncrypter() {
        if (this.encrypter == null) {
            synchronized (lock) {
                if (this.encrypter == null) {
                    try {
                        this.encrypter = new org.keyczar.Encrypter(this.keyczarReader);
                    } catch (KeyczarException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }

        return this.encrypter;
    }

}
