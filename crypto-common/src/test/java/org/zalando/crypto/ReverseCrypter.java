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
package org.zalando.crypto;

/**
 * Simply reverses the text given. Use this for testing.
 *
 * @author  jbellmann
 */
public final class ReverseCrypter implements Encrypter, Decrypter {

    @Override
    public String decrypt(final String text) {
        return reverse(text);
    }

    @Override
    public String encrypt(final String text) {
        return reverse(text);
    }

    protected String reverse(final String text) {
        return new StringBuilder(text).reverse().toString();
    }

}
