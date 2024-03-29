> 作用 :可以根据关键字匹配,自动输入结果 比如(yes/no)

## 安装

**expect依赖tcl库**

安装包：永久有效

链接：https://pan.baidu.com/s/1V8868ubzCXiLFCPGocgGtw 
提取码：2jbr

链接：https://pan.baidu.com/s/1zVIIpDZ6iplu6_s1yzaJxQ 
提取码：63g1

```sh
我将两个压缩包传到了/opt/目录下
1.安装tcl
tar xf tcl8.4.11-src.tar.gz
cd tcl8.4.11/unix
./configure
make && make install

2.安装expect
tar xf expect-5.43.0.tar.gz
cd expect-5.43.0
##### 注意:--with-tclinclude是自己安装tcl的目录
./configure --with-tclinclude=/opt/tcl8.4.11/generic --with-tclconfig=/usr/local/lib/
 make && make install
```

## 创建软链接

ln -s /opt/expect-5.43/expect   /usr/bin/expect

##  测试用例 expect结合scp

**创建文件:vim test.sh**

```sh
#!/usr/bin/expect
set timeout 10
set username [lindex $argv 0]
set password [lindex $argv 1]
set ip [lindex $argv 2]
set port [lindex $argv 3]
set sourcePath [lindex $argv 4]
set targetPath [lindex $argv 5]
spawn scp -P$port $sourcePath  $username@$ip:$targetPath
expect {
"Are you sure you want to continue connecting (yes/no)?" { send "yes\n"; exp_continue }
"$username@$ip's password:" { send "$password\n" }
}
expect eof
```

**赋予执行权限:chmod +x test.sh**

> 不要sh test.sh 这个是expect脚本 

**执行: ./test.sh root root 172.132.312.11 22 /opt/test.sh /opt** 

