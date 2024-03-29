#!/bin/bash

##################################################################################执行脚本前的操作##################################################
#### 1.该脚本上传到/opt  
#### 2.执行命令 cd /opt;sh system_upgrade.sh
#### 3.结束后重新xshell 端口为12288不是22了

if [[ "$(whoami)" != "root" ]]; then
	echo "please run this script as root ." >&2
	exit 1
fi

# dns
dns(){
  cat >> /etc/resolv.conf << EOF
nameserver 8.8.8.8
nameserver 114.114.114.114
EOF
}

#yum update
yum_update(){
  cd /etc/yum.repos.d/ && mkdir bak && mv -f *.repo bak/
  wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
  wget -O /etc/yum.repos.d/epel.repo http://mirrors.aliyun.com/repo/epel-7.repo
  yum -y update
}

#configure yum source
yum_config(){
  yum -y install wget epel-release
  yum clean all && yum makecache
  # 安装自动补全
  yum install -y bash-completion bash-completion-extras tree screen nmap nc dos2unix tcpdump lsof 
  yum -y install iotop iftop net-tools kernel-devel lrzsz gcc gcc-c++ make cmake libxml2-devel openssl-devel curl curl-devel unzip sudo ntp libaio-devel vim ncurses-devel autoconf automake zlib-devel python-devel bash-completion python-pip git-core telnet
  yum -y install  bzip2-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel
  echo -e "\033[31mconfigure yum source successful...\033[0m"
  # 安装ftp
  yum -y install vsftpd
  service vsftpd start
  systemctl enable vsftpd
  systemctl restart vsftpd
  echo -e "\033[31mftp successful...\033[0m"
}

#system config
system_config(){
  sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
  setenforce 0
  timedatectl set-local-rtc 1 && timedatectl set-timezone Asia/Shanghai
  yum -y install chrony && systemctl start chronyd.service && systemctl enable chronyd.service
  echo -e "\033[31mchronyd successful...\033[0m"
}

ulimit_config(){
  echo "ulimit -SHn 102400" >> /etc/rc.local
  cat >> /etc/security/limits.conf << EOF
*           soft   nofile       102400
*           hard   nofile       102400
*           soft   nproc        unlimited
*           hard   nproc        unlimited
*           soft  memlock      unlimited
*           hard  memlock      unlimited
EOF
}

#set sysctl
sysctl_config(){
  cp /etc/sysctl.conf /etc/sysctl.conf.bak
  cat > /etc/sysctl.conf << EOF
net.ipv4.ip_forward = 0
net.ipv4.conf.default.rp_filter = 1
net.ipv4.conf.default.accept_source_route = 0
kernel.sysrq = 0
kernel.core_uses_pid = 1
net.ipv4.tcp_syncookies = 1
kernel.msgmnb = 65536
kernel.msgmax = 65536
kernel.shmmax = 68719476736
kernel.shmall = 4294967296
# 表示系统同时保持TIME_WAIT套接字的最大数量，如果超过这个数字，TIME_WAIT套接字将立刻被清除并打印警告信息。
net.ipv4.tcp_max_tw_buckets = 6000
net.ipv4.tcp_sack = 1
net.ipv4.tcp_window_scaling = 1
net.ipv4.tcp_rmem = 4096 87380 4194304
net.ipv4.tcp_wmem = 4096 16384 4194304
net.core.wmem_default = 8388608
net.core.rmem_default = 8388608
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216
net.core.netdev_max_backlog = 262144
net.ipv4.tcp_max_orphans = 3276800
# 表示SYN队列的长度，默认为1024，加大队列长度为262144，可以容纳更多等待连接的网络连接数。
net.ipv4.tcp_max_syn_backlog = 262144
net.ipv4.tcp_timestamps = 0
net.ipv4.tcp_synack_retries = 1
# 表示开启TCP连接中TIME-WAIT sockets的快速回收，默认为0，表示关闭。
net.ipv4.tcp_syn_retries = 1
net.ipv4.tcp_tw_recycle = 1
# 表示开启重用。允许将TIME-WAIT sockets重新用于新的TCP连接，默认为0，表示关闭；
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_mem = 94500000 915000000 927000000
# 修改系默认的 TIMEOUT 时间
net.ipv4.tcp_fin_timeout = 1
net.ipv4.tcp_keepalive_time = 30
# 表示当keepalive起用的时候，TCP发送keepalive消息的频度。缺省是2小时，改为20分钟。
net.ipv4.ip_local_port_range = 1024 65000
EOF
  /sbin/sysctl -p
  echo -e "\033[31msysctl successful...\033[0m"
}

