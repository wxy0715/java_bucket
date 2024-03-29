### 双机热备方案

这种方案是国内企业中最为普遍的一种高可用方案，双机热备其实就是指一台服务器在提供服务，另一台为某服务的备用状态，当一台服务器不可用另外一台就会顶替上去。

**keepalived是什么？**

`Keepalived`软件起初是专为`LVS`负载均衡软件设计的，用来管理并监控LVS集群系统中各个服务节点的状态，后来又加入了可以实现高可用的`VRRP (Virtual Router Redundancy Protocol ,虚拟路由器冗余协议）`功能。

因此，`Keepalived`除了能够管理LVS软件外，还可以作为其他服务`（例如：Nginx、Haproxy、MySQL等）`的高可用解决方案软件

**故障转移机制**

`Keepalived`高可用服务之间的故障切换转移，是通过`VRRP` 来实现的。

在 `Keepalived`服务正常工作时，主 `Master`节点会不断地向备节点发送（多播的方式）心跳消息，用以告诉备`Backup`节点自己还活着，当主 `Master`节点发生故障时，就无法发送心跳消息，备节点也就因此无法继续检测到来自主 `Master`节点的心跳了，于是调用自身的接管程序，接管主Master节点的 IP资源及服务。

### 实现过程

**准备工作**

192.168.16.128

192.168.16.129

两台虚拟机。安装好`Nginx`

**安装Nginx**

更新`yum`源文件：

```
rpm -ivh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
```

安装Nginx:

```
yum -y install  nginx
```

操作命令：

```
systemctl start nginx; #启动Nginx
systemctl stop nginx; #停止Nginx
```

**安装keepalived**

`yum`方式直接安装即可，该方式会自动安装依赖：

```
yum -y install keepalived
```

**修改主机（192.168.16.128）keepalived配置文件**

`yum`方式安装的会生产配置文件在`/etc/keepalived`下：

```
vi keepalived.conf
keepalived.conf:
#检测脚本
vrrp_script chk_http_port {
    script "/usr/local/src/check_nginx_pid.sh" #心跳执行的脚本，检测nginx是否启动
    interval 2                          #（检测脚本执行的间隔，单位是秒）
    weight 2                            #权重
}
#vrrp 实例定义部分
vrrp_instance VI_1 {
    state MASTER            # 指定keepalived的角色，MASTER为主，BACKUP为备
    interface ens33         # 当前进行vrrp通讯的网络接口卡(当前centos的网卡) 用ifconfig查看你具体的网卡
    virtual_router_id 66    # 虚拟路由编号，主从要一直
    priority 100            # 优先级，数值越大，获取处理请求的优先级越高
    advert_int 1            # 检查间隔，默认为1s(vrrp组播周期秒数)
    #授权访问
    authentication {
        auth_type PASS #设置验证类型和密码，MASTER和BACKUP必须使用相同的密码才能正常通信
        auth_pass 1111
    }
    track_script {
        chk_http_port            #（调用检测脚本）
    }
    virtual_ipaddress {
        192.168.16.130            # 定义虚拟ip(VIP)，可多设，每行一个
    }
}
```

`virtual_ipaddress` 里面可以配置vip,在线上通过vip来访问服务。

```
interface` 需要根据服务器网卡进行设置通常查看方式 `ip addr
```

`authentication`配置授权访问后备机也需要相同配置

**修改备机（192.168.16.129）keepalived配置文件**

```
keepalived.conf:
#检测脚本
vrrp_script chk_http_port {
    script "/usr/local/src/check_nginx_pid.sh" #心跳执行的脚本，检测nginx是否启动
    interval 2                          #（检测脚本执行的间隔）
    weight 2                            #权重
}
#vrrp 实例定义部分
vrrp_instance VI_1 {
    state BACKUP                        # 指定keepalived的角色，MASTER为主，BACKUP为备
    interface ens33                      # 当前进行vrrp通讯的网络接口卡(当前centos的网卡) 用ifconfig查看你具体的网卡
    virtual_router_id 66                # 虚拟路由编号，主从要一直
    priority 99                         # 优先级，数值越大，获取处理请求的优先级越高
    advert_int 1                        # 检查间隔，默认为1s(vrrp组播周期秒数)
    #授权访问
    authentication {
        auth_type PASS #设置验证类型和密码，MASTER和BACKUP必须使用相同的密码才能正常通信
        auth_pass 1111
    }
    track_script {
        chk_http_port                   #（调用检测脚本）
    }
    virtual_ipaddress {
        192.168.16.130                   # 定义虚拟ip(VIP)，可多设，每行一个
    }
}
```

**检测脚本：**

```
#!/bin/bash
#检测nginx是否启动了
A=`ps -C nginx --no-header |wc -l`        
if [ $A -eq 0 ];then    #如果nginx没有启动就启动nginx                        
      systemctl start nginx                #重启nginx
      if [ `ps -C nginx --no-header |wc -l` -eq 0 ];then    #nginx重启失败，则停掉keepalived服务，进行VIP转移
              killall keepalived                    
      fi
fi
```

脚本授权:`chmod 775 check_nginx_pid.sh`

说明：脚本必须通过授权，不然没权限访问啊，在这里我们两条服务器执行、`VIP(virtual_ipaddress:192.168.16.130)`,我们在生产环境是直接通过vip来访问服务。

模拟`nginx`故障：

修改两个服务器默认访问的`Nginx`的`html`页面作为区别。

首先访问`192.168.16.130`,通过`vip`进行访问，页面显示`192.168.16.128`；说明当前是主服务器提供的服务。

这个时候`192.168.16.128`主服务器执行命令：

```双机热备方案
systemctl stop nginx; #停止nginx
```