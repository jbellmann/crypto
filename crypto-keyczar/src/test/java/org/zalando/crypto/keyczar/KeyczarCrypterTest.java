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

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.zalando.crypto.Decrypter;
import org.zalando.crypto.Encrypter;
import org.zalando.crypto.test.Fixture;

public class KeyczarCrypterTest {

    private Encrypter encrypter;
    private Decrypter decrypter;

    @Before
    public void setUp() {
        File publicMetaLocation = new File("public");
        File privateMetaLocation = new File("private");

        this.encrypter = new KeyczarEncrypter(publicMetaLocation.getAbsolutePath());
        this.decrypter = new KeyczarDecrypter(privateMetaLocation.getAbsolutePath());

    }

    @Test
    public void encryptMessage() {
        String encryptedMessage = this.encrypter.encrypt(Fixture.MESSAGE);
        System.out.println(encryptedMessage);
    }

    @Test
    public void decryptMessage() {
        String decryptedMessage1 = this.decrypter.decrypt(Fixture.ENCRYPTED_MESSAGE);
        System.out.println(decryptedMessage1);

        String decryptedMessage2 = this.decrypter.decrypt(Fixture.ENCRYPTED_MESSAGE_2);
        System.out.println(decryptedMessage2);

        Assertions.assertThat(decryptedMessage1).isEqualTo(decryptedMessage2);
    }

    @Test(expected = RuntimeException.class)
    public void testEagerInitializationOfEncrypterWithNonExistingFolder() {
        File publicMetaLocation = new File("notExistentPublic");
        new KeyczarEncrypter(publicMetaLocation.getAbsolutePath(), false);
    }

    @Test
    public void testLazyInitializationOfEncrypterWithNonExistingFolder() {
        File publicMetaLocation = new File("notExistentPublic");
        KeyczarEncrypter encrypter = new KeyczarEncrypter(publicMetaLocation.getAbsolutePath(), true);

        try {
            encrypter.encrypt("text");
            Assertions.fail("Expected an Exception because of non-exsiting directory");
        } catch (Throwable t) {
            // expected behavior
        }
    }

    @Test(expected = RuntimeException.class)
    public void testEagerInitializationOfDecrypterWithNonExistingFolder() {
        File privateMetaLocation = new File("notExistentPrivate");
        new KeyczarDecrypter(privateMetaLocation.getAbsolutePath(), false);
    }

    @Test
    public void testLazyInitializationOfDecrypterWithNonExistingFolder() {
        File privateMetaLocation = new File("notExistentPrivate");
        KeyczarDecrypter decrypter = new KeyczarDecrypter(privateMetaLocation.getAbsolutePath(), true);

        try {
            decrypter.decrypt("text");
            Assertions.fail("Expected an Exception because of non-exsiting directory");
        } catch (Throwable t) {
            // expected behavior
        }
    }

}