#iptables
iptables_install(){
  systemctl stop firewalld.service
  systemctl mask firewalld.service
  systemctl disable firewalld.service
  yum -y install iptables-services 
  systemctl enable iptables
  systemctl start iptables
  iptables -F
  service iptables save
  echo -e "\033[31miptables successful...\033[0m"
  update_port
}

# update ssh port
update_port() {
  sed -i "s/#Port 22/Port 12288/g" /etc/ssh/sshd_config
  systemctl restart sshd
  iptables -I INPUT -p tcp --dport 12288 -j ACCEPT
  # logstash
  iptables -I INPUT -p tcp --dport 9600 -j ACCEPT
  # logstash-tcp模式
  iptables -I INPUT -p tcp --dport 5000 -j ACCEPT
  # filebeat
  iptables -I INPUT -p tcp --dport 5044 -j ACCEPT
  # elasticsearch
  iptables -I INPUT -p tcp --dport 9200 -j ACCEPT
  # kibana
  iptables -I INPUT -p tcp --dport 5601 -j ACCEPT
  # mariadb
  iptables -I INPUT -p tcp --dport 3306 -j ACCEPT
  # docker portainer
  iptables -I INPUT -p tcp --dport 9000 -j ACCEPT
  # nginx
  iptables -I INPUT -p tcp --dport 80 -j ACCEPT
  # nginxWebUI
  iptables -I INPUT -p tcp --dport 8079 -j ACCEPT
  # tomcat
  iptables -I INPUT -p tcp --dport 8080 -j ACCEPT
  # redis
  iptables -I INPUT -p tcp --dport 6379 -j ACCEPT
  # zookeeper
  iptables -I INPUT -p tcp --dport 2181 -j ACCEPT
  # kafka
  iptables -I INPUT -p tcp --dport 9092 -j ACCEPT
  # rocketmq
  iptables -I INPUT -p tcp --dport 9876 -j ACCEPT
  iptables -I INPUT -p tcp --dport 17890 -j ACCEPT
  # rabbitmq
  iptables -I INPUT -p tcp --dport 15672 -j ACCEPT
  # nacos
  iptables -I INPUT -p tcp --dport 8848 -j ACCEPT
  # mongodb
  iptables -I INPUT -p tcp --dport 27017 -j ACCEPT
  # ftp
  iptables -I INPUT -p tcp --dport 21 -j ACCEPT
  iptables -I INPUT -p tcp --dport 20 -j ACCEPT
  iptables -I INPUT -p tcp --dport 23 -j ACCEPT
  service iptables save
  iptables -nvL
  echo -e "\033[31m ssh port successful...\033[0m"
}

# install java8
install_jdk() {
  yum -y install java-1.8.0-openjdk*
  echo -e "\033[31m java8 successful...\033[0m"
}

# install_git
install_git() {
  yum install -y git
  echo -e "\033[31m install_git successful...\033[0m"
}

#install docker
install_docker() {
 yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
	yum -y install yum-utils device-mapper-persistent-data lvm2
  yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
	yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
	yum-config-manager --enable docker-ce-edge
	yum-config-manager --enable docker-ce-test
	yum-config-manager --disable docker-ce-edge
	yum -y install docker-ce
	yes | cp -rf /root/init_script/daemon.json /etc/docker
	systemctl start docker
	systemctl enable docker
  echo -e "\033[31mdocker successful...\033[0m"
  # 允许转发
  echo "net.ipv4.ip_forward=1" >>/usr/lib/sysctl.d/00-system.conf
  systemctl restart network && systemctl restart docker
}

#install_docker_compose
install_docker_compose() {
	curl -L "https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
  # 创建软链
  sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
	chmod +x /usr/local/bin/docker-compose 
	docker-compose --version
	echo -e "\033[31m install_docker_compace successful...\033[0m"
}

#install_docker_portainer
install_docker_portainer() {
  sudo docker pull portainer/portainer
  docker run -d -p 9000:9000 --name portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v /mnt/docker/portainer:/data portainer/portainer
  docker restart portainer
}

