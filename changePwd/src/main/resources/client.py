#!/usr/bin/python
# -*- coding:utf-8 -*-
"""
@Time : 2021/10/28 19:09
@Author : wxy
@contact: wxy18955007261@163.com
@file: client.py
@desc:  
"""
import socket

if __name__ == '__main__':
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect(('127.0.0.1', 44444))
    user = "test"
    password = "qqwang0715.."
    print("密码为:"+password)
    msg = user+'|'+password
    client.send(msg.encode('utf-8'))
    back_msg = client.recv(1024).decode().encode("utf-8").decode()
    print('changed passwd result: %s' % back_msg)
    pos = back_msg.find('\r')
    if pos > -1:
        back_msg = back_msg[0:pos]
    if back_msg.find('成功')>-1:
        print("密码修改成功！")
    else:
        print("密码修改失败！")
    client.close()
