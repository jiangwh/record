# Unsafe
```
Constructor<Unsafe> constructor =
Unsafe.class.getDeclaredConstructor(new Class<?>[0]);
constructor.setAccessible(true);
Unsafe unsafe = constructor.newInstance(new Object[0]);
//objectFieldOffset 计算字段 相对类的偏移量 long offset
unsafe.objectFieldOffset(Test.class.getDeclaredField("s"));
//cas操作,返回操作成功或者失败
boolean res = unsafe.compareAndSwapInt(t, offset, 1, 1);
```

```
AbstractQueuedSynchronizer
//定义一个节点
Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
//定义前一个节点    
Node pred = tail;
if (pred != null) {
    //建立当前节点与前一个节点的关系
    node.prev = pred;
    //如果跟期望一样，那么就定义前一个节点的后续关系
    if (compareAndSetTail(pred, node)) {
        pred.next = node;
        return node;
    }
}
//针对tail为null的处理
for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }

```