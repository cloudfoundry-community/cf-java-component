/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.client;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author Mike Heath
 */
public class RestCollection<T> extends AbstractCollection<Resource<T>> {

	private final int totalResults;
	private Iterator<Resource<T>> iterator;

	public RestCollection(int totalResults, Iterator<Resource<T>> iterator) {
		this.totalResults = totalResults;
		this.iterator = iterator;
	}

	@Override
	public Iterator<Resource<T>> iterator() {
		return iterator;
	}

	@Override
	public int size() {
		return totalResults;
	}

}
