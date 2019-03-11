package client.model;

/**
 * Created by Future on 2/6/18.
 */
public class Pair<K, V> {

	public K fi;
	public V se;

	public Pair(K key, V value) {
		this.fi = key;
		this.se = value;
	}

	public K getFirst() {
		return fi;
	}

	public V getSecond() {
		return se;
	}
}