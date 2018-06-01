# java应用诊断
下面的步骤可能不会适用于所有java应用程序。
java应用诊断的相关步骤：
- 应用数据采集。
- 使用工具进行诊断。
- 针对内存的诊断。
- 针对性能的诊断。
- jvm bug的诊断。

## 应用数据采集  ##     
这里的应用数据采集是要采集到与系统性能、内存相关的数据，并非应用程序的所有日志数据。针对应用自身数据，根据应用需求打印日志，考虑使用日志框架，方便调节日志的级别，诊断应用。这里主要是针对性能相关数据。如何进行采集数据呢？   
- 采集程序奔溃时的内存数据（core file）。
- 采集内存溢出时的heap dump数据。
- 采集垃圾回收日志。
- 采集启动时jvm的相关参数。
- 采集实时运行数据（如：thread dump数据）。
- 采集内存、cpu使用数据。

在java应用具体的如何操作：
- core file
> 在Java应用crashes的时候可以产生core文件。在linux服务器上面要```ulimit -c unlimited```打开。
- heap dump
> 在jvm启动时增加```-XX:+HeapDumpOnOutOfMemoryError```参数，那么java应用程序由于OOM异常导致退出时，会产生heap dump信息。
- gc log
> 在jvm启动的参数中增加```-verbosegc```就会打印gc日志。
- jvm 参数
> 在诊断异常时，查看jvm的启动参数也很重要。比如：jvm的gc策略、内存分配的情况，通过启动参数可以查看出来。在启动参数中增加``` -XX+PrintCommandLineFlags```用来打印启动命令参数。
- thread dump
> 对于thread dump信息应该在诊断时，实时收集数据，对比数据处理。可以使用jstack打印对应的thread dump信息。
- 内存、cpu数据
> 这些数据也时实时收集的，可以通过jmc工具收集。可以通过开启jmx来通过监控mbean获取相关数据。开启jmx的另外一个好处就是可以通过远程进行监控。

## 针对应用进行诊断
应用诊断可以分为好几类：
- 事后诊断
> 此类工具可以用于在故障发生后，针对故障的信息进行的诊断。如：oom之后对heap dump的分析。
- 在线诊断
> 此类工具针对故障发生时，进行的针对。如：程序锁住时，对thread dump的分析。
- 监控
> 此类工具用于监控运行的应用程序。
- 其他
> 如何诊断使用jni的应用程序，使用```-Xcheck:jni```诊断 jni接口,```-verbose:jni```打印jni接口的日志。
这里只是想按照诊断处理的时间以及方式分一下类，但是正常使用的诊断工具就是那么固定的一些。

### 常用的诊断参数以及诊断工具介绍
- ```-XX:OnError```在启动java应用时增加该参数，可以在应用程序退出时执行用户定义的脚本。如 java -XX:OnError="mail jiangwh@mail.com" app.jar，在错误时发送邮件给管理员(jiangwh@mail.com)。
- ```-XX:+HeapDumpOnOutOfMemoryError```在oom异常时存储应用程序的heap dump信息。前面已经提到过该命令。
- ```-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,disk=true,repository=/tmp,maxage=1h,settings=default```该命令可以让应用程序存储jfr文件，可以使用jmc工具进行查看。
- 

### 诊断工具使用
- jmc
```
   
```
- jconsole
```
   
```
- jvisualvm
```
   
```
- jps
```
 
```
- jmap
```

```
- jhat
```
```
- jstat
```
```
- jstack
```
```
- jcmd
```

```


## 诊断的问题
- 内存溢出
> 内存溢出的问题，有可能比较难排查。我们需要对比内存数据的状态。可以使用jcmd 命令对比commit的内存，如果commit的内存越来越多，那么极有可能存在内存泄漏的问题。

- 性能

> gc性能    
由于gc导致的性能问题。gc对应程序的性能有很大的影响。gc耗费的时间长不是问题，问题在于gc时暂停耗费了很多时间。如果一次独立的gc耗费的时间太长，那么需要考虑更改gc策略。   

> 同步性能    
  利用同步锁导致的性能问题。       
  
> IO性能
  由于socket、文件读写导致的性能问题。      
  
> 代码执行性能     
  查看代码执行的性能。

- 线程问题
线程的问题，一般都在运行时表现出来。线程主要会存在以下两个问题：
> 线程循环

> 线程hang住