# Docker容器性能监控工具google/cadvisor
install_docker_cadvisor() {
  docker pull google/cadvisor
  # 运行容器简写方式
  docker run -v /:/rootfs:ro -v /var/run:/var/run:rw -v /sys:/sys:ro -v /var/lib/docker/:/var/lib/docker:ro -p 8080:8080 --detach=true --privileged=true --name=cadvisor --restart=always google/cadvisor:latest
}

#install_nginx
install_nginx() {
  #install_nginx
  #nginx path prefix: "/opt/install/nginx"
  #nginx binary file: "/opt/install/nginx/sbin/nginx"
  #nginx modules path: "/opt/install/nginx/modules"
  #nginx configuration prefix: "/opt/install/nginx/conf"
  #nginx configuration file: "/opt/install/nginx/conf/nginx.conf"
  #nginx pid file: "/opt/install/nginx-1.18.0/nginx.pid"
  #nginx error log file: "/opt/install/nginx/logs/error.log"
  #nginx http access log file: "/opt/install/nginx/logs/access.log"
  #nginx http client request body temporary files: "client_body_temp"
  #nginx http proxy temporary files: "proxy_temp"
  #nginx http fastcgi temporary files: "fastcgi_temp"
  #nginx http uwsgi temporary files: "uwsgi_temp"
  #nginx http scgi temporary files: "scgi_temp"
  nginx_version="1.18.0"
	cd /opt/software
	wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/nginx-${nginx_version}.tar.gz
	tar -zxvf nginx-${nginx_version}.tar.gz -C /opt/install 
	cd /opt/install/nginx-${nginx_version}
	./configure --prefix=/opt/install/nginx --with-http_stub_status_module --with-http_ssl_module --pid-path=/opt/install/nginx-${nginx_version}/nginx.pid
	make && make install
  /opt/install/nginx/sbin/nginx
	echo -e "\033[31m install_nginx successful...\033[0m"
}

# install nginx管理系统
install_nginxWebUI(){
  cd /opt/install
  wget  http://file.nginxwebui.cn/nginxWebUI-3.3.3.jar
  nohup java -jar -Dfile.encoding=UTF-8 /opt/install/nginxWebUI-3.3.3.jar --server.port=8079 --project.home=/opt/install/nginxWebUI > /dev/null &
}

# install tomcat
install_tomcat() {
  tomcat_version="9.0.54"
	cd /opt/software
	wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/apache-tomcat-${tomcat_version}.tar.gz
  tar -zxvf apache-tomcat-${tomcat_version}.tar.gz -C /opt/install 
  mv /opt/install/apache-tomcat-${tomcat_version} /opt/install/tomcat
  /opt/install/tomcat/bin/startup.sh
  echo -e "\033[31m tomcat successful...\033[0m"
}

# install_redis
install_redis() {
  redis_version="4.0.10"
  # 下载
	cd /opt/software
	wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/redis-${redis_version}.tar.gz
  tar -zxvf redis-${redis_version}.tar.gz -C /opt/install 
  cd /opt/install/redis-${redis_version}
  # 安装
  make
  make PREFIX=/opt/install/redis install
  mkdir /opt/install/redis/etc/
  cp redis.conf /opt/install/redis/etc/
  cd /opt/install/redis/bin/
  cp redis-benchmark redis-cli redis-server /usr/bin/
  # 更改配置
  sed -i "s/daemonize no/daemonize yes/g" /opt/install/redis/etc/redis.conf
  /opt/install/redis/bin/redis-server /opt/install/redis/etc/redis.conf
  echo -e "\033[31m install_redis successful...\033[0m"
}

