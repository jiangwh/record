# SQL Server数据的相关操作
> 翻出了很久以前纪录的日志，上传至博客备忘。

## 强制语句查询索引
```
SELECT COUNT(1) FROM ACCT_FLOW with(index =idx_acct_flow_ct) WHERE CREATE_TIME >='2014-03-01 00:00:00' 
```
## 语句加锁操作

```
SELECT * FROM table WITH (HOLDLOCK) //不可修改
SELECT * FROM table WITH (TABLOCKX) //不可以执行任何操作
执行加锁
SELECT * from USERINFO (XLOCK) WHERE USER_ID='111' --排他锁
select * from USERINFO with(updlock) where USER_ID='111'
```

## 查询锁定的对象
```
SELECT request_session_id spid,OBJECT_NAME(resource_associated_entity_id)tableName 
FROM sys.dm_tran_locks 
WHERE resource_type='OBJECT '
```
## 查询引起死锁的进程
```
use master
go
declare @spid int,@bl int
DECLARE s_cur CURSOR FOR
select  0 ,blocked
from (select * from sysprocesses where  blocked>0 ) a
where not exists(select * from (select * from sysprocesses where  blocked>0 ) b
where a.blocked=spid)
union select spid,blocked from sysprocesses where  blocked>0
OPEN s_cur
FETCH NEXT FROM s_cur INTO @spid,@bl
WHILE @@FETCH_STATUS = 0
begin
if @spid =0
select '引起数据库死锁的是:
'+ CAST(@bl AS VARCHAR(10)) + '进程号,其执行的SQL语法如下'
else
select '进程号SPID：'+ CAST(@spid AS VARCHAR(10))+ '被' + '
进程号SPID：'+ CAST(@bl AS VARCHAR(10)) +'阻塞,其当前进程执行的SQL语法如下'
DBCC INPUTBUFFER (@bl )
FETCH NEXT FROM s_cur INTO @spid,@bl
end

CLOSE s_cur
DEALLOCATE s_cur
```
## 设置事务级别
```
SET Transaction Isolation Level Read UNCOMMITTED
set transaction isolation level repeatable read

begin tran
SELECT * FROM USERINFO WHERE USER_ID='111'
   --共享锁
commit tran

```


select * from rs.SAMDBIPFIX.dbo.ONLINE_USER
## DBlink 操作

```
exec sp_addlinkedserver 'rs' , '' , 'SQLOLEDB' , '192.168.54.32'

exec sp_addlinkedsrvlogin 'rs' , 'false' , null , 'sa' , 'sa@123'
insert into(对应字段) 表 select 对应字段  from 远程库 (远程库实例：rs.SAMDBIPFIX.dbo.ONLINE_USER)
select * into taget_table from src_table;
```
## 性能语句
```
SELECT TOP 10
[session_id],
[request_id],
[start_time] AS '开始时间',
[status] AS '状态',
[command] AS '命令',
dest.[text] AS 'sql语句', 
DB_NAME([database_id]) AS '数据库名',
[blocking_session_id] AS '正在阻塞其他会话的会话ID',
[wait_type] AS '等待资源类型',
[wait_time] AS '等待时间',
[wait_resource] AS '等待的资源',
[reads] AS '物理读次数',
[writes] AS '写次数',
[logical_reads] AS '逻辑读次数',
[row_count] AS '返回结果行数'
FROM sys.[dm_exec_requests] AS der 
CROSS APPLY 
sys.[dm_exec_sql_text](der.[sql_handle]) AS dest 
WHERE [session_id]>50 AND DB_NAME(der.[database_id])='gposdb'  
ORDER BY [cpu_time] DESC

--在SSMS里选择以文本格式显示结果
SELECT TOP 10 
dest.[text] AS 'sql语句'
FROM sys.[dm_exec_requests] AS der 
CROSS APPLY 
sys.[dm_exec_sql_text](der.[sql_handle]) AS dest 
WHERE [session_id]>50  
ORDER BY [cpu_time] DESC
```
## 索引整理
```
ALTER INDEX ALL ON LOG REORGANIZE;
ALTER INDEX ALL ON LOG REBUILD WITH (ONLINE = ON);
```
## DBCC工具
```
DBCC sqlperf(logspace);
DBCC SHRINKFILE(N'SAMDB_log',50);
sp_helpdb 'SAMDB'
DBCC DROPCLEANBUFFERS;
```
## 执行计划
```
SET STATISTICS PROFILE ON;       ——执行计划
SET STATISTICS TIME ON;
SET STATISTICS IO ON;
```