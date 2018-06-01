# linux操作
## iptable
> 防火墙   

> 利用iptables进行转发
```
iptables -t nat -A POSTROUTING -s 192.168.0.0/24 -o eth0 -j MASQUERADE
需要现开启内核转发
net.ip4.ip_forward=1
```
> 查看iptables的策略  
```iptables -L -n --line-number```

> 删除策略  
```iptables -D INPUT 5 ```
  
> 清除iptable  
```iptables -F ```

> 保存iptables策略  
```service iptables save```

> 开机启动  
``` chkconfig iptables on```


## dd
> 测试硬盘io速度

```
time dd if=/dev/zero of=/test.dbf bs=64k count=4k conv=fsync
```

## find
> 查找非重复文件
```
find . -not -empty -type f -printf "%s\n" |
sort -rn |
uniq -d |
xargs -I {} -n1 find -type f -size {} c -print0 | xargs -0 md5sum |
sort |
uniq -w32 --all-repeated=separate |
cut -b 36- 
```
find -not -empty -type f -printf "%s\n" | sort -rn | uniq -d |
xargs -I {} -n1 find -type f -size {} c -print0 | xargs -0 md5sum

> find . -not -empty -type f -print | sort -rn | uniq -d

## expect
> 自动交互执行脚本
```
#!/usr/bin/expect -f
set timeout 30
set password "xxx"
spawn ssh -l xxx xxx.xxx.xx.xx -p 13911
expect "password:"
send_user "in put password!"
send "$password\n"
#expect "]*"
send "su root\n" 
expect "密码："
send "xxx\n"
interact
```
## awk

## bash

## ssh
> 穿透nat，反向远程

```
被控制主机
ssh -NfR 10000:localhost:22 root@172.17.8.71 -p 22
密码为控制主机密码。

控制主机
ssh jiangwh@localhost -p 10000
密码为被控制主机密码。
```

## netstat
查看端口以及对应进程
```
netstat -nap 
```
