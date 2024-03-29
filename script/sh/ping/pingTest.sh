#!/bin/bash

if [ "$1" = "start" ];then
  nohup ping -c 100 $2 > /opt/sgg/cacheFile/pingTest 2>&1 &
elif [ "$1" = "stop" ];then
  for i in $(ps -ef | grep -v grep |grep -v pingTest |grep ping| awk '{print $2}')
  do
    kill -9 $i
  done
  cat /dev/null > /opt/sgg/cacheFile/pingTest
fi
