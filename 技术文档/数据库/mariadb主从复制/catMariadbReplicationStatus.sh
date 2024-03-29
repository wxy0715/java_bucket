#!/bin/bash
param=$(mysql -e "show slave status\G" | grep Slave_IO_Running |awk -F ":" '{print $2}')
param1=$(mysql -e "show slave status\G" | grep Slave_SQL_Running |awk -F ":" '{print $2}' |awk "NR==1")
if [ $param1 = "Yes" ] && [ $param = "Yes" ]; then
  echo "true"
  # 获取对方服务器IP
  host=$(mysql -e "show slave status\G" | grep Master_Host |awk -F ":" '{print $2}')
  # 获取从节点还是主节点 1主节点2从节点
  master=$(cat /etc/my.cnf.d/server.cnf  | grep server-id |awk -F "=" '{print $2}')
  echo $host
  echo $master
else
  echo "false"
fi

<<<<<<< HEAD
=======
# �ж���������
master=$(cat /etc/my.cnf.d/server.cnf  |grep server-id |awk -F "=" '{print $2}')
echo $master
# ��ȡhost
host=$(mysql -e "show slave status\G" |grep Master_Host |awk -F ":" '{print $2}')
echo $host

# ��ȡ����Ip
virtual_ipaddress=$(cat /etc/keepalived/keepalived.conf |grep virtual_server |awk '{print $2}')
echo $virtual_ipaddress
>>>>>>> ee8f80b36004144f37b8423c440fa70ce1ada44a
