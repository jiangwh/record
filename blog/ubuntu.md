# ubuntu操作

## 远程桌面

```
 sudo apt-get install rdesktop
 rdesktop -u R05145 -p xxx 192.168.54.32:3899
 rdesktop -u R05145 -p xxx -g 1600x900 192.168.54.32:3389 
 -f 全屏
 -g 指定分辨率
```

## 安装docker

```
sudo apt-get remove docker docker-engine docker.io

查看本地镜像库
sudo docker image ls

sudo docker image pull library/hello-world

sudo docker container run hello-world

sudo docker container run -it ubuntu bash


sudo docker container ls --all
sudo docker container kill [containID]

端口映射 
sudo docker container run -p 8000:3000 -it koa-demo /bin/bash

sudo docker container run -p 8000:3000 -it koa-demo:0.0.1 /bin/bash

```

>-p参数：容器的 3000 端口映射到本机的 8000 端口。    
-it参数：容器的 Shell 映射到当前的     Shell，然后你在本机窗口输入的命令，就会传入容器。   
koa-demo:0.0.1：image 文件的名字（如果有标签，还需要提供标签，默认是 latest 标签）。   
/bin/bash：容器启动以后，内部第一个执行的命令。这里是启动 Bash，保证用户可以使用 Shell。

```
查看日志 
docker container logs [containerID]

构建应用
sudo docker build -t docker .

查看可以运行的镜像
sudo docker image ls

运行docker
sudo docker run -d -p 8080:8080 docker

sudo docker run springboot:latest

docker run -t -i ubuntu:14.04 /bin/bash

导出容器
docker	export	7691a814370e > ubuntu.tar
导入容器
cat	ubuntu.tar | docker import - test/ubuntu:v1.0

删除容器
docker container rm trusting_newton
```

maven plugs for docker
```
maven docker 
DOCKER_HOST=unix:///var/run/docker.sock mvn clean install
mvn package docker:build

```


## 命令行启动方式
```
/etc/default
sudo vi grub 


GRUB_CMDLINE_LINUX="text"
GRUB_TERMINAL=console

sudo update-grub


```

```
docker for mac
docker search mysql
docker pull mysql:latest
docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin -d  mysql:latest
docker exec -it mysql bash
```
