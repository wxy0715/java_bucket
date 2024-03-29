# linux安装python3.6步骤

## 1. 安装依赖环境

yum -y install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel

## 2.下载Python3
<https://www.python.org/downloads/>

```
`# wget https://www.python.org/ftp/python/3.6.1/Python-3.6.1.tgz`
```

## 3.安装python3

我个人习惯安装在/usr/local/python3（具体安装位置看个人喜好）
创建目录：

```
`# mkdir -p /usr/local/python3`
```

解压下载好的Python-3.x.x.tgz包(具体包名因你下载的Python具体版本不不同⽽而不不同，如：我下载的是Python3.6.1.那我这里就是Python-3.6.1.tgz)

```
`# tar -zxvf Python-3.6.1.tgz`
```

## 4.进入解压后的目录，编译安装。

```
`# cd Python-3.6.1``# ./configure --prefix=/usr/local/python3`
```

报错:configure: error: no acceptable C compiler found in $PATH 问题解决 

解决办法: yum -y install gcc

```
`# make`
```

## 建立软链接

```
ln -s /usr/local/python3/bin/python3 /usr/bin/python3
ln -s /usr/local/python3/bin/pip3 /usr/bin/pip3
升级pip: python3 -m pip install --upgrade pip
```



## 测试

```
pip3 install pymysql
```

