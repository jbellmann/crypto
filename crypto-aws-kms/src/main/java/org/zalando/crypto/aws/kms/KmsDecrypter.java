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
package org.zalando.crypto.aws.kms;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.nio.ByteBuffer;

import org.zalando.crypto.Decrypter;
import org.zalando.crypto.Prefixed;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.util.Base64;

/**
 * 
 * @author jbellmann
 *
 */
public class KmsDecrypter extends KmsBase implements Decrypter, Prefixed {
	
	private static final String PREFIX = "aws:kms:";

	private final AWSKMS kms;

	public KmsDecrypter(AWSKMS kms) {
		this.kms = kms;
	}

	@Override
	public String decrypt(String encryptedText) {
		if (isNullOrEmpty(encryptedText)) {
			return EMPTY_STRING;
		} else {

			// Assuming the encryptedText is encoded in Base64
			final ByteBuffer encryptedBytes = ByteBuffer.wrap(Base64.decode(encryptedText.getBytes()));

			final DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(encryptedBytes);

			return extractString(decryptByKms(decryptRequest).getPlaintext());
		}
	}

	protected DecryptResult decryptByKms(DecryptRequest decryptRequest) {
		return kms.decrypt(decryptRequest);
	}
    
	public String getPrefix() {
		return PREFIX;
	}
}
