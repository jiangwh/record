# Kubernetes  集群搭建
## 安装master节点
### 配置仓库、关闭防火墙
>  epel-release软件包，这个软件包会自动配置yum的软件仓库。关闭防火墙，防止后续影响docker的iptable


```
yum -y install epel-release
systemctl stop firewalld
systemctl disable firewalld
setenforce 0
```
### 安装 etcd、kubernetes-master服务

```
yum -y install etcd kubernetes-master
```

### 修改配置
- 修改etcd的监听地址

```
vi /etc/etcd/etc.conf

[root@localhost etcd]# more etcd.conf 
#[Member]
#ETCD_CORS=""
ETCD_DATA_DIR="/var/lib/etcd/default.etcd"
#ETCD_WAL_DIR=""
#ETCD_LISTEN_PEER_URLS="http://localhost:2380"
ETCD_LISTEN_CLIENT_URLS="http://0.0.0.0:2379"
#ETCD_MAX_SNAPSHOTS="5"
#ETCD_MAX_WALS="5"
ETCD_NAME="default"
#ETCD_SNAPSHOT_COUNT="100000"
#ETCD_HEARTBEAT_INTERVAL="100"
#ETCD_ELECTION_TIMEOUT="1000"
#ETCD_QUOTA_BACKEND_BYTES="0"
#ETCD_MAX_REQUEST_BYTES="1572864"
#ETCD_GRPC_KEEPALIVE_MIN_TIME="5s"
#ETCD_GRPC_KEEPALIVE_INTERVAL="2h0m0s"
#ETCD_GRPC_KEEPALIVE_TIMEOUT="20s"
#
#[Clustering]
#ETCD_INITIAL_ADVERTISE_PEER_URLS="http://localhost:2380"
ETCD_ADVERTISE_CLIENT_URLS="http://localhost:2379"
#ETCD_DISCOVERY=""
#ETCD_DISCOVERY_FALLBACK="proxy"
#ETCD_DISCOVERY_PROXY=""
#ETCD_DISCOVERY_SRV=""
#ETCD_INITIAL_CLUSTER="default=http://localhost:2380"
#ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
#ETCD_INITIAL_CLUSTER_STATE="new"
#ETCD_STRICT_RECONFIG_CHECK="true"
#ETCD_ENABLE_V2="true"
#
#[Proxy]
#ETCD_PROXY="off"
#ETCD_PROXY_FAILURE_WAIT="5000"
#ETCD_PROXY_REFRESH_INTERVAL="30000"
#ETCD_PROXY_DIAL_TIMEOUT="1000"
#ETCD_PROXY_WRITE_TIMEOUT="5000"
#ETCD_PROXY_READ_TIMEOUT="0"
#
#[Security]
#ETCD_CERT_FILE=""
#ETCD_KEY_FILE=""
#ETCD_CLIENT_CERT_AUTH="false"
#ETCD_TRUSTED_CA_FILE=""
#ETCD_AUTO_TLS="false"
#ETCD_PEER_CERT_FILE=""
#ETCD_PEER_KEY_FILE=""
#ETCD_PEER_CLIENT_CERT_AUTH="false"
#ETCD_PEER_TRUSTED_CA_FILE=""
#ETCD_PEER_AUTO_TLS="false"
#
#[Logging]
#ETCD_DEBUG="false"
#ETCD_LOG_PACKAGE_LEVELS=""
#ETCD_LOG_OUTPUT="default"
#
#[Unsafe]
#ETCD_FORCE_NEW_CLUSTER="false"
#
#[Version]
#ETCD_VERSION="false"
#ETCD_AUTO_COMPACTION_RETENTION="0"
#
#[Profiling]
#ETCD_ENABLE_PPROF="false"
#ETCD_METRICS="basic"
#
#[Auth]
#ETCD_AUTH_TOKEN="simple"


```
- 修改apiserver的监听地址以及端口

