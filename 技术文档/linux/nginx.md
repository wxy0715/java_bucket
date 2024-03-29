# Nginx



## nginx进程模型

![image-20221111095852457](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221111095852457.png)



## Nginx的事件处理

**nginx采用异步非阻塞的方式处理客户端请求 并发量跟随服务器性能决定**

![image-20221111102214843](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221111102214843.png)

![image-20221111102245233](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221111102245233.png)



## Nginx配置

![image-20221111102609280](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221111102609280.png)

### root

当配置了root参数时：访问路径即为 root后的路径拼接location的路径 

第一种 访问根---->html/foodie-shop/index.html

第二种 访问imooc----> html/imooc

第三种起别名 访问static----> html/imooc

![image-20221116145929259](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221116145929259.png)

### gzip

![image-20221116152201988](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221116152201988.png)



### location

![image-20221116162946182](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221116162946182.png)

### 跨域

![image-20221116165011331](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221116165011331.png)

### [防盗链](https://blog.csdn.net/wzj_110/article/details/111769821)

![image-20221116165223860](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221116165223860.png)

### expiress缓存过期时间

![image-20221206200750867](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206200750867.png)

![image-20221206200849262](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206200849262.png)

 **晚上22时30分**

![image-20221206201051442](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206201051442.png)

![image-20221206202957159](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206202957159.png)





## upstream

### max_conns 最大并发量

![image-20221206135647316](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206135647316.png)

### slow_start(从0开始慢慢的提高权重)

![image-20221206143243379](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206143243379.png)

### down (该服务器暂时不可用)

### backup(其他服务器宕机时候才会启动这一台)

### max_fails和fail_timeout

```
当用户请求在fail_timeout时间内失败了max_fails次,则fail_timeout时间内不会再有请求分配到该机器,过了fail_timeout时间后会重置(用户可以继续访问)
```

## [keepalive提高吞吐量](https://blog.csdn.net/qq_34168988/article/details/125207437)

![image-20221206145513660](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206145513660.png)



## Nginx日志切割

编写一个sh脚本，在crontab里加一个每天执行一次的cron语句即可![image-20221111164415959](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221111164415959.png)



## Nginx搭建3台tomcat服务器

![image-20221205222039661](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221205222039661.png)

![image-20221205222232056](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221205222232056.png)

### 权重

![image-20221205222551351](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221205222551351.png)

### ip_hash 负载均衡

```
保证了同一个用户访问的服务器不变,用了hash算法后不能删除server只能upstream->down,不然hash失效
```

![image-20221206150338684](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206150338684.png)

 ### hash

```
upstream tomcats {
		hash $request_uri;
}
```



### least_conn (最少连接数优先)

![image-20221206164639572](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206164639572.png)





## SSL

```
1.先买一个域名,然后申请一个证书
2.dns解析
3.pem和key文件下载,然后上传到nginx.conf同级
```

![image-20221206213341483](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206213341483.png)

![image-20221206213442960](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221206213442960.png)

```sh
server {
    listen 443 ssl;
    server_name www.cjree.cn;
    root html;
    index index.html index.htm;
    ssl_certificate cert/ssl.pem;
    ssl_certificate_key cert/ssl.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1.1 TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    location / {
        proxy_pass http://tomcats;
        proxy_http_version 1.1;
        proxy_set_header Connection "";
    }
}

# 负载均衡
upstream tomcats{
server 101.43.76.117:8080 weight=1 max_conns=2;
server 101.43.60.8:8080 weight=1 max_conns=2;
server 175.24.174.18:8080 weight=1 max_conns=2;
keepalive 32;
}

```



## keepalive双机热备

### 安装

```
下载安装包 解压
配置 ./configure --prefix=/usr/local/keepalived --sysconf=/etc
make && make install
whereis keepalived
```

### 配置文件

```sh
! Configuration File for keepalived

global_defs {
   # 路由id 当前安装keepalived的全局唯一id
   router_id keep_1014376117
}

#计算机节点
vrrp_instance VI_1 {
    # 主节点
    state MASTER
    # 当前实例绑定的网卡
    interface eth0
    # 保证主备节点一致
    virtual_router_id 51
    # 优先级,谁的优先级高,在master挂了以后会优先变为master
    priority 100
    # 主备之间同步检查的时间间隔,默认1s
    advert_int 1
    # 认证授权的密码,防止非法节点进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    unicast_src_ip 10.123.4.12 #单播模式，本机内网 IP
    unicast_peer {
    	10.123.4.3 #备机的内网 IP
    }
        track_interface {
    	eth0 #跟踪接口跟踪接口,,设置额外的监控设置额外的监控
    }
    strict_mode off #1.3以上以上KEEPALIENDKEEPALIEND版本使用单播模式需要配置版本使用单播模式需要配置
    
    virtual_ipaddress {
       10.0.16.13
    }
}
```

### Keepalived配置Nginx自动重启

