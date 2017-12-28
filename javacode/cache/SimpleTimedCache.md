## SimpleTimedCache 
   Bad Way~

```
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleTimedCache<K, V> {
	private long timeout = 3600000;	
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Map<K, CacheEntry> entryMap;	
	private TimeIndex timeIndex = new TimeIndex();
	public SimpleTimedCache() {
		this(10000, 3600000);
	}
	public SimpleTimedCache(int initialSize, long timeout) {
		this.timeout = timeout;
		entryMap = new HashMap<K, CacheEntry>(initialSize);
	}
	public void put(K key, V value) {
		clearExpiredEntry();
		long now = System.currentTimeMillis();
		lock.writeLock().lock();
		try {
			CacheEntry e = entryMap.get(key);
			//构建有序链表
			if (e == null) {
				e = new CacheEntry(key, value, now);
				entryMap.put(key, e);
				timeIndex.addLast(e);
			} else {
				timeIndex.remove(e);
				e.cacheTime = now;
				e.value = value;
				timeIndex.addLast(e);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}


	public int size() {
		return entryMap.size();
	}

	public List<V> getAll() {
		clearExpiredEntry();
		List<V> list = new ArrayList<V>();
		Iterator<Entry<K, SimpleTimedCache<K, V>.CacheEntry>> iter = entryMap.entrySet().iterator();
		lock.readLock().lock();
		try {
			while (iter.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> entry = (Map.Entry<K, V>) iter.next();
				K key = entry.getKey();
				CacheEntry e = entryMap.get(key);
				list.add(e.value);
			}
		} finally {
			lock.readLock().unlock();
		}

		return list;

	}

	public V get(K key) {
		clearExpiredEntry();

		lock.readLock().lock();
		try {
			CacheEntry e = entryMap.get(key);
			return e == null ? null : e.value;
		} finally {
			lock.readLock().unlock();
		}
	}

	public void invalidateByKey(K key) {
		lock.writeLock().lock();
		try {
			CacheEntry e = entryMap.remove(key);
			if (e != null) {
				timeIndex.remove(e);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public V remove(K key) {
		V result = null;
		lock.writeLock().lock();
		try {
			CacheEntry e = entryMap.remove(key);
			if (e != null) {
				result = e.value;
				timeIndex.remove(e);
			}
		} finally {
			lock.writeLock().unlock();
		}
		return result;
	}

	public void clear() {
		lock.writeLock().lock();
		try {
			entryMap.clear();
			timeIndex.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void clearExpiredEntry() {
		long expireTime = System.currentTimeMillis() - timeout;
		CacheEntry head = timeIndex.head;
		if (head != null && head.cacheTime < expireTime) {
			lock.writeLock().lock();
			try {
				for (Iterator<CacheEntry> iter = timeIndex.iterator(); iter.hasNext();) {
					CacheEntry e = iter.next();
					if (e.cacheTime < expireTime) {
						iter.remove();
						entryMap.remove(e.key);
					} else {
						break;
					}
				}
			} finally {
				lock.writeLock().unlock();
			}
		}
	}

	class TimeIndex implements Iterable<CacheEntry> {
		CacheEntry head;
		CacheEntry tail;

		void remove(CacheEntry e) {
			// 如果是头或尾，移动头、尾指针
			if (e == head) {
				head = e.next;
			}
			if (e == tail) {
				tail = e.prev;
			}

			// 把e从链表中删除
			if (e.prev != null)
				e.prev.next = e.next;
			if (e.next != null)
				e.next.prev = e.prev;
			e.next = null;
			e.prev = null;
		}

		void addLast(CacheEntry e) {
			if (head == null) {
				head = e;
			}

			if (tail != null) {
				tail.next = e;
				e.prev = tail;
			}
			tail = e;
		}

		void clear() {
			CacheEntry e = head;
			while (e != null) {
				CacheEntry temp = e.next;
				e.prev = null;
				e.next = null;
				e = temp;
			}
			head = null;
			tail = null;
		}

		public Iterator<CacheEntry> iterator() {
			return new Iterator<CacheEntry>() {
				CacheEntry currentEntry = null;
				CacheEntry nextEntry = head;

				public boolean hasNext() {
					return nextEntry != null;
				}

				public CacheEntry next() {
					if (nextEntry != null) {
						currentEntry = nextEntry;
						nextEntry = nextEntry.next;
						return currentEntry;
					} else {
						throw new IllegalStateException();
					}
				}

				public void remove() {
					if (currentEntry != null) {
						TimeIndex.this.remove(currentEntry);
						currentEntry = null;
					}
				}
			};
		}
	}

	class CacheEntry {
		final K key;
		V value;
		long cacheTime;
		CacheEntry prev;
		CacheEntry next;

		public CacheEntry(K key, V value, long cacheTime) {
			this.key = key;
			this.value = value;
			this.cacheTime = cacheTime;
		}

		@Override
		public boolean equals(Object obj) {
			CacheEntry other = (CacheEntry) obj;
			return key.equals(other.key);
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}
}
```