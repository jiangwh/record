# Postgresql 数据库事务
psql -d YKTDBDEV -U postgres -W 
```
BEGIN TRANSACTION;
select * from t_user where id=32768;
commit;
-- rollback;
```

```
-- 查看数据库的数据库级别
select current_setting('transaction_isolation'); 
show default_transaction_isolation;

-- 修改事务级别
begin;
set transaction isolation level serializable;
commit;
-- 设置会话
SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

> 事务级别解释

```
dirty read    
A transaction reads data written by a concurrent uncommitted transaction.  
脏读：一个事务读取到另外一个事务未提交的数据。

nonrepeatable read  
A transaction re-reads data it has previously read and finds that data has been modified by another transaction (that committed since the initial read).  
不可重复读：一个事务再次读取数据时，发现 数据被例外一个事务修改了。
不可重复读没有行级锁。

phantom read  
A transaction re-executes a query returning a set of rows that satisfy a search condition and finds that the set of rows satisfying the condition has changed due to another recently-committed transaction.  
幻读：一个事务再次执行一个不改变查询条件的查询语句时发现返回的结果集增加了。
幻读存在纪录的行级锁。

serialization anomaly
The result of successfully committing a group of transactions is inconsistent with all possible orderings of running those transactions one at a time.
```

```
READ_UNCOMMITTED  支持脏读

A constant indicating that dirty reads, non-repeatable reads and phantom reads can occur. This level allows a row changed by one transaction to be read by another transaction before any changes in that row have been committed (a "dirty read"). If any of the changes are rolled back, the second transaction will have retrieved an invalid row.

1、T1：insert into `users`(`id`, `name`) values (1, 'XXX');
2、T2：select * from users where id=1;
T1事务未提交，T2事务可以读取。

READ_COMMITTED
阻止脏读，存在幻读以及不可重复读。
A constant indicating that dirty reads are prevented; non-repeatable reads and phantom reads can occur. This level only prohibits a transaction from reading a row with uncommitted changes in it.

不可重复读
1、T1：select * from users where id=1;
2、T2：update users set name="test" where id=1
3、T1: select * from users where id=1;
T1事务两次结果不一致。

REPEATABLE_READ
阻止脏读及不可重复读。存在幻读。
A constant indicating that dirty reads and non-repeatable reads are prevented; phantom reads can occur. This level prohibits a transaction from reading a row with uncommitted changes in it, and it also prohibits the situation where one transaction reads a row, a second transaction alters the row, and the first transaction rereads the row, getting different values the second time (a "non-repeatable read").

1、T1：select * from users where id = 1; -- null
2、T2：insert into `users`(`id`, `name`) values (1, 'XXX');
3、T1：select * from users where id = 1; -- null

T1查询users表不存在id为1的数据，T1再次查询id为1的数据不为空，这个叫幻读。


SERIALIZABLE

A constant indicating that dirty reads, non-repeatable reads and phantom reads are prevented. This level includes the prohibitions in ISOLATION_REPEATABLE_READ and further prohibits the situation where one transaction reads all rows that satisfy a WHERE condition, a second transaction inserts a row that satisfies that WHERE condition, and the first transaction rereads for the same condition, retrieving the additional "phantom" row in the second read.

```

