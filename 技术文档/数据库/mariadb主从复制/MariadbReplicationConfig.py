#!/usr/bin/python
# -*- coding:utf-8 -*-
"""
@Time : 2021/3/18 9:58
@Author : wxy
@contact: wxy18955007261@163.com
@file: MariadbReplicationConfig.py
@desc: 配置MariaDB主从复制
"""
import configparser
import os
import sys

'''
可以 改为从数据库读取配置,那样即可在页面回显了 
数据库配置字段 1.节点状态 2.主/备节点 3.对方ip 4.本机ip 5.数据库用户名 6.数据库密码 7.忽略同步的表 8.需要同步的表 9.需要同步的数据库
sys.argv[1] 1开启0关闭
sys.argv[2] 1主节点 0备节点
sys.argv[3] 对方真实ip地址
sys.argv[4] 本机ip地址
'''
is_run = str(sys.argv[1])
is_master = str(sys.argv[2])
real_ip = str(sys.argv[3])
local_ip = str(sys.argv[4])

# is_run = "1"
# is_master = "1"
# real_ip = "81.69.172.101"
# local_ip = "49.232.142.65"
'''
    配置主从复制
'''


def yum_install_mariadb(real_ip, is_master):
    config = configparser.ConfigParser()
    config.read('/etc/my.cnf.d/server.cnf', encoding='utf-8')
    config.set('mysqld', 'server-id', is_master)
    config.set('mysqld', 'character_set_server', "utf8mb4")
    config.set('mysqld', 'collation-server', "utf8mb4_unicode_ci")
    config.set('mysqld', 'init_connect', 'SET NAMES utf8mb4')
    config.set('mysqld', 'skip-character-set-client-handshake', "true")
    config.set('mysqld', 'log-bin', "mysql-bin")
    config.set('mysqld', 'relay-log', "relay-bin")
    config.set('mysqld', 'binlog-do-db', "lsblj")
    config.set('mysqld', 'replicate-ignore-table', "lsblj.lsblj_config") # 同步忽略表 需要可配
    config.set('mysqld', 'binlog-ignore-db', "information_schema,performance_schema,mysql")
    config.set('mysqld', 'slave_skip_errors', "1062")
    config.write(open("/etc/my.cnf.d/server.cnf", "w"))
    os.system('systemctl restart mariadb')
    os.system("sleep 1")
    os.system('mysql -e "stop slave"')
    os.system('mysql -e "reset slave"')
    os.system("sleep 1")
    cmd = "grant replication slave on *.* to 'lsblj'@" + "'" + real_ip + "'" + " identified by 'lsblj';"
    print('mysql -e "' + cmd + '"')
    os.system('mysql -e "' + cmd + '"')
    os.system('mysql -e "flush privileges"')


'''
    关闭mariadb主从复制
'''
def close_mariadb():
    os.system('mysql -e "stop slave"')
    os.system('mysql -e "reset slave"')


'''
    开启防火墙
'''
def start_iptables():
    os.system("iptables -D INPUT -s " + real_ip + " -p tcp --dport 3306 -j ACCEPT")
    os.system("service iptables save")


'''
    关闭防火墙
'''
def close_iptables():
    os.system("iptables -D INPUT -s " + real_ip + " -p tcp --dport 3306 -j ACCEPT")
    os.system("iptables -I INPUT -s " + real_ip + " -p tcp --dport 3306 -j ACCEPT")
    os.system("service iptables save")


if __name__ == '__main__':
    if is_master != '1':
        is_master = '2'
    # 0关闭
    if is_run == 0:
        # 开启防火墙规则
        start_iptables()
        # 关闭主从复制
        close_mariadb()
    else:
        # 关闭防火墙规则
        #close_iptables()
        # 开启主从复制
        yum_install_mariadb(real_ip, is_master)
