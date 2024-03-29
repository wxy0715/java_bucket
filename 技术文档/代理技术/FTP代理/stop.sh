#!/bin/bash

basepath=$(cd `dirname $0`; pwd)
CONFIG=$basepath/ftpproxy.conf
PID=$(cat $CONFIG.pid)
# 安全退出
kill -15 $PID
mv $CONFIG.pid $CONFIG.pid.$PID

# 检查进程是否被关掉，并删除pid文件,1分钟未关闭则kill -9
for((i=1;i<=30;i++));
do
   ps -fe|grep $PID |grep -v grep
   if [ $? -eq 0 ]; then
       if [ $i -eq 120 ]; then
           kill -9 $PID
       else
           sleep 2
       fi
   else
       break
   fi
done
rm -f $CONFIG.pid.$PID