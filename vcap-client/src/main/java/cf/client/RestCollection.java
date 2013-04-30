package cf.client;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author Mike Heath <elcapo@gmail.com>
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
