#!/bin/bash
# 获取主从复制的log_file mysql-bin.000004
mysql -e "show master status" |awk "NR==2"|awk '{print $1}'
