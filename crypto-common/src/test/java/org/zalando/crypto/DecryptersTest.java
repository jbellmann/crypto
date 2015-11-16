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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DecryptersTest {
	
	
	@Test
	public void filterDecryptors(){
		List<Decrypter> unfiltered = new ArrayList<Decrypter>();
		unfiltered.add(new DefaultTestDecrypter());
		unfiltered.add(new PrefixedTestDecrypter());
		unfiltered.add(new PrefixedTestDecrypter());
		unfiltered.add(new DefaultTestDecrypter());
		unfiltered.add(new DefaultTestDecrypter());
		unfiltered.add(new DefaultTestDecrypter());
		List<Decrypter> filtered = Decrypters.prefixedDecrypters(unfiltered);
		Assertions.assertThat(filtered).isNotNull();
		Assertions.assertThat(filtered.size()).isEqualTo(2);
		Assertions.assertThat(filtered).hasOnlyElementsOfType(Prefixed.class);
	}
	
	static class DefaultTestDecrypter implements Decrypter {

		public String decrypt(String encryptedString) {
			return encryptedString;
		}
		
	}
	
	static class PrefixedTestDecrypter implements Decrypter, Prefixed {

		public String getPrefix() {
			return "prefix";
		}

		public String decrypt(String encryptedString) {
			return encryptedString;
		}
		
	}

}
