#!/bin/bash
# 获取主从复制的log_pos 887
mysql -e "show master status" |awk "NR==2"|awk '{print $2}'
