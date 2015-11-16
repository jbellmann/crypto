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

import org.keyczar.Crypter;
import org.keyczar.KeyczarFileReader;
import org.keyczar.SessionCrypter;
import org.keyczar.exceptions.Base64DecodingException;
import org.keyczar.exceptions.KeyczarException;
import org.keyczar.interfaces.KeyczarReader;
import org.keyczar.util.Base64Coder;
import org.zalando.crypto.Decrypter;

/**
 * @author  jbellmann
 */
public class KeyczarDecrypter implements Decrypter {

    private final KeyczarConfig keyczarConfig;
    private Crypter crypter;
    private KeyczarReader keyczarReader;

    private final Object lock = new Object();

    public KeyczarDecrypter(final String privateKeyLocationDirectory) {
        this(privateKeyLocationDirectory, new KeyczarConfig(), false);
    }

    public KeyczarDecrypter(final String privateKeyLocationDirectory, final boolean lazy) {
        this(privateKeyLocationDirectory, new KeyczarConfig(), lazy);
    }

    public KeyczarDecrypter(final String privateKeyLocationDirectory, final KeyczarConfig keyczarConfig) {
        this(privateKeyLocationDirectory, keyczarConfig, false);
    }

    public KeyczarDecrypter(final String privateKeyLocationDirectory, final KeyczarConfig keyczarConfig,
            final boolean lazy) {
        this(new KeyczarFileReader(privateKeyLocationDirectory), keyczarConfig, lazy);
    }

    public KeyczarDecrypter(final KeyczarReader keyczarReader) {
        this(keyczarReader, new KeyczarConfig(), false);
    }

    public KeyczarDecrypter(final KeyczarReader keyczarReader, final KeyczarConfig keyczarConfig) {
        this(keyczarReader, keyczarConfig, false);
    }

    public KeyczarDecrypter(final KeyczarReader keyczarReader, final KeyczarConfig keyczarConfig, final boolean lazy) {
        if (lazy) {
            this.keyczarConfig = keyczarConfig;
            this.keyczarReader = keyczarReader;
        } else {
            try {
                this.crypter = new Crypter(keyczarReader);
                this.keyczarConfig = keyczarConfig;
            } catch (KeyczarException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public String decrypt(final String text) {
        final int separatorIndex = text.indexOf(getConfig().getSeparator());
        if (separatorIndex < 1) {
            throw new RuntimeException("Invalid message format, can not decrypt");
        }

        try {
            final byte[] encryptedMsg = Base64Coder.decodeWebSafe(text.substring(0, separatorIndex));
            final byte[] encryptedKey = Base64Coder.decodeWebSafe(text.substring(separatorIndex + 1));
            return new String(new SessionCrypter(getCrypter(), encryptedKey).decrypt(encryptedMsg),
                    getConfig().getCharsetName());
        } catch (Base64DecodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyczarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected KeyczarConfig getConfig() {
        return this.keyczarConfig;
    }

    protected Crypter getCrypter() {
        if (this.crypter == null) {
            synchronized (lock) {
                if (this.crypter == null) {
                    try {
                        this.crypter = new Crypter(this.keyczarReader);
                    } catch (KeyczarException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }

        return this.crypter;
    }

}