#install_mariadb
install_mariadb() {
  mariadb_version="10.4"
  # 卸载mariadb 删除配置文件,删除数据目录
  yum remove -y mariadb* && rm -f /etc/my.cnf && rm -rf /var/lib/mysql/
  > /etc/yum.repos.d/Mariadb.repo
  cat >> /etc/yum.repos.d/Mariadb.repo << EOF
[mariadb]
name = MariaDB
baseurl = https://mirrors.ustc.edu.cn/mariadb/yum/${mariadb_version}/centos7-amd64
gpgkey=https://mirrors.ustc.edu.cn/mariadb/yum/RPM-GPG-KEY-MariaDB
gpgcheck=1
EOF
  # 刷新yum缓存 并安装
  yum clean all &&  yum makecache &&  yum -y install MariaDB-server MariaDB-client
  # 启动,自启
  systemctl enable mariadb.service && systemctl start mariadb.service
  echo -e "\nY\nY\nroot\nroot\ny\nn\ny\nn\ny\ny\n" | mysql_secure_installation
  echo -e "\033[31m install_mariadb successful...\033[0m"
  mysql -e 'create user "wxy" identified by "wxy";'
  # 适配高版本
  mysql -e 'grant all privileges on *.* to "wxy"@"%" with grant option;'
  # 适配低版本
  mysql -e 'grant all privileges on *.* to "root"@"%"; on *.* to "wxy"@"%" identified  by "wxy";'
  mysql -e 'grant all privileges on *.* to "wxy"@"localhost" identified  by "wxy";'
  mysql -e 'flush privileges;'
}

# 安装es+ik
install_es() {
  es_version="7.13.3"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/ELK/elasticsearch-${es_version}.tar.gz
  tar -zxvf elasticsearch-${es_version}.tar.gz -C /opt/install
  # 创建es用户
  adduser es
  echo -e "es\nes\n" | passwd es
  chown -R es:es /opt/install/elasticsearch-${es_version}/
  chmod 775 /opt/install/elasticsearch-${es_version}/
  # 修改系统配置
  sysctl -w vm.max_map_count=655360
  sysctl -p
  # 启动命令,暂时不启动,需要切换用户
  # /opt/install/elasticsearch-${es_version}/bin/elasticsearch -d
  echo -e "\033[31m install_es successful...\033[0m"
}

# 安装filebeat
install_filebeat(){
  version="7.13.3"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/ELK/filebeat-${version}.tar.gz
  tar -zxvf filebeat-${version}.tar.gz -C /opt/install
  cd /opt/install/filebeat-${version}
  nohup ./filebeat &
}

# 安装logstash
install_logstash(){
  version="7.13.3"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/ELK/logstash-${version}.tar.gz
  tar -zxvf logstash-${version}.tar.gz -C /opt/install
  # 启动
  cd /opt/install/logstash-${version}
  nohup bin/logstash -f config/logstash-sample.conf &
}

# 安装kibana
install_kibana(){
  version="7.13.3"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/ELK/kibana-${version}-linux.tar.gz
  tar -zxvf kibana-${version}-linux.tar.gz -C /opt/install
  # 启动
  cd /opt/install/kibana-${version}-linux
  nohup bin/kibana &
}

# ELK搭建
install_elk(){
  version="7.13.3"
  install_es
  install_filebeat
  install_logstash
  install_kibana
  echo -e "\033[31m install_elk successful...\033[0m"
}

# 安装zookeeper
install_zookeeper(){
  zookeeper_version="3.5.7"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/apache-zookeeper-${zookeeper_version}-bin.tar.gz
  tar -zxvf apache-zookeeper-${zookeeper_version}-bin.tar.gz -C /opt/install
  cp /opt/install/apache-zookeeper-${zookeeper_version}-bin/conf/zoo_sample.cfg /opt/install/apache-zookeeper-${zookeeper_version}-bin/conf/zoo.cfg
  # 修改配置
  cat > /opt/install/apache-zookeeper-${zookeeper_version}-bin/conf/zoo.cfg << EOF
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/opt/install/apache-zookeeper-${zookeeper_version}-bin/zkData
clientPort=2181
4lw.commands.whitelist=*
admin.serverPort=8071
quorumListenOnAllIPs=true
EOF
  # 启动
  sh /opt/install/apache-zookeeper-${zookeeper_version}-bin/bin/zkServer.sh start
  echo -e "\033[31m install_zookeeper successful...\033[0m"
}

# 安装kafka  依赖zookeeper jdk 
install_kafka(){
  kafka_version="2.11-2.4.0"
  # 由于单机,暂时忽略集群配置
  cd /opt/software/
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/kafka_${kafka_version}.tgz
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/sbt-1.5.5.tgz
  tar zxvf kafka_${kafka_version}.tgz -C /opt/install/
  tar -zxvf sbt-1.5.5.tgz -C /opt/install/
  cd /opt/install/kafka_${kafka_version}/bin
  mkdir ../logs
  touch ../logs/server-start.log
  # 启动
  #nohup sh kafka-server-start.sh ../config/server.properties > ../logs/server-start.log 2>&1 &
  #sh kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 0 --partitions 0 --topic test
  #sh kafka-topics.sh --list --zookeeper localhost:2181
  # 发送消息 bin/kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic test
  # 接收消息 bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic test --from-beginning
  echo -e "\033[31m install_kafka successful...\033[0m"
}

