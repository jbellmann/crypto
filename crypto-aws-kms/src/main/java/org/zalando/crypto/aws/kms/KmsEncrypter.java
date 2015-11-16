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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.nio.ByteBuffer;

import org.zalando.crypto.Encrypter;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.util.Base64;

public class KmsEncrypter extends KmsBase implements Encrypter {

	private final AWSKMS kms;
	private final String kmsKeyId;

	public KmsEncrypter(AWSKMS kms, String kmsKeyId) {
		checkNotNull(kms, "'kms' should never be null");
		checkArgument(isNullOrEmpty(kmsKeyId), "'kmsKeyId' must not be blank");
		this.kms = kms;
		this.kmsKeyId = kmsKeyId;
	}

	@Override
	public String encrypt(String text) {
		if (isNullOrEmpty(text)) {

			return EMPTY_STRING;
		} else {
			final EncryptRequest encryptRequest = new EncryptRequest().withKeyId(kmsKeyId) //
					.withPlaintext(ByteBuffer.wrap(text.getBytes()));

			final ByteBuffer encryptedBytes = kms.encrypt(encryptRequest).getCiphertextBlob();

			return extractString(ByteBuffer.wrap(Base64.encode(encryptedBytes.array())));
		}
	}

}
