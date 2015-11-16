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
package org.zalando.crypto.spring.boot;

import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.zalando.crypto.Decrypter;
import org.zalando.crypto.spring.EncryptableProperties;

/**
 * @author jbellmann
 */
public abstract class EncryptedPropertySourceLoader implements PropertySourceLoader, PriorityOrdered {

	private Decrypter decrypter;

	public EncryptedPropertySourceLoader() {
		loadAndSetDecryptor();
		// String catalinaBase = System.getProperty("catalina.base");
		// if (StringUtils.hasText(catalinaBase)) {
		//
		// // TODO, build path to private key
		// LOG.info("Using : {}", KeyczarDecrypter.class.getSimpleName());
		// this.decrypter = new KeyczarDecrypter(null, true);
		// } else {
		// LOG.info("Using : {}", NoOpCrypter.class.getSimpleName());
		// this.decrypter = new NoOpCrypter(true);
		// }
	}
	
	protected abstract void loadAndSetDecryptor() ;

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE - 100;
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { "properties", "xml" };
	}

	@Override
	public PropertySource<?> load(final String name, final Resource resource, final String profile) throws IOException {
		validateDecrypter();
		if (profile == null) {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			if (!properties.isEmpty()) {
				return new PropertiesPropertySource(name,
						new EncryptableProperties(properties, decrypter, getPrefix()));
			}
		}

		return null;
	}
	
	protected void validateDecrypter(){
		Assert.notNull(decrypter, "'Decrypter' should not be null");
	}

	protected void setDecrypter(Decrypter decrypter) {
		Assert.notNull(decrypter, "'Decrypter' should not be null");
		this.decrypter = decrypter;
	}

	public String getPrefix() {
		return "crypto:";
	}
}
