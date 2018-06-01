# JVM内存空间分布
young | old | perm/metaspace |
---|---|---|
eden,s0,s1|    |  

# 触发Full GC
- old 区域满
- old 区域增加速度大于剩余空间
- perm 区域满
- cms gc出现 promotion failed和concurrent mode failure两种异常

```
promotion failed是在进行Minor GC时，survivor space放不下、对象只能放入旧生代，而此时旧生代也放不下造成的。
concurrent mode failure是在执行CMS GC的过程中同时有对象要放入旧生代，而此时旧生代空间不足造成的。
```

## GC类型
- Partial GC：并不收集整个GC堆的模式Young GC：只收集young gen的GC
- Old GC：只收集old gen的GC。只有CMS的concurrent collection是这个模式
- Mixed GC：收集整个young gen以及部分old gen的GC。只有G1有这个模式
- Full GC：收集整个堆，包括young gen、old gen、perm gen（如果存在的话）等所有部分的模式。

young GC：当young gen中的eden区分配满的时候触发。注意young GC中有部分存活对象会晋升到old gen，所以young GC后old gen的占用量通常会有所升高。

在s0,s1切换多次后(可以配置)，young区的对象会被放入old区域。
 
full GC：当准备要触发一次young GC时，如果发现统计数据说之前young GC的平均晋升大小比目前old gen剩余的空间大，则不会触发young GC而是转为触发full GC（因为HotSpot VM的GC里，除了CMS的concurrent collection之外，其它能收集old gen的GC都会同时收集整个GC堆，包括young gen，所以不需要事先触发一次单独的young GC）；或者，如果有perm gen的话，要在perm gen分配空间但已经没有足够空间时，也要触发一次full GC；或者System.gc()、heap dump带GC，默认也是触发full GC。


## GC 策略
- Serial收集器
一个单线程的收集器，在进行垃圾收集时候，必须暂停其他所有的工作线程直到它收集结束。
特点：CPU利用率最高，停顿时间即用户等待时间比较长。
适用场景：小型应用
通过JVM参数-XX:+UseSerialGC可以使用串行垃圾回收器。

- Parallel收集器
采用多线程来通过扫描并压缩堆
特点：停顿时间短，回收效率高，对吞吐量要求高。
适用场景：大型应用，科学计算，大规模数据采集等。
通过JVM参数 XX:+USeParNewGC 打开并发标记扫描垃圾回收器。

- CMS收集器
采用“标记-清除”算法实现，使用多线程的算法去扫描堆，对发现未使用的对象进行回收。
（1）初始标记
（2）并发标记
（3）并发预处理
（4）重新标记
（5）并发清除
（6）并发重置
特点：响应时间优先，减少垃圾收集停顿时间
适应场景：服务器、电信领域等。
通过JVM参数 -XX:+UseConcMarkSweepGC设置

- G1收集器
在G1中，堆被划分成 许多个连续的区域(region)。采用G1算法进行回收，吸收了CMS收集器特点。
特点：支持很大的堆，高吞吐量
  --支持多CPU和垃圾回收线程
  --在主线程暂停的情况下，使用并行收集
  --在主线程运行的情况下，使用并发收集
实时目标：可配置在N毫秒内最多只占用M毫秒的时间进行垃圾回收
通过JVM参数 –XX:+UseG1GC 使用G1垃圾回收器


## 空间信息
- -Xmx30m -Xms30m
```

jiangwh:classes jiangwh$ jmap -heap 1441
Attaching to process ID 1441, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.31-b07

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 31457280 (30.0MB)
   NewSize                  = 10485760 (10.0MB)
   MaxNewSize               = 10485760 (10.0MB)
   OldSize                  = 20971520 (20.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 8388608 (8.0MB)
   used     = 1192104 (1.1368789672851562MB)
   free     = 7196504 (6.863121032714844MB)
   14.210987091064453% used
From Space:
   capacity = 1048576 (1.0MB)
   used     = 0 (0.0MB)
   free     = 1048576 (1.0MB)
   0.0% used
To Space:
   capacity = 1048576 (1.0MB)
   used     = 0 (0.0MB)
   free     = 1048576 (1.0MB)
   0.0% used
PS Old Generation
   capacity = 20971520 (20.0MB)
   used     = 0 (0.0MB)
   free     = 20971520 (20.0MB)
   0.0% used

851 interned Strings occupying 55496 bytes.
```
- -Xmx30m -Xms30m -Xmn20m
```
jiangwh:classes jiangwh$ jmap -heap 1450
Attaching to process ID 1450, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.31-b07

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 31457280 (30.0MB)
   NewSize                  = 20971520 (20.0MB)
   MaxNewSize               = 20971520 (20.0MB)
   OldSize                  = 10485760 (10.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 15728640 (15.0MB)
   used     = 1912448 (1.8238525390625MB)
   free     = 13816192 (13.1761474609375MB)
   12.159016927083334% used
From Space:
   capacity = 2621440 (2.5MB)
   used     = 0 (0.0MB)
   free     = 2621440 (2.5MB)
   0.0% used
To Space:
   capacity = 2621440 (2.5MB)
   used     = 0 (0.0MB)
   free     = 2621440 (2.5MB)
   0.0% used
PS Old Generation
   capacity = 10485760 (10.0MB)
   used     = 0 (0.0MB)
   free     = 10485760 (10.0MB)
   0.0% used

851 interned Strings occupying 55496 bytes.
```
```

java -d64 -server -XX:+AggressiveOpts -XX:+UseLargePages -Xmn10g  -Xms26g -Xmx26g

新生代要足够大，使用大的分页。aggressiveOpts使用积极优化性能特性。

java -d64 -XX:+UseG1GC -Xms26g Xmx26g -XX:MaxGCPauseMillis=500 -XX:+PrintGCTimeStamp

GC不能够影响程序运行。

```

#### GC的选择

jvm应用程序：
> 除非遇到暂停问题，否则请尝试尽可能多地为虚拟机授予内存。默认大小通常太小。     
>设置-Xms和-Xmx相同的值通过从虚拟机中删除最重要的尺寸决定来提高可预测性。但是，如果您做出糟糕的选择，虚拟机将无法补偿。     
>一般来说，增加处理器的数量会增加内存，因为分配可以并行化。
