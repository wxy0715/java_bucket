#!/usr/bin/python
# -*- coding:utf-8 -*-
"""
@Time : 2021/10/28 19:09
@Author : wxy
@contact: wxy18955007261@163.com
@file: windowsPassword.py
@desc:  
"""
import ctypes
import socket
import subprocess
import sys

from toml.decoder import unicode


def is_admin():
    try:
        return ctypes.windll.shell32.IsUserAnAdmin()
    except:
        return False


if __name__ == '__main__':
    if is_admin():
        # 杀死之前的进程
        # try:
        #     subprocess.Popen("taskkill /f /t /im windowsPassword.exe", shell=True)
        # except Exception as e:
        #     print("该进程第一次启动!")
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind(('0.0.0.0', 44444))
        server.listen(5)  # 开始监听TCP传入连接
        while True:
            conn, addr = server.accept()
            # 接受用户名和密码
            client_msg = conn.recv(1024).decode()
            user = client_msg.split('|')[0]
            pwd = client_msg.split('|')[1]
            cmd = "net user " + user + " " + pwd
            print(cmd)
            try:
                # shell=False的时候cmd_list是一个列表，shell=True的时候cmd_list是一个字符串
                msg = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                stderr = str(msg.stderr.read().decode("gbk")).strip()
                stdout = str(msg.stdout.read().decode("gbk")).strip()
                if "" != stderr:
                    print(stderr)
                    conn.send(stderr.encode('utf-8')+"\n".encode('utf-8'))
                    conn.send("exit".encode('utf-8') + "\n".encode('utf-8'))
                if stdout.find("成功") > -1:
                    print(stdout+"\n")
                    conn.send(stdout.encode('utf-8')+"\n".encode('utf-8'))
                    conn.send("exit".encode('utf-8') + "\n".encode('utf-8'))
                else:
                    print(stdout)
                    conn.send(stdout.encode('utf-8')+"\n".encode('utf-8'))
                    conn.send("exit".encode('utf-8') + "\n".encode('utf-8'))
            except Exception as e:
                conn.send("异常".encode("utf-8")+"\n".encode('utf-8'))
                conn.send("exit".encode('utf-8') + "\n".encode('utf-8'))
    else:
        if sys.version_info[0] == 3:
            ctypes.windll.shell32.ShellExecuteW(None, "runas", sys.executable, __file__, None, 1)
        else:  # in python2.x
            ctypes.windll.shell32.ShellExecuteW(None, u"runas", unicode(sys.executable), unicode(__file__), None, 1)