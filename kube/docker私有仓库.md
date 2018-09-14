# 使用nexus存储docker镜像
## 安装nexus
## 为docker创建本地存储
- create repository
- choose hosted type
- http配置

```
Connectors allow Docker clients to connect directly to hosted registries, but are not always required. Consult our documentation for which connector is appropriate for your use case.
Create an HTTP connector at specified port. Normally used if the server is behind a secure proxy.
```

我们选择使用了8082端口
## 配置docker

增加仓库 --insecure-registry ip：port

```
[root@localhost dockerfile]# more /etc/sysconfig/docker
# /etc/sysconfig/docker

# Modify these options if you want to change the way the docker daemon runs
OPTIONS='--selinux-enabled=false --log-driver=journald --signature-verification=false --registry-mirror=https://olzwzeg2.mirror.aliy
uncs.com --insecure-registry 192.168.54.25:8082  --label name=docker_server_73'
if [ -z "${DOCKER_CERT_PATH}" ]; then
    DOCKER_CERT_PATH=/etc/docker
fi

# Do not add registries in this file anymore. Use /etc/containers/registries.conf
# from the atomic-registries package.
#

# On an SELinux system, if you remove the --selinux-enabled option, you
# also need to turn on the docker_transition_unconfined boolean.
# setsebool -P docker_transition_unconfined 1

# Location used for temporary files, such as those created by
# docker load and build operations. Default is /var/lib/docker/tmp
# Can be overriden by setting the following environment variable.
# DOCKER_TMPDIR=/var/tmp

# Controls the /etc/cron.daily/docker-logrotate cron job status.
# To disable, uncomment the line below.
# LOGROTATE=false

# docker-latest daemon can be used by starting the docker-latest unitfile.
# To use docker-latest client, uncomment below lines
#DOCKERBINARY=/usr/bin/docker-latest
#DOCKERDBINARY=/usr/bin/dockerd-latest
#DOCKER_CONTAINERD_BINARY=/usr/bin/docker-containerd-latest
#DOCKER_CONTAINERD_SHIM_BINARY=/usr/bin/docker-containerd-shim-latest
```

## 上传docker镜像  

- 登录私有仓库
```
docker login 192.168.54.25:8082
```

- 找到需要上传的镜像，这里准备上传myweb

```
[root@localhost sysconfig]# docker images
REPOSITORY                                                       TAG                 IMAGE ID            CREATED             SIZE
apache/rocketmq-broker                                           4.2.0-k8s           ea883cacc992        3 days ago          373 MB
apache/rocketmq-namesrv                                          4.2.0-k8s           1ff4176f5a3f        3 days ago          373 MB
apache/rocketmq-base                                             4.2.0               53215c52fa5c        3 days ago          373 MB
quay.io/kubernetes-ingress-controller/nginx-ingress-controller   0.19.0              22ebbdddfabb        4 days ago          414 MB
mywebapp                                                         1.1.0               4fa6e92832e5        5 days ago          584 MB
mywebapp                                                         1.2.0               4fa6e92832e5        5 days ago          584 MB
myweb                                                            1.0.0               c60b88eb4808        5 days ago          584 MB
myweb                                                            latest              33da70d47db9        5 days ago          584 MB
docker.io/mongo                                                  latest              8bf72137439e        3 weeks ago         380 MB
docker.io/centos                                                 7                   5182e96772bf        4 weeks ago         200 MB
ddd                                                              latest              152050092207        7 weeks ago         617 MB
ddd                                                              1.0                 26dd55249101        7 weeks ago         617 MB
docker.io/apache/syncope                                         2.1.0               a837d17ec061        7 weeks ago         630 MB
docker.io/oraclelinux                                            latest              45bfd0efe21c        8 weeks ago         234 MB
docker.io/postgres                                               latest              730027eb9d78        2 months ago        236 MB
docker.io/sentry                                                 latest              dfe5147051ab        2 months ago        595 MB
docker.io/mysql                                                  latest              8d99edb9fd40        2 months ago        445 MB
docker.io/centos                                                 latest              49f7960eb7e4        3 months ago        200 MB
registry.access.redhat.com/rhel7/pod-infrastructure              latest              99965fb98423        10 months ago       209 MB
docker.io/kubeguide/tomcat-app                                   v1                  a29e200a18e9        2 years ago         358 MB
docker.io/nginx                                                  1.7.9               84581e99d807        3 years ago         91.7 MB
```

- 修改镜像

```
docker tag myweb 192.168.54.25:8082/myweb:latest
```

- 确认镜像是否修改

```
[root@localhost dockerfile]# docker images
REPOSITORY                                                       TAG                 IMAGE ID            CREATED             SIZE
apache/rocketmq-broker                                           4.2.0-k8s           ea883cacc992        3 days ago          373 MB
apache/rocketmq-namesrv                                          4.2.0-k8s           1ff4176f5a3f        3 days ago          373 MB
apache/rocketmq-base                                             4.2.0               53215c52fa5c        3 days ago          373 MB
quay.io/kubernetes-ingress-controller/nginx-ingress-controller   0.19.0              22ebbdddfabb        4 days ago          414 MB
mywebapp                                                         1.1.0               4fa6e92832e5        5 days ago          584 MB
mywebapp                                                         1.2.0               4fa6e92832e5        5 days ago          584 MB
myweb                                                            1.0.0               c60b88eb4808        5 days ago          584 MB
192.168.54.25:8082/myweb                                         latest              33da70d47db9        5 days ago          584 MB
myweb                                                            latest              33da70d47db9        5 days ago          584 MB
docker.io/mongo                                                  latest              8bf72137439e        3 weeks ago         380 MB
docker.io/centos                                                 7                   5182e96772bf        4 weeks ago         200 MB
ddd                                                              latest              152050092207        7 weeks ago         617 MB
ddd                                                              1.0                 26dd55249101        7 weeks ago         617 MB
docker.io/apache/syncope                                         2.1.0               a837d17ec061        7 weeks ago         630 MB
docker.io/oraclelinux                                            latest              45bfd0efe21c        8 weeks ago         234 MB
docker.io/postgres                                               latest              730027eb9d78        2 months ago        236 MB
docker.io/sentry                                                 latest              dfe5147051ab        2 months ago        595 MB
docker.io/mysql                                                  latest              8d99edb9fd40        2 months ago        445 MB
docker.io/centos                                                 latest              49f7960eb7e4        3 months ago        200 MB
registry.access.redhat.com/rhel7/pod-infrastructure              latest              99965fb98423        10 months ago       209 MB
docker.io/kubeguide/tomcat-app                                   v1                  a29e200a18e9        2 years ago         358 MB
docker.io/nginx                                                  1.7.9               84581e99d807        3 years ago         91.7 MB
```

- 上传镜像

```
[root@localhost dockerfile]# docker push 192.168.54.25:8082/myweb
The push refers to a repository [192.168.54.25:8082/myweb]
c5aee7e74d24: Pushed 
278b967ecbe3: Pushed 
bcc97fbfc9e1: Pushed 
latest: digest: sha256:94fac388fd8ee9c29192e6f66820924596f003afe8758a41eca39ad79ab52a3f size: 954
```