```
vi /etc/kubernetes/apiserver

[root@localhost etcd]# more /etc/kubernetes/apiserver
###
# kubernetes system config
#
# The following values are used to configure the kube-apiserver
#

# The address on the local server to listen to.
KUBE_API_ADDRESS="--insecure-bind-address=0.0.0.0"

# The port on the local server to listen on.
KUBE_API_PORT="--port=8080"

# Port minions listen on
KUBELET_PORT="--kubelet-port=10250"

# Comma separated list of nodes in the etcd cluster
KUBE_ETCD_SERVERS="--etcd-servers=http://127.0.0.1:2379"

# Address range to use for services
KUBE_SERVICE_ADDRESSES="--service-cluster-ip-range=10.254.0.0/16"

# default admission control policies
KUBE_ADMISSION_CONTROL="--admission-control=NamespaceLifecycle,NamespaceExists,LimitRanger,SecurityContextDeny,Resour
ceQuota"

# Add your own!
KUBE_API_ARGS=""
```

### 重启服务
```
systemctl restart etcd
systemctl enable etcd
systemctl status etcd

systemctl restart kube-apiserver
systemctl enable kube-apiserver
systemctl status kube-apiserver

systemctl restart kube-controller-manager
systemctl enable kube-controller-manager
systemctl status kube-controller-manager

systemctl restart kube-scheduler
systemctl enable kube-scheduler
systemctl status kube-scheduler
```

## 安装Node节点
### 安装flannel网络、kubernetes-node   
```
yum -y install flannel kubernetes-node
```
### 修改flannel网络使用etcd服务
```
[root@localhost sysconfig]# more /etc/sysconfig/flanneld 
# Flanneld configuration options  

# etcd url location.  Point this to the server where etcd runs
FLANNEL_ETCD_ENDPOINTS="http://172.17.8.110:2379"

# etcd config key.  This is the configuration key that flannel queries
# For address range assignment
FLANNEL_ETCD_PREFIX="/atomic.io/network"

# Any additional options that you want to pass
#FLANNEL_OPTIONS=""
 
[root@localhost sysconfig]# more /etc/sysconfig/flanneld
# Flanneld configuration options  

# etcd url location.  Point this to the server where etcd runs
FLANNEL_ETCD_ENDPOINTS="http://172.17.8.110:2379"

# etcd config key.  This is the configuration key that flannel queries
# For address range assignment
FLANNEL_ETCD_PREFIX="/atomic.io/network"

# Any additional options that you want to pass
#FLANNEL_OPTIONS=""
 
```
### 修改kubernetes配置
```
[root@localhost kubernetes]# more /etc/kubernetes/kubelet 
###
# kubernetes kubelet (minion) config

# The address for the info server to serve on (set to 0.0.0.0 or "" for all interfaces)
KUBELET_ADDRESS="--address=0.0.0.0"

# The port for the info server to serve on
# KUBELET_PORT="--port=10250"

# You may leave this blank to use the actual hostname
KUBELET_HOSTNAME="--hostname-override=172.17.8.111"

# location of the api-server
KUBELET_API_SERVER="--api-servers=http://172.17.8.110:8080"

# pod infrastructure container
KUBELET_POD_INFRA_CONTAINER="--pod-infra-container-image=registry.access.redhat.com/rhel7/pod-infrastructure:latest"

# Add your own!
KUBELET_ARGS=""
```

```
[root@localhost kubernetes]# more /etc/kubernetes/config 
###
# kubernetes system config
#
# The following values are used to configure various aspects of all
# kubernetes services, including
#
#   kube-apiserver.service
#   kube-controller-manager.service
#   kube-scheduler.service
#   kubelet.service
#   kube-proxy.service
# logging to stderr means we get it in the systemd journal
KUBE_LOGTOSTDERR="--logtostderr=true"

# journal message level, 0 is debug
KUBE_LOG_LEVEL="--v=0"

# Should this cluster be allowed to run privileged docker containers
KUBE_ALLOW_PRIV="--allow-privileged=false"

# How the controller-manager, scheduler, and proxy find the apiserver
KUBE_MASTER="--master=http://172.17.8.110:8080"
```


### 重启服务
```
systemctl restart kube-proxy
systemctl enable kube-proxy
systemctl status kube-proxy
 
systemctl restart kubelet
systemctl enable kubelet
systemctl status kubelet
 
systemctl restart docker
systemctl enable docker
systemctl status docker
 
systemctl restart flanneld
systemctl enable flanneld
systemctl status flanneld
```

## 集群验证
- master 节点验证集群是否正常  

```
[root@localhost etcd]# kubectl get nodes
NAME           STATUS    AGE
172.17.8.111   Ready     1h
172.17.8.112   Ready     1h
```

- 创建rc  

```
kubectl create -f 
```

- 创建svc  

```
kubectl create -f 
```

- 确认Pods  

```
[root@localhost etcd]# kubectl get pods
NAME          READY     STATUS    RESTARTS   AGE
mysql-1rzjr   1/1       Running   0          48m
```

- 确认rc  

```
[root@localhost etcd]# kubectl get rc
NAME      DESIRED   CURRENT   READY     AGE
mysql     1         1         1         1h
```

- 确认svc  

```
[root@localhost etcd]# kubectl get svc
NAME         CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
kubernetes   10.254.0.1     <none>        443/TCP          2h
mysql        10.254.89.86   <nodes>       3306:32706/TCP   1h
```

## Troubleshoot
### overlay网络问题
```
ERROR: XFS filesystem at /var has ftype=0, cannot use overlay backend; consider different driver or separate volume or OS reprovision
```

```
[root@localhost sysconfig]# vi docker

# /etc/sysconfig/docker

# Modify these options if you want to change the way the docker daemon runs
OPTIONS='--selinux-enabled =false
```
-------------------------
### 授权问题
```
No API token found for service account "default", retry after the token is automatically created and added to the service account
```
```
openssl genrsa -out /etc/kubernetes/serviceaccount.key 2048
vi /etc/kubenetes/apiserver
KUBE_API_ARGS="--service_account_key_file=/etc/kubernetes/serviceaccount.key"
vi /etc/kubernetes/controller-manager 
KUBE_CONTROLLER_MANAGER_ARGS="--service_account_private_key_file=/etc/kubernetes/serviceaccount.key"

```
-----------------------------
### 证书问题
```
docker pull registry.access.redhat.com/rhel7/pod-infrastructure:latest
缺少证书
```

```
yum install *rhsm*  
```
上面的命令无法解决问题,需要进行remove回滚操作。

```
wget http://mirror.centos.org/centos/7/os/x86_64/Packages/python-rhsm-1.19.10-1.el7_4.x86_64.rpm
wget http://mirror.centos.org/centos/7/os/x86_64/Packages/python-rhsm-certificates-1.19.10-1.el7_4.x86_64.rpm

rpm -i python-rhsm-certificates-1.19.10-1.el7_4.x86_64.rpm
rpm -ivh python-rhsm-1.19.10-1.el7_4.x86_64.rpm
```
> 在节点上面运行时，会自动remove kubernetes-node的包，只有找一台安装的服务器，copy "/etc/rhsm/ca/redhat-uep.pem"文件


### 伸缩相关命令

> 外部无法访问端口时，需要进行Iptable放行
iptables -P FORWARD ACCEPT

> 修改副本数量

```
kubectl scale --replicas=3 -f mywebapp-rc.yaml 
kubectl scale --replicas=1 rc/mywebapp
```

> 自动伸缩

```
kubectl autoscale rc mywebapp --max=5 --cpu-percent=50
```
> 滚动升级

```
kubectl rolling-update mywebapp mywebapp-v1 --image=mywebapp:1.2.0
```

> 回滚

当升级到一半发现存在问题，需要中断升级命令，执行回滚命令。
```
kubectl rolling-update mywebapp-v1 mywebapp-v2 --rollback
```
