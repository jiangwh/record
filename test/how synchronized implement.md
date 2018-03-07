# synchronized实现原理      
站在使用者的角度查看synchronized的原理     
- 在java中使用synchronized
```
    public static void main(String[] args) {
		synchronized (LRUCache.class) {
			new LRUCache<>(1).toString();
		}
	}
```
jvm针对上面的代码编译成指令后如下：
```
 public static void main(java.lang.String[]);
    Code:
       0: ldc           #16                 // class com/jiangwh/cache/LRUCache
       2: dup
       3: astore_1
       4: monitorenter
       5: new           #16                 // class com/jiangwh/cache/LRUCache
       8: dup
       9: iconst_1
      10: invokespecial #17                 // Method "<init>":(I)V
      13: invokevirtual #18                 // Method toString:()Ljava/lang/String;
      16: pop
      17: aload_1
      18: monitorexit
      19: goto          27
      22: astore_2
      23: aload_1
      24: monitorexit
      25: aload_2
      26: athrow
      27: return
    Exception table:
       from    to  target type
           5    19    22   any
          22    25    22   any

```
如果我们把synchronized关键字删除，指令会变成如下：
```
  public static void main(java.lang.String[]);
    Code:
       0: new           #16                 // class com/jiangwh/cache/LRUCache
       3: dup
       4: iconst_1
       5: invokespecial #17                 // Method "<init>":(I)V
       8: invokevirtual #18                 // Method toString:()Ljava/lang/String;
      11: pop
      12: return
```
对比一下，可以看出在使用synchronized关键的代码中增加了```monitorenter```指令。
摘录下jdk的规范文档：
> Synchronization of sequences of instructions is typically used to encode the synchronized block of the Java programming language.       The Java Virtual Machine supplies the monitorenter and monitorexit instructions to support such language constructs.     Proper implementation of synchronized blocks requires cooperation from a compiler targeting the Java Virtual Machine (§3.14).
