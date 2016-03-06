/*
 * Copyright 2005-2009 by Lars Torunski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.meiah.linkfilters;

/**
 * Link filter utilities for AND, OR, NOT and XOR.
 * 
 * @author Lars Torunski
 * @version $Revision: 1.9 $
 */
public final class LinkFilterUtil {

	/**
	 * Disallow creation of utility class.
	 */
	private LinkFilterUtil() {
	}

	// --- AND LinkFilter ---

	/**
	 * @param filter1
	 *            first filter
	 * @param filter2
	 *            second filter
	 * @return true if filter1 and filter2 accept the parameters of
	 *         {@link LinkFilter}
	 */
	public static LinkFilter and(LinkFilter filter1, LinkFilter filter2) {
		return new And(new LinkFilter[] { filter1, filter2 });
	}

	/**
	 * @param filters
	 *            an array of {@link LinkFilter}
	 * @return true if all filters accept the parameters of {@link LinkFilter}
	 */
	public static LinkFilter and(LinkFilter[] filters) {
		return new And(filters);
	}

	/**
	 * Internal and filter.
	 */
	private static class And implements LinkFilter {

		private LinkFilter[] filters;

		public And(LinkFilter[] filters) {
			this.filters = filters;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(String link) {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i].accept(link) == false) {
					return false;
				}
			}
			return true;
		}

	}

	// --- OR LinkFilter ---

	/**
	 * @param filter1
	 *            first filter
	 * @param filter2
	 *            second filter
	 * @return true if filter1 or filter2 accept the parameters of
	 *         {@link LinkFilter}
	 */
	public static LinkFilter or(LinkFilter filter1, LinkFilter filter2) {
		return new Or(new LinkFilter[] { filter1, filter2 });
	}

	/**
	 * @param filters
	 *            an array of {@link LinkFilter}
	 * @return true if one filter accepts the parameters of {@link LinkFilter}
	 */
	public static LinkFilter or(LinkFilter[] filters) {
		return new Or(filters);
	}

	/**
	 * Internal or filter.
	 */
	private static class Or implements LinkFilter {

		private LinkFilter[] filters;

		public Or(LinkFilter[] filters) {
			this.filters = filters;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(String link) {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i].accept(link)) {
					return true;
				}
			}
			return false;
		}

	}

	// --- NOT LinkFilter ---

	/**
	 * @param filter
	 *            a {@link LinkFilter}
	 * @return true if the filter doesn't accepts the parameters of
	 *         {@link LinkFilter} false if the filter accepts the parameters
	 */
	public static LinkFilter not(LinkFilter filter) {
		return new Not(filter);
	}

	/**
	 * Internal not filter.
	 */
	private static class Not implements LinkFilter {

		private LinkFilter filter;

		public Not(LinkFilter filter) {
			this.filter = filter;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(String link) {
			return !filter.accept(link);
		}

	}

	// --- XOR LinkFilter ---

	/**
	 * @param filter1
	 *            first filter
	 * @param filter2
	 *            second filter
	 * @return true if one filter accept the parameters of {@link LinkFilter}
	 *         and the other not
	 */
	public static LinkFilter xor(LinkFilter filter1, LinkFilter filter2) {
		return new Xor(filter1, filter2);
	}

	/**
	 * Internal xor filter.
	 */
	private static class Xor implements LinkFilter {

		private LinkFilter f1;
		private LinkFilter f2;

		public Xor(LinkFilter f1, LinkFilter f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(String link) {
			final boolean acceptF1 = f1.accept(link);
			final boolean acceptF2 = f2.accept(link);

			return ((acceptF1 && !acceptF2) || (!acceptF1 && acceptF2));
		}

	}

}
