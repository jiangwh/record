<<<<<<< HEAD
##TimeCacheMap
```
/**
*	数据结构主要是用LinkedList & Map的结构， 来存储缓存对象。
*	利用定是任务（本代码中使用Thread.sleep也可以使用timer的定时任务）来定时删除链表尾部的Map增加链表首部的Map
*	相比之前使用 单纯使用Map || Queue 通过便利的方式删除超时对象，大大提高了代码的效率
*	The TimeCacheMap
*/
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class TimeCacheMap<K, V> {
	// this default ensures things expire at most 50% past the expiration time
	//初始化对象Map
	private static final int DEFAULT_NUM_BUCKETS = 3; 

	//超时对象,回调接口，以便处理删除事件
	public static interface ExpiredCallback<K, V> {
		public void expire(K key, V val);
	}

	private LinkedList<HashMap<K, V>> _buckets;

	private final Object _lock = new Object();
	private Thread _cleaner;
	private ExpiredCallback _callback;

	public TimeCacheMap(int expirationSecs, int numBuckets, ExpiredCallback<K, V> callback) {
		if (numBuckets < 2) {
			throw new IllegalArgumentException("numBuckets must be >= 2");
		}
		_buckets = new LinkedList<HashMap<K, V>>();
		for (int i = 0; i < numBuckets; i++) {
			_buckets.add(new HashMap<K, V>());
		}
		_callback = callback;
		final long expirationMillis = expirationSecs * 1000L;
		final long sleepTime = expirationMillis / (numBuckets - 1);
		_cleaner = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Map<K, V> dead = null;
						Thread.sleep(sleepTime);
						synchronized (_lock) {
							dead = _buckets.removeLast();
							_buckets.addFirst(new HashMap<K, V>());
						}
						if (_callback != null) {
							for (Entry<K, V> entry : dead.entrySet()) {
								_callback.expire(entry.getKey(), entry.getValue());
							}
						}
					}
				} catch (InterruptedException ex) {
					
				}
			}
		});
		_cleaner.setDaemon(true);
		_cleaner.start();
	}

	public TimeCacheMap(int expirationSecs, ExpiredCallback<K, V> callback) {
		this(expirationSecs, DEFAULT_NUM_BUCKETS, callback);
	}

	public TimeCacheMap(int expirationSecs) {
		this(expirationSecs, DEFAULT_NUM_BUCKETS);
	}

	public TimeCacheMap(int expirationSecs, int numBuckets) {
		this(expirationSecs, numBuckets, null);
	}

	public boolean containsKey(K key) {
		synchronized (_lock) {
			for (HashMap<K, V> bucket : _buckets) {
				if (bucket.containsKey(key)) {
					return true;
				}
			}
			return false;
		}
	}

	public V get(K key) {
		synchronized (_lock) {
			for (HashMap<K, V> bucket : _buckets) {
				if (bucket.containsKey(key)) {
					return bucket.get(key);
				}
			}
			return null;
		}
	}

	public void put(K key, V value) {
		synchronized (_lock) {
			Iterator<HashMap<K, V>> it = _buckets.iterator();
			HashMap<K, V> bucket = it.next();
			bucket.put(key, value);
			while (it.hasNext()) {
				bucket = it.next();
				bucket.remove(key);
			}
		}
	}

	public Object remove(K key) {
		synchronized (_lock) {
			for (HashMap<K, V> bucket : _buckets) {
				if (bucket.containsKey(key)) {
					return bucket.remove(key);
				}
			}
			return null;
		}
	}

	public int size() {
		synchronized (_lock) {
			int size = 0;
			for (HashMap<K, V> bucket : _buckets) {
				size += bucket.size();
			}
			return size;
		}
	}

	public void cleanup() {
		_cleaner.interrupt();
	}
}
```
=======
#TimeCacheMap
public class TimeCacheMap<K, V> {
  //this default ensures things expire at most 50% past the expiration time
  private static final int DEFAULT_NUM_BUCKETS = 3;

  public static interface ExpiredCallback<K, V> {
    public void expire(K key, V val);
  }

  private LinkedList<HashMap<K, V>> _buckets;

  private final Object _lock = new Object();
  private Thread _cleaner;
  private ExpiredCallback _callback;

  public TimeCacheMap(int expirationSecs, int numBuckets, ExpiredCallback<K, V> callback) {
    if(numBuckets<2) {
      throw new IllegalArgumentException("numBuckets must be >= 2");
    }
    _buckets = new LinkedList<HashMap<K, V>>();
    for(int i=0; i<numBuckets; i++) {
      _buckets.add(new HashMap<K, V>());
    }


    _callback = callback;
    final long expirationMillis = expirationSecs * 1000L;
    final long sleepTime = expirationMillis / (numBuckets-1);
    _cleaner = new Thread(new Runnable() {
      public void run() {
        try {
          while(true) {
            Map<K, V> dead = null;
            Time.sleep(sleepTime);
            synchronized(_lock) {
              dead = _buckets.removeLast();
              _buckets.addFirst(new HashMap<K, V>());
            }
            if(_callback!=null) {
              for(Entry<K, V> entry: dead.entrySet()) {
                _callback.expire(entry.getKey(), entry.getValue());
              }
            }
          }
        } catch (InterruptedException ex) {

        }
      }
    });
    _cleaner.setDaemon(true);
    _cleaner.start();
  }

  public TimeCacheMap(int expirationSecs, ExpiredCallback<K, V> callback) {
    this(expirationSecs, DEFAULT_NUM_BUCKETS, callback);
  }

  public TimeCacheMap(int expirationSecs) {
    this(expirationSecs, DEFAULT_NUM_BUCKETS);
  }

  public TimeCacheMap(int expirationSecs, int numBuckets) {
    this(expirationSecs, numBuckets, null);
  }


  public boolean containsKey(K key) {
    synchronized(_lock) {
      for(HashMap<K, V> bucket: _buckets) {
        if(bucket.containsKey(key)) {
          return true;
        }
      }
      return false;
    }
  }

  public V get(K key) {
    synchronized(_lock) {
      for(HashMap<K, V> bucket: _buckets) {
        if(bucket.containsKey(key)) {
          return bucket.get(key);
        }
      }
      return null;
    }
  }

  public void put(K key, V value) {
    synchronized(_lock) {
      Iterator<HashMap<K, V>> it = _buckets.iterator();
      HashMap<K, V> bucket = it.next();
      bucket.put(key, value);
      while(it.hasNext()) {
        bucket = it.next();
        bucket.remove(key);
      }
    }
  }

  public Object remove(K key) {
    synchronized(_lock) {
      for(HashMap<K, V> bucket: _buckets) {
        if(bucket.containsKey(key)) {
          return bucket.remove(key);
        }
      }
      return null;
    }
  }

  public int size() {
    synchronized(_lock) {
      int size = 0;
      for(HashMap<K, V> bucket: _buckets) {
        size+=bucket.size();
      }
      return size;
    }
  }

  public void cleanup() {
    _cleaner.interrupt();
  }
}
>>>>>>> 4e71786502b717a049e61786020ef8d706624dc0
