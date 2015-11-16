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

import org.keyczar.Keyczar;

/**
 * @author  jbellmann
 */
public class KeyczarConfig {

    static final String DEFAULT_ENCODING = Keyczar.DEFAULT_ENCODING;

    /**
     * The encrypted text returned by this class has the format encryptedText + SEPARATOR + encryptedSymmetricKey. It
     * must not be a character used by Base64Coder. ~ was chosen because it is "web safe", i.e. it will not be encoded
     * when it occurs in an URL
     */
    static final String DEFAULT_SEPARATOR = "~";

    public String getCharsetName() {
        return DEFAULT_ENCODING;
    }

    public String getSeparator() {
        return DEFAULT_SEPARATOR;
    }

}
