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

import org.keyczar.KeyczarFileReader;
import org.keyczar.interfaces.KeyczarReader;
import org.zalando.crypto.Decrypter;
import org.zalando.crypto.Encrypter;

import com.google.common.base.Preconditions;

/**
 * Can encrypt and decrypt messages.
 *
 * @author  jbellmann
 */
public class DefaultKeyczarCrypter implements Encrypter, Decrypter {

    private final KeyczarEncrypter keyczarEncrypter;
    private final KeyczarDecrypter keyczarDecrypter;

    public DefaultKeyczarCrypter(final String privateKeyFileDirectory, final String publicKeyFileLocation) {
        this(privateKeyFileDirectory, publicKeyFileLocation, new KeyczarConfig(), false);
    }

    public DefaultKeyczarCrypter(final String privateKeyFileDirectory, final String publicKeyFileDirectory,
            final KeyczarConfig keyczarConfig) {
        this(privateKeyFileDirectory, publicKeyFileDirectory, keyczarConfig, false);
    }

    public DefaultKeyczarCrypter(final String privateKeyFileDirectory, final String publicKeyFileDirectory,
            final KeyczarConfig keyczarConfig, final boolean lazy) {
        this(new KeyczarFileReader(privateKeyFileDirectory), new KeyczarFileReader(publicKeyFileDirectory),
            keyczarConfig, lazy);
    }

    public DefaultKeyczarCrypter(final KeyczarReader decryptKeyczarReader, final KeyczarReader encryptKeyczarReader) {
        this(decryptKeyczarReader, encryptKeyczarReader, new KeyczarConfig(), false);
    }

    public DefaultKeyczarCrypter(final KeyczarReader decryptKeyczarReader, final KeyczarReader encryptKeyczarReader,
            final KeyczarConfig keyczarConfig, final boolean lazy) {
    	Preconditions.checkNotNull(decryptKeyczarReader, "'decryptKeyczarReader' should not be null.");
    	Preconditions.checkNotNull(encryptKeyczarReader, "'encryptKeyczarReader' should not be null.");
    	Preconditions.checkNotNull(keyczarConfig, "'keyczarConfig' should not be null.");
        this.keyczarDecrypter = new KeyczarDecrypter(decryptKeyczarReader, keyczarConfig);
        this.keyczarEncrypter = new KeyczarEncrypter(encryptKeyczarReader, keyczarConfig);
    }

    @Override
    public String decrypt(final String text) {
        return this.keyczarDecrypter.decrypt(text);
    }

    @Override
    public String encrypt(final String text) {
        return this.keyczarEncrypter.encrypt(text);
    }
}