```sh
1.增加Nginx重启检测脚本
vim /etc/keepalived/check_nginx_alive_or_not.sh
#!/bin/bash
A=`ps -C nginx --no-header | wc -l` 
#判断nginx是否宕机，如果宕机了，尝试重启
if[ $A -eq 0 ];then
 /usr/local/nginx/sbin/nginx
 #等待一小会再次检查nginx，如果没有启动成功，则停止keepalived，使其启动备用机
 sleep 3
 if[ `ps -C nginx --no-header wc -l` -eq 0 ];then
    killall keepalived
 fi
fi
#增加运行权限
chmod +x /etc/keepalived/check_nginx_alive_or_not.sh

2.配置keepalived监听nginx脚本
vrrp_scriptcheck_nginx_alive
{
  script "/etc/keepalived/check_nginx_alive_or_not.sh"
  interval 2 #每隔两秒运行上一行脚本weight10#如果脚本运行失败，则升级权重+10
}

3.在vrrp_instance中新增监控的脚本
track_script{
  check_nginx_alive#追踪nginx脚本
}

4.重启Keepalived使得配置文件生效
systemctl restart keepalived


```

## LVS+Nginx

### 核心

```
ipvs
```

### 区别

```
nginx 负责接受请求和响应请求
LVS 只接受请求但是不响应请求
所以 lvs的性能是nginx的几十倍
```



![image-20221207232816613](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221207232816613.png)

![image-20221207232838762](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221207232838762.png)



### 三种模式

#### ![image-20221207233705199](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221207233705199.png)

![image-20221207233453070](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221207233453070.png)

![image-20221207233827734](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221207233827734.png)





## LVS+Keepalived+nginx

![image-20221208171303516](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221208171303516.png)

![image-20221208202220725](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221208202220725.png)



### 虚拟ip网卡设置

![image-20221208171538855](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221208171538855.png)







## 示例

```nginx
worker_processes auto;
events {
    worker_connections  1024;
    accept_mutex on;
  }
http {
  include mime.types;
  default_type application/octet-stream;
  keepalive_timeout 120s;
  gzip on;
  gzip_min_length 8k;
  gzip_comp_level 4;
  client_max_body_size 1024m;
  client_header_buffer_size 32k;
  client_body_buffer_size 8m;
  server_names_hash_bucket_size 512;
  proxy_headers_hash_max_size 51200;
  proxy_headers_hash_bucket_size 6400;
  log_format main escape=json '{"remoteUser":"$remote_user","request":"$request","upstreamResponseTime":"$upstream_response_time","requestLength":"$request_length","httpUserAgent":"$http_user_agent","requestTime":"$request_time","httpHost":"$http_host","httpReferer":"$http_referer","remoteAddr":"$remote_addr","timeLocal":"$time_local","status":"$status","bodyBytesDent":"$body_bytes_sent"}';
  access_log /home/nginxWebUI/log/access.log main;
  proxy_connect_timeout 600s;
  proxy_read_timeout 600s;
  vhost_traffic_status_zone ;
  vhost_traffic_status_filter_by_host on;
  error_log /home/nginxWebUI/log/error.log;
  log_format escape=json '{"remoteUser":"$remote_user","request":"$request","upstreamResponseTime":"$upstream_response_time","requestLength":"$request_length","httpUserAgent":"$http_user_agent","requestTime":"$request_time","httpHost":"$http_host","httpReferer":"$http_referer","remoteAddr":"$remote_addr","timeLocal":"$time_local","status":"$status","bodyBytesDent":"$body_bytes_sent"}';
  server {
    server_name seaskysh.oicp.net;
    listen 80;
    location ~ ^/portainer/api/(.+)$  {
      proxy_pass http://192.168.1.80:9000/api/$1?$args;
      proxy_http_version 1.1;
      proxy_set_header Upgrade $http_upgrade;
      proxy_set_header Connection "upgrade";
    }
    location ~ "^/portainer(/?.*)" {
      proxy_pass http://192.168.1.80:9000$1$is_args$args;
      proxy_set_header Host $host:$server_port;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
    }
    location /projectplan/ {
      proxy_pass http://192.168.1.80:8080/projectplan/;
      proxy_set_header Host $host:$server_port;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
    }
    location /projectplanweb/index.html  {
      add_header Cache-Control "no-cache,no-store";
    }
    location ^~ /projectplanweb/ {
      try_files $uri /projectplanweb/index.html;
    }
  }
  
   server {
    listen 443 ssl;
    server_name www.opuxrcm.cn;
    root html;
        location ^~ / {
           try_files $uri /index.html;
        }

    index index.html index.htm;
    ssl_certificate 8950715_www.opuxrcm.cn.pem;
    ssl_certificate_key 8950715_www.opuxrcm.cn.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1.1 TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
        location /api/ {
             proxy_pass http://127.0.0.1:8081/;
             proxy_set_header Host $host:$server_port;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

