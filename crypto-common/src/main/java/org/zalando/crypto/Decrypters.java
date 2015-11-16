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

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 
 * @author jbellmann
 *
 */
public final class Decrypters {

	public static List<Decrypter> prefixedDecrypters(List<Decrypter> toFilter) {
		return Lists.newArrayList(Iterables.filter(toFilter, new PrefixedDecrypterPredicate()));
	}

	public static List<Decrypter> nonPrefixedDecrypters(List<Decrypter> toFilter) {
		return Lists.newArrayList(Iterables.filter(toFilter, new PrefixedDecrypterPredicate()));
	}

	public static List<Decrypter> findByPrefix(List<Decrypter> decrypters, String prefix) {
		Preconditions.checkNotNull(decrypters, "'Decrypter's-list should not be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(prefix), "'prefix' should never be null or empty.");
		return Lists.newArrayList(Iterables.filter(decrypters,
				Predicates.and(new PrefixedDecrypterPredicate(), new PrefixPredicate(prefix))));
	}

	static class PrefixPredicate implements Predicate<Decrypter> {

		private final String prefix;

		public PrefixPredicate(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public boolean apply(Decrypter input) {
			return prefix.equals(((Prefixed) input).getPrefix());
		}

	}

	static class PrefixedDecrypterPredicate implements Predicate<Decrypter> {

		public boolean apply(Decrypter input) {
			return (input instanceof Prefixed);
		}

	}

	static class NonPrefixedDecrypterPredicate implements Predicate<Decrypter> {

		public boolean apply(Decrypter input) {
			return !(input instanceof Prefixed);
		}

	}

}
