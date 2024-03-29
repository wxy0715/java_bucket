#!/usr/bin/python
# -*- coding:utf-8 -*-
"""
@Time : 2021/3/18 9:58
@Author : wxy
@contact: wxy18955007261@163.com
@file: MariadbReplicationStart.py
@desc: 启动
"""

import os
import sys

log_file = str(sys.argv[1])
log_pos = str(sys.argv[2])
# 对方IP
real_ip = str(sys.argv[3])


if __name__ == '__main__':
    cmd1 = "change master to master_host= '"+real_ip+"' , master_user='lsblj', master_password='lsblj', master_log_file='"+log_file+"', master_log_pos="+log_pos
    os.system('mysql -e "' + cmd1 + '"')
    print('mysql -e "' + cmd1 + '"')
    os.system('mysql -e "start slave"')
    os.system("sleep 1")
    os.system('mysql -e "show slave status\G"')