# 安装rabbitmq  运行目录:/usr/lib/rabbitmq 配置:/etc/rabbitmq 端口15672
install_rabbitmq(){
  mkdir -p /opt/software/RabbitMq/
  cd /opt/software/RabbitMq/
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/RabbitMq/erlang-23.1.1-1.el6.x86_64.rpm
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/RabbitMq/rabbitmq-server-3.8.9-1.el7.noarch.rpm
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/RabbitMq/socat-1.7.3.2-1.1.el7.x86_64.rpm
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/RabbitMq/socat-1.7.3.2-5.el7.lux.x86_64.rpm
  rpm -ivh erlang-23.1.1-1.el6.x86_64.rpm
  rpm -ivh socat-1.7.3.2-1.1.el7.x86_64.rpm
  rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm
  rpm -ivh rabbitmq-server-3.8.9-1.el7.noarch.rpm
  chown -R rabbitmq:rabbitmq /var/lib/rabbitmq/mnesia/
  echo $(whereis rabbitmq)
  # 启动后台管理插件
  /usr/lib/rabbitmq/bin/rabbitmq-plugins enable rabbitmq_management
  # 启动RabbitMQ
  systemctl start rabbitmq-server
  systemctl status rabbitmq-server
  # 添加用户
  /usr/lib/rabbitmq/bin/rabbitmqctl add_user mq mq
  # 设置权限
  /usr/lib/rabbitmq/bin/rabbitmqctl set_user_tags mq administrator
}

# 安装rocketmq  页面:ip:17890
install_rocketmq(){
  rocketmq_version="4.5.1"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/rocketmq-all-${rocketmq_version}-bin-release.zip
  unzip rocketmq-all-${rocketmq_version}-bin-release.zip -d /opt/install/
  cd /opt/install/rocketmq-all-${rocketmq_version}-bin-release/bin
  # 修改运行脚本配置 不然jvm参数过大 启动失败
  sed -i "s/-server -Xms4g -Xmx4g -Xmn2g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m/-server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m/g" /opt/install/rocketmq-all-4.5.1-bin-release/bin/runserver.sh
  sed -i "s/-server -Xms8g -Xmx8g -Xmn4g/-server -Xms256m -Xmx256m -Xmn128m/g" /opt/install/rocketmq-all-4.5.1-bin-release/bin/runbroker.sh
  sed -i "s/-server -Xms1g -Xmx1g -Xmn256m -XX:PermSize=128m -XX:MaxPermSize=128m/-server -Xms128m -Xmx128m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=128m/g" /opt/install/rocketmq-all-4.5.1-bin-release/bin/tools.sh
  # 启动
  nohup sh mqnamesrv &
  echo $(echo -e "\n" ) 
  echo $(echo -e "\n" ) 
  nohup sh mqbroker -n localhost:9876 &
  echo $(echo -e "\n" ) 
  echo $(echo -e "\n" ) 
  # 测试生产者
  #export NAMESRV_ADDR=localhost:9876
  #sh tools.sh org.apache.rocketmq.example.quickstart.Producer
  # 测试消费者
  #sh tools.sh org.apache.rocketmq.example.quickstart.Consumer
  # 停止
  #sh mqshutdown broker
  #sh mqshutdown namesrv

  # 启动管理控制台 端口17890
  cd /opt/software
  wget https://edu-springboot.oss-cn-beijing.aliyuncs.com/%E5%AE%89%E8%A3%85%E5%8C%85/rocketmq-console-ng-1.0.1.jar
  nohup java -jar /opt/software/rocketmq-console-ng-1.0.1.jar &
  echo $(echo -e "\n" ) 
  echo $(echo -e "\n" )
  echo -e "\033[31m install_rocketmq successful...\033[0m"
}

