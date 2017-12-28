## LRUCache Base on LinkedHashMap
```
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {

	int cacheSize = 0;
	float loadFactor = 0.75f;
	LinkedHashMap<K, V> map;

	public LRUCache(int cacheSize) {
		this.cacheSize = cacheSize;
		map = new LinkedHashMap<K, V>(cacheSize, loadFactor, true) {
			private static final long serialVersionUID = -1496596499685247267L;

			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LRUCache.this.cacheSize;
			}
		};
	}

	public synchronized void clear() {
		map.clear();
	}

	public synchronized V get(K k) {
		return map.get(k);
	}

	public synchronized void put(K k, V v) {
		map.put(k, v);
	}

	public synchronized V remove(K k) {
		return map.remove(k);
	}

	public synchronized int size() {
		return map.size();
	}

	public synchronized Collection<?> values() {
		return map.values();
	}

	public synchronized void addAll(Map<K, V> m) {
		map.putAll(m);
	}

}
