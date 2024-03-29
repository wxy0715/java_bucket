#!/bin/bash

if [ $# -eq 0 ]
then
    echo "No arguments supplied"
    exit
fi

basepath=$(cd `dirname $0`; pwd)
cd $basepath
CONFIG=$basepath/$1

# test
# CONFIG=$basepath/ftpproxy.conf

# 堆空间大小配置，为了避免动态堆大小影响效率，请将初始大小和最大大小配置相同
HEAP_OPTS="-Xms512m -Xmx512m"
# 垃圾回收配置
JVM_PERFORMANCE_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=30 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ExplicitGCInvokesConcurrent -XX:MaxInlineLevel=15"

JAVA_OPTS="${HEAP_OPTS} ${JVM_PERFORMANCE_OPTS}"

# 启动
nohup java ${JAVA_OPTS} -jar ftp-proxy-1.0.jar $CONFIG >/dev/null 2>&1 &
echo $! > $CONFIG.pid
