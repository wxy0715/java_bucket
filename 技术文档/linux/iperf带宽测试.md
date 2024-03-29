# 网络性能测试

## 手动安装iperf

```sh
# 源码部署
上传iperf-3.1.3-source.tar.gz包
tar xzvf iperf-3.1.3-source.tar.gz
cd iperf-3.1.3/
./configure
make && make install
```

## 使用方法

```sh
服务端: iperf -s -D(后台守护线程启动)
客户端:iperf -c 101.43.60.8 -p5002 -t 10 -d -n2000000 -o /opt/iperf.txt
-c 指定服务器网卡Ip
-p端口
-t 时间
-0 输出文件
-d 双向测试
-n 指定字节数
```