# 安装病毒扫描ClamAV
install_clamAV(){
  yum -y install clamav-server clamav-data clamav-update clamav-filesystem clamav clamav-scanner-systemd clamav-devel clamav-lib clamav-server-systemd 
  # 配置ClamAV权限
  setsebool -P antivirus_can_scan_system 1 
  setsebool -P clamd_use_jit 1 
  # 查看设置结果
  getsebool -a | grep antivirus 
  # 删除示例
  sed -i -e "s/^Example/#Example/" /etc/clamd.d/scan.conf 
  sed -i -e "s/^Example/#Example/" /etc/freshclam.conf 
  # 修改配置
  sed -i "s/#LocalSocket/LocalSocket/g" /etc/clamd.d/scan.conf 
  # 更新病毒库  病毒库保存位置:/var/lib/clamav/daily.cvd /var/lib/clamav/main.cvd  
  freshclam
  # 启动 ,暂时不启动
  #sudo systemctl start clamd@scan
  #sudo systemctl enable clamd@scan
  echo -e "\033[31m install_clamAV successful...\033[0m"
}

# 安装python3.6,这个暂时不需要,应该已经安装了3.7
install_python(){
  cd /opt/software
  wget https://www.python.org/ftp/python/3.6.1/Python-3.6.1.tgz
  tar -zxvf Python-3.6.1.tgz -C /usr/local
  mv /usr/local/Python-3.6.1 /usr/local/python3
  cd /usr/local/python3
  ./configure 
  make && make install
  ln -s /usr/local/bin/python3 /usr/bin/python3
  ln -s /usr/local/bin/pip3 /usr/bin/pip3
  python3 -m pip install --upgrade pip
  pip3 install pymysql
  echo -e "\033[31m update_python successful...\033[0m"
}

# 安装mongodb
install_mongodb(){
  mongodb_version="4.0.8"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/mongodb-linux-x86_64-${mongodb_version}.tgz
  tar zxvf mongodb-linux-x86_64-${mongodb_version}.tgz
  mv mongodb-linux-x86_64-${mongodb_version} /opt/install/mongodb4
  cd /opt/install/mongodb4
  mkdir -p data/db
  mkdir logs
  cd /opt/install/mongodb4/bin
  cat > mongodb.conf << EOF
dbpath = /opt/install/mongodb4/data/db  # Mongodb数据库存放位置
logpath = /opt/install/mongodb4/logs/mongodb.log  # 日志
logappend = true  # 日志追加级别，表示不覆盖日志，而是往后面添加
port = 27017   # mongodb 端口
fork = true    # 以守护进程的方式在后台运行
bind_ip=0.0.0.0  #可 访问的IP地址， 0.0.0.0表示IP都可以被访问
EOF

  cat >> /etc/profile << EOF
export PATH=$PATH:/opt/install/mongodb4/bin
EOF

  source /etc/profile
  # 启动
  mongod -f mongodb.conf 
}

# 安装nacos
install_nacos(){
  nacos_version="2.0.3"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/nacos-server-${nacos_version}.tar.gz
  tar -zxvf nacos-server-${nacos_version}.tar.gz -C /opt/install/
  /opt/install/nacos/bin/startup.sh -m standalone
}

# 安装keepalived
install_keepalived(){
  keepalived_version="2.0.18"
  cd /opt/software
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/keepalived-${keepalived_version}.tar.gz
  wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/software/keepalived.conf
  tar -zxvf keepalived-${keepalived_version}.tar.gz -C /opt/install/
  cd /opt/install/keepalived-${keepalived_version}/
  ./configure --prefix=/usr/local/keepalived --sysconf=/etc
  make && make install
  whereis keepalived
  cd keepalived/etc/
  \cp -f init.d/keepalived /etc/init.d/
  \cp -f sysconfig/keepalived /etc/sysconfig/
  systemctl restart keepalived
  systemctl status keepalived
}

main(){
  mkdir /opt/software
  mkdir /opt/install
  # dns # 阿里云服务器无需配置
  yum_update
  yum_config
  system_config
  sysctl_config
  ulimit_config
  iptables_install
  install_jdk
  install_git
  install_docker
  install_docker_compose
  install_docker_portainer
  # Docker容器性能监控工具google/cadvisor
  install_docker_cadvisor
  install_nginx
  install_nginxWebUI
  install_tomcat
  install_redis
  install_mariadb
  #install_es #elk中包含了es,不需要单独安装
  install_elk
  install_zookeeper
  install_clamAV
  install_kafka
  install_rabbitmq
  install_rocketmq
  install_mongodb
  install_nacos
  install_keepalived
}
main

