#!/bin/bash
param=$(mysql -e "show slave status\G" | grep Slave_IO_Running |awk -F ":" '{print $2}')
param1=$(mysql -e "show slave status\G" | grep Slave_SQL_Running |awk -F ":" '{print $2}' |awk "NR==1")
if [ $param1 = "Yes" ] && [ $param = "Yes" ]; then
  echo "true"
  # è·å–å¯¹æ–¹æœåŠ¡å™¨IP
  host=$(mysql -e "show slave status\G" | grep Master_Host |awk -F ":" '{print $2}')
  # è·å–ä»èŠ‚ç‚¹è¿˜æ˜¯ä¸»èŠ‚ç‚¹ 1ä¸»èŠ‚ç‚¹2ä»èŠ‚ç‚¹
  master=$(cat /etc/my.cnf.d/server.cnf  | grep server-id |awk -F "=" '{print $2}')
  echo $host
  echo $master
else
  echo "false"
fi

<<<<<<< HEAD
=======
# ÅĞ¶ÏÖ÷»ú±¸»ú
master=$(cat /etc/my.cnf.d/server.cnf  |grep server-id |awk -F "=" '{print $2}')
echo $master
# »ñÈ¡host
host=$(mysql -e "show slave status\G" |grep Master_Host |awk -F ":" '{print $2}')
echo $host

# »ñÈ¡¸¡¶¯Ip
virtual_ipaddress=$(cat /etc/keepalived/keepalived.conf |grep virtual_server |awk '{print $2}')
echo $virtual_ipaddress
>>>>>>> ee8f80b36004144f37b8423c440fa70ce1ada44a
