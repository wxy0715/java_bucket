# 借鉴:朱双映的博客

# 安装

```shell
#配置好yum源以后安装iptables-service
# yum install -y iptables-services
#停止firewalld
# systemctl stop firewalld
#禁止firewalld自动启动
# systemctl disable firewalld
#启动iptables
# systemctl start iptables
#将iptables设置为开机自动启动，以后即可通过iptables-service控制iptables服务
# systemctl enable iptables
```

# iptables基础

## 开篇

```yaml
# 从逻辑上讲。防火墙可以大体分为主机防火墙和网络防火墙。
主机防火墙：针对于单个主机进行防护。
网络防火墙：往往处于网络入口或边缘，针对于网络入口进行防护，服务于防火墙背后的本地局域网。
网络防火墙和主机防火墙并不冲突，可以理解为，网络防火墙主外（集体）， 主机防火墙主内（个人）。
 
# 从物理上讲，防火墙可以分为硬件防火墙和软件防火墙:
硬件防火墙：在硬件级别实现部分防火墙功能，另一部分功能基于软件实现，性能高，成本高。
软件防火墙：应用软件处理逻辑运行于通用硬件平台之上的防火墙，性能低，成本低。
```

### netfilter

```yaml
iptables其实不是真正的防火墙，我们可以把它理解成一个客户端代理，用户通过iptables这个代理，将用户的安全设定执行到对应的”安全框架”中，这个”安全框架”才是真正的防火墙，这个框架的名字叫netfilter
netfilter才是防火墙真正的安全框架（framework），netfilter位于内核空间。
iptables其实是一个命令行工具，位于用户空间，我们用这个工具操作真正的框架。
netfilter/iptables（下文中简称为iptables）组成Linux平台下的包过滤防火墙，与大多数的Linux软件一样，这个包过滤防火墙是免费的，它可以代替昂贵的商业防火墙解决方案，完成封包过滤、封包重定向和网络地址转换（NAT）等功能。
# Netfilter是Linux操作系统核心层内部的一个数据包处理模块，它具有如下功能：
1.网络地址转换(Network Address Translate)
2.数据包内容修改
3.以及数据包过滤的防火墙功能

所以说，虽然我们使用service iptables start启动iptables”服务”，但是其实准确的来说，iptables并没有一个守护进程，所以并不能算是真正意义上的服务，而应该算是内核提供的功能。
```

## iptables基础

### 链的概念

```yaml
1.当客户端访问服务器的web服务时，客户端发送报文到网卡，而tcp/ip协议栈是属于内核的一部分，所以，客户端的信息会通过内核的TCP协议传输到用户空间中的web服务中，而此时，客户端报文的目标终点为web服务所监听的套接字（IP：Port）上，当web服务需要响应客户端请求时，web服务发出的响应报文的目标终点则为客户端，这个时候，web服务所监听的IP与端口反而变成了原点，我们说过，netfilter才是真正的防火墙，它是内核的一部分，所以，如果我们想要防火墙能够达到`防火`的目的，则需要在内核中设置关卡，所有进出的报文都要通过这些关卡，经过检查后，符合放行条件的才能放行，符合阻拦条件的则需要被阻止，于是，就出现了input关卡和output关卡，而这些关卡在iptables中不被称为”关卡”,而被称为”链”。
2.其实我们上面描述的场景并不完善，因为客户端发来的报文访问的目标地址可能并不是本机，而是其他服务器，当本机的内核支持IP_FORWARD时，我们可以将报文转发给其他服务器，所以，这个时候，我们就会提到iptables中的其他”关卡”，也就是其他”链”，他们就是  “路由前”、”转发”、”路由后”，他们的英文名是
PREROUTING、FORWARD、POSTROUTING
3.所以，根据图，我们能够想象出某些常用场景中，报文的流向：
到本机某进程的报文：PREROUTING –> INPUT
由本机转发的报文：PREROUTING –> FORWARD –> POSTROUTING
由本机的某进程发出报文（通常为响应报文）：OUTPUT –> POSTROUTING
```

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/021217_0051_2.png)

### 表的概念

```yaml
# 我们对每个”链”上都放置了一串规则，但是这些规则有些很相似，比如，A类规则都是对IP或者端口的过滤，B类规则是修改报文，那么这个时候，我们是不是能把实现相同功能的规则放在一起呢，必须能的。
我们把具有相同功能的规则的集合叫做”表”，所以说，不同功能的规则，我们可以放置在不同的表中进行管理，而iptables已经为我们定义了4种表，每种表对应了不同的功能，而我们定义的规则也都逃脱不了这4种功能的范围，所以，学习iptables之前，我们必须先搞明白每种表 的作用。#
# iptables为我们提供了如下规则的分类，或者说，iptables为我们提供了如下”表”
filter表：负责过滤功能，防火墙；内核模块：iptables_filter
nat表：network address translation，网络地址转换功能；内核模块：iptable_nat
mangle表：拆解报文，做出修改，并重新封装 的功能；iptable_mangle
raw表：关闭nat表上启用的连接追踪机制；iptable_raw
也就是说，我们自定义的所有规则，都是这四种分类中的规则，或者说，所有规则都存在于这4张”表”中。
```

### 表链关系

```yaml
PREROUTING      的规则可以存在于：raw表，mangle表，nat表。
INPUT          的规则可以存在于：mangle表，filter表，（centos7中还有nat表，centos6中没有）。
FORWARD         的规则可以存在于：mangle表，filter表。
OUTPUT         的规则可以存在于：raw表mangle表，nat表，filter表。
POSTROUTING      的规则可以存在于：mangle表，nat表。

表（功能）<–>   链（钩子）：
raw     表中的规则可以被哪些链使用：PREROUTING，OUTPUT
mangle  表中的规则可以被哪些链使用：PREROUTING，INPUT，FORWARD，OUTPUT，POSTROUTING
nat     表中的规则可以被哪些链使用：PREROUTING，OUTPUT，POSTROUTING（centos7中还有INPUT，centos6中没有）
filter  表中的规则可以被哪些链使用：INPUT，FORWARD，OUTPUT
```

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/021217_0051_6.png)



###  处理动作

```yaml
# 处理动作在iptables中被称为target（这样说并不准确，我们暂且这样称呼），动作也可以分为基本动作和扩展动作。
ACCEPT：允许数据包通过。
DROP：直接丢弃数据包，不给任何回应信息，这时候客户端会感觉自己的请求泥牛入海了，过了超时时间才会有反应。
REJECT：拒绝数据包通过，必要时会给数据发送端一个响应的信息，客户端刚请求就会收到拒绝的信息。
SNAT：源地址转换，解决内网用户用同一个公网地址上网的问题。
MASQUERADE：是SNAT的一种特殊形式，适用于动态的、临时会变的ip上。
DNAT：目标地址转换。
REDIRECT：在本机做端口映射。
LOG：在/var/log/messages文件中记录日志信息，然后将数据包传递给下一条规则，也就是说除了记录以外不对数据包做任何其他操作，仍然让下一条规则去匹配。
```

### 参数

```
-t<表>：指定要操纵的表；
-A：向规则链中添加条目；
-D：从规则链中删除条目；
-i：向规则链中插入条目；
-R：替换规则链中的条目；
-L：显示规则链中已有的条目；
-F：清楚规则链中已有的条目；
-Z：清空规则链中的数据包计算器和字节计数器；
-N：创建新的用户自定义规则链；
-P：定义规则链中的默认目标；
-h：显示帮助信息；
-p：指定要匹配的数据包协议类型；
-s：指定要匹配的数据包源ip地址；
-j<目标>：指定要跳转的目标；
-i<网络接口>：指定数据包进入本机的网络接口；
-o<网络接口>：指定数据包要离开本机所使用的网络接口。

# -A 追加规则在DROP 规则后，-I增加规则在DROP 规则前。drop前才生效
```



# iptables实际操作之规则查询

![image-20210823144457209](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823144457209.png)

```yaml
上例中，我们使用-t选项，指定要操作的表，使用-L选项，查看-t选项对应的表的规则，-L选项的意思是，列出规则，所以，上述命令的含义为列出filter表的所有规则，显示出了3条链（蓝色标注部分为链），INPUT链、FORWARD链、OUTPUT链，每条链中都有自己的规则。比如，我们需要禁止某个IP地址访问我们的主机，我们则需要在INPUT链上定义规则。因为，我们在理论总结中已经提到过，报文发往本机时，会经过PREROUTING链与INPUT链（如果你没有明白，请回顾前文），所以，如果我们想要禁止某些报文发往本机，我们只能在PREROUTING链和INPUT链中定义规则，但是PREROUTING链并不存在于filter表中，换句话说就是，PREROUTING关卡天生就没有过滤的能力，所以，我们只能在INPUT链中定义，当然，如果是其他工作场景，可能需要在FORWARD链或者OUTPUT链中定义过滤规则。

# 刚才提到，我们可以使用iptables -t filter -L命令列出filter表中的所有规则，那么举一反三，我们也可以查看其它表中的规则，示例如下。
iptables -t raw -L
iptables -t mangle -L
iptables -t nat -L
其实，我们可以省略-t filter，当没有使用-t选项指定表时，默认为操作filter表，即iptables -L表示列出filter表中的所有规则。
```

![image-20210823144903597](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823144903597.png)

## 返回值解析

```yaml
pkts:对应规则匹配到的报文的个数。

bytes:对应匹配到的报文包的大小总和。

target:规则对应的target，往往表示规则对应的”动作”，即规则匹配成功后需要采取的措施。

prot:表示规则对应的协议，是否只针对某些协议应用此规则。

opt:表示规则对应的选项。

in:表示数据包由哪个接口(网卡)流入，我们可以设置通过哪块网卡流入的报文需要匹配当前规则。

out:表示数据包由哪个接口(网卡)流出，我们可以设置通过哪块网卡流出的报文需要匹配当前规则。

source:表示规则对应的源头地址，可以是一个IP，也可以是一个网段。

destination:表示规则对应的目标地址。可以是一个IP，也可以是一个网段。
```

## 参数解析

```sh
上图中INPUT链后面的括号中包含policy ACCEPT ，0 packets，0bytes 三部分。

policy表示当前链的默认策略，policy ACCEPT表示上图中INPUT的链的默认动作为ACCEPT，换句话说就是，默认接受通过INPUT关卡的所有请求，所以我们在配置INPUT链的具体规则时，应该将需要拒绝的请求配置到规则中，说白了就是”黑名单”机制，默认所有人都能通过，只有指定的人不能通过，当我们把INPUT链默认动作设置为接受(ACCEPT)，就表示所有人都能通过这个关卡，此时就应该在具体的规则中指定需要拒绝的请求，就表示只有指定的人不能通过这个关卡，这就是黑名单机制，但是，你一定发现了，上图中所显示出的规则，大部分都是接受请求(ACCEPT)，并不是想象中的拒绝请求(DROP或者REJECT)，这与我们所描述的黑名单机制不符啊，按照道理来说，默认动作为接受，就应该在具体的规则中配置需要拒绝的人，但是上图中并不是这样的，之所以出现上图中的情况，是因为IPTABLES的工作机制导致到，上例其实是利用了这些”机制”，完成了所谓的”白名单”机制，并不是我们所描述的”黑名单”机制，我们此处暂时不用关注这一点，之后会进行详细的举例并解释，此处我们只要明白policy对应的动作为链的默认动作即可，或者换句话说，我们只要理解，policy为链的默认策略即可。

packets表示当前链（上例为INPUT链）默认策略匹配到的包的数量，0 packets表示默认策略匹配到0个包。
bytes表示当前链默认策略匹配到的所有包的大小总和。

我们可以把packets与bytes称作”计数器”，上图中的计数器记录了默认策略匹配到的报文数量与总大小，”计数器”只会在使用-v选项时，才会显示出来
-n :iptables默认为我们进行了名称解析，但是在规则非常多的情况下如果进行名称解析，效率会比较低，所以，在没有此需求的情况下，我们可以使用-n选项，表示不对IP地址进行名称反解，直接显示IP地址
--line-number 显示行号
```

## 命令小节

```
iptables -t 表名 -L
```

`查看对应表的所有规则，-t选项指定要操作的表，省略”-t 表名”时，默认表示操作filter表，-L表示列出规则，即查看规则。`

```
iptables -t 表名 -L 链名
```

`查看指定表的指定链中的规则。`

```
iptables -t 表名 -v -L
```

`查看指定表的所有规则，并且显示更详细的信息（更多字段），-v表示verbose，表示详细的，冗长的，当使用-v选项时，会显示出”计数器”的信息，由于上例中使用的选项都是短选项，所以一般简写为iptables -t 表名 -vL`

```
iptables -t 表名 -n -L
```

`表示查看表的所有规则，并且在显示规则时，不对规则中的IP或者端口进行名称反解，-n选项表示不解析IP地址。`

```
iptables --line-numbers -t 表名 -L
```

`表示查看表的所有规则，并且显示规则的序号，–line-numbers选项表示显示规则的序号，注意，此选项为长选项，不能与其他短选项合并，不过此选项可以简写为–line，注意，简写后仍然是两条横杠，仍然是长选项。`

```
iptables -t 表名 -v -x -L
```

`表示查看表中的所有规则，并且显示更详细的信息(-v选项)，不过，计数器中的信息显示为精确的计数值，而不是显示为经过可读优化的计数值，-x选项表示显示计数器的精确值。`

```
iptables --line -t filter -nvxL
```

`当然，也可以只查看某张表中的某条链，此处以filter表的INPUT链为例`

```
iptables --line -t filter -nvxL INPUT
```



# iptables规则管理

```shell
多个ip用逗号隔开两侧不能有空格
端口范围是port:port1 冒号分隔
```

### 清空filter表INPUT链中的规则

```shell
iptables -F INPUT
清空INPUT链以后，filter表中的INPUT链已经不存在任何的规则，但是可以看出，INPUT链的默认策略是ACCEPT，也就是说，INPUT链默认”放行”所有发往本机的报文，当没有任何规则时，会接受所有报文，当报文没有被任何规则匹配到时，也会默认放行报文。
```

### 拒绝某个主机上的所有报文访问当前机器

iptables -t filter -I INPUT -s 49.232.142.65 -j DROP

![image-20210823150504814](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823150504814.png)





### 顺序问题(以第一条为准)

![image-20210823151546106](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823151546106.png)



```yaml
由于第一条是DROP,所以会丢弃,不会继续匹配下一条符合的了
```

![image-20210823152014375](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823152014375.png)

```yaml
ACCEPT在第一条所以匹配第一条,DROP失效
```

### 指定行插入

![image-20210823152225080](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823152225080.png)

### 删除规则

**有两种办法**

**方法一：根据规则的编号去删除规则:** `iptables -t filter -D INPUT 3`

**方法二：根据具体的匹配条件与动作删除规则:** `iptables -t filter -D INPUT -s ip -j ACCEPT`

**清空所有:**iptables -t 表名 -F 链名/iptables -t 表名 -F



### 修改规则

```shell
# 不推荐 -R选项表示修改对应链中的规则，示例表示修改filter表中INPUT链的第3条规则，将这条规则的动作修改为ACCEPT， -s 192.168.1.146为这条规则中原本的匹配条件，如果省略此匹配条件，修改后的规则中的源地址可能会变为0.0.0.0/0。
iptables -t filter -R INPUT 3 -s 192.168.1.146 -j ACCEPT

推荐: 先删除再添加,-R修改有坑
```

```shell
每张表的每条链中，都有自己的默认策略，我们也可以理解为默认”动作”。
当报文没有被链中的任何规则匹配到时，或者，当链中没有任何规则时，防火墙会按照默认动作处理报文，我们可以修改指定链的默认策略，使用如下命令即可。
使用-t指定要操作的表，使用-P选项指定要修改的链，上例中，-P FORWARD DROP表示将表中FORWRD链的默认策略改为DROP。
iptables -t filter -P FORWRAD DROP
```



### 保存规则

service iptables save/iptables-save > /etc/sysconfig/iptables

### ping

````bash
# 开启
iptables -I INPUT -p icmp --icmp-type echo-request -j ACCEPT
iptables -A OUTPUT -p icmp --icmp-type echo-reply -j ACCEPT

# 禁止
iptables -D INPUT -p icmp --icmp-type echo-request -j ACCEPT
iptables -D OUTPUT -p icmp --icmp-type echo-reply -j ACCEPT
````





# 匹配条件

```shell
iptables -t filter -I INPUT ! -s 192.168.1.0/24 -j ACCEPT  (! ip) 代表除了这个ip都匹配,但是由于并没有指定这个ip的动作,所以并不会对该ip产生拒绝的动作
```

### 基本匹配条件总结

-s用于匹配报文的源地址,可以同时指定多个源地址，每个IP之间用逗号隔开，也可以指定为一个网段。

```
#示例如下
iptables -t filter -I INPUT -s 192.168.1.111,192.168.1.118 -j DROP
iptables -t filter -I INPUT -s 192.168.1.0/24 -j ACCEPT
iptables -t filter -I INPUT ! -s 192.168.1.0/24 -j ACCEPT
```



-d用于匹配报文的目标地址,可以同时指定多个目标地址，每个IP之间用逗号隔开，也可以指定为一个网段。

```
#示例如下
iptables -t filter -I OUTPUT -d 192.168.1.111,192.168.1.118 -j DROP
iptables -t filter -I INPUT -d 192.168.1.0/24 -j ACCEPT
iptables -t filter -I INPUT ! -d 192.168.1.0/24 -j ACCEPT
```



p用于匹配报文的协议类型,可以匹配的协议类型tcp、udp、udplite、icmp、esp、ah、sctp等（centos7中还支持icmpv6、mh）。

```
#示例如下
iptables -t filter -I INPUT -p tcp -s 192.168.1.146 -j ACCEPT
iptables -t filter -I INPUT ! -p udp -s 192.168.1.146 -j ACCEPT
```

 

-i用匹配报文是从哪个网卡接口流入本机的，由于匹配条件只是用于匹配报文流入的网卡，所以在OUTPUT链与POSTROUTING链中不能使用此选项。

```
#示例如下
iptables -t filter -I INPUT -p icmp -i eth4 -j DROP
iptables -t filter -I INPUT -p icmp ! -i eth4 -j DROP
```

 

-o用于匹配报文将要从哪个网卡接口流出本机，于匹配条件只是用于匹配报文流出的网卡，所以在INPUT链与PREROUTING链中不能使用此选项。

```
#示例如下
iptables -t filter -I OUTPUT -p icmp -o eth4 -j DROP
iptables -t filter -I OUTPUT -p icmp ! -o eth4 -j DROP
```

 

### 扩展匹配条件总结

tcp扩展模块

常用的扩展匹配条件如下：

-p tcp -m tcp –sport 用于匹配tcp协议报文的源端口，可以使用冒号指定一个连续的端口范围

-p tcp -m tcp –dport 用于匹配tcp协议报文的目标端口，可以使用冒号指定一个连续的端口范围

```
#示例如下
iptables -t filter -I OUTPUT -d 192.168.1.146 -p tcp -m tcp --sport 22 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport 22:25 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport :22 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport 80: -j REJECT
iptables -t filter -I OUTPUT -d 192.168.1.146 -p tcp -m tcp ! --sport 22 -j ACCEPT
```

 

multiport扩展模块

常用的扩展匹配条件如下：

-p tcp -m multiport –sports 用于匹配报文的源端口，可以指定离散的多个端口号,端口之间用”逗号”隔开

-p udp -m multiport –dports 用于匹配报文的目标端口，可以指定离散的多个端口号，端口之间用”逗号”隔开

```
#示例如下
iptables -t filter -I OUTPUT -d 192.168.1.146 -p udp -m multiport --sports 137,138 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m multiport --dports 22,80 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m multiport ! --dports 22,80 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m multiport --dports 80:88 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m multiport --dports 22,80:88 -j REJECT
```

# iprange扩展模块

```yaml
之前我们已经总结过，在不使用任何扩展模块的情况下，使用-s选项或者-d选项即可匹配报文的源地址与目标地址，而且在指定IP地址时，可以同时指定多个IP地址，每个IP用”逗号”隔开，但是，-s选项与-d选项并不能一次性的指定一段连续的IP地址范围，如果我们需要指定一段连续的IP地址范围，可以使用iprange扩展模块。

使用iprange扩展模块可以指定”一段连续的IP地址范围”，用于匹配报文的源地址或者目标地址。
iprange扩展模块中有两个扩展匹配条件可以使用
–src-range
–dst-range
上述两个选项分别用于匹配报文的源地址所在范围与目标地址所在范围。
iptables -t filter -I INPUT -m iprange --src-range 192.168.1.127-192.168.1.146 -j DROP
iptables -t filter -I OUTPUT -m iprange --dst-range 192.168.1.127-192.168.1.146 -j DROP
iptables -t filter -I INPUT -m iprange ! --src-range 192.168.1.127-192.168.1.146 -j DROP
```

# string扩展模块

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/042517_1621_3.png)

```yaml
使用string扩展模块，可以指定要匹配的字符串，如果报文中包含对应的字符串，则符合匹配条件。
比如，如果报文中包含字符”XXOO”，我们就丢弃当前报文。
设置完上图中的规则后，由于index.html中包含”OOXX”字符串，所以，146的回应报文无法通过126的INPUT链，所以无法获取到页面对应的内容。
那么，我们来总结一下string模块的常用选项
–algo：用于指定匹配算法，可选的算法有bm与kmp，此选项为必须选项，我们不用纠结于选择哪个算法，但是我们必须指定一个。
–string：用于指定需要匹配的字符串。
# 示例
iptables -t filter -I INPUT -p tcp --sport 80 -m string --algo bm --string "OOXX" -j REJECT
iptables -t filter -I INPUT -p tcp --sport 80 -m string --algo kmp --string "OOXX" -j REJECT
```

# time扩展模块

```shell
我们可以通过time扩展模块，根据时间段区匹配报文，如果报文到达的时间在指定的时间范围以内，则符合匹配条件。
–timestart：用于指定时间范围的开始时间，不可取反
–timestop：用于指定时间范围的结束时间，不可取反

–weekdays：用于指定”星期几”，可取反
–monthdays：用于指定”几号”，可取反

–datestart：用于指定日期范围的开始日期，不可取反
–datestop：用于指定日期范围的结束时间，不可取反

# 示例
iptables -t filter -I OUTPUT -p tcp --dport 80 -m time --timestart 09:00:00 --timestop 19:00:00 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 443 -m time --timestart 09:00:00 --timestop 19:00:00 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time --weekdays 6,7 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time --monthdays 22,23 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time ! --monthdays 22,23 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time --timestart 09:00:00 --timestop 18:00:00 --weekdays 6,7 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time --weekdays 5 --monthdays 22,23,24,25,26,27,28 -j REJECT
iptables -t filter -I OUTPUT -p tcp --dport 80  -m time --datestart 2017-12-24 --datestop 2017-12-27 -j REJECT
```

**–monthdays与–weekdays可以使用”!”取反，其他选项不能取反。**

# connlimit扩展模块

****

使用connlimit扩展模块，可以限制每个IP地址同时链接到server端的链接数量，注意：我们不用指定IP，其默认就是针对”每个客户端IP”，即对单IP的并发连接数限制。

```shell
–connlimit-above：单独使用此选项时，表示限制每个IP的链接数量。
–connlimit-mask：此选项不能单独使用，在使用–connlimit-above选项时，配合此选项，则可以针对”某类IP段内的一定数量的IP”进行连接数量的限制
--connlimit-above n 　＃限制为多少个
--connlimit-mask n  　＃这组主机的掩码,默认是connlimit-mask 32 ,即每个IP.
# 示例
iptables -I INPUT -p tcp --dport 22 -m connlimit --connlimit-above 2 -j REJECT
iptables -I INPUT -p tcp --dport 22 -m connlimit --connlimit-above 20 --connlimit-mask 24 -j REJECT
iptables -I INPUT -p tcp --dport 22 -m connlimit --connlimit-above 10 --connlimit-mask 27 -j REJECT
```

# limit扩展模块

```shell
connlimit模块是对连接数量进行限制的，limit模块是对”报文到达速率”进行限制的。
用大白话说就是，如果我想要限制单位时间内流入的包的数量，就能用limit模块。
我们可以以秒为单位进行限制，也可以以分钟、小时、天作为单位进行限制。
比如，限制每秒中最多流入3个包，或者限制每分钟最多流入30个包，都可以。

–limit-burst：类比”令牌桶”算法，此选项用于指定令牌桶中令牌的最大数量,满了才会进行限制
–limit：类比”令牌桶”算法，此选项用于指定令牌桶中生成新令牌的频率，可用时间单位有second、minute 、hour、day。
# 示例
iptables -t filter -I INPUT -p icmp -m limit --limit-burst 1 --limit 10/minute -j ACCEPT
iptables -t filter -A INPUT -p icmp -j REJECT
```

```
令牌桶算法。
有一个木桶，木桶里面放了5块令牌，而且这个木桶最多也只能放下5块令牌，所有报文如果想要出关入关，都必须要持有木桶中的令牌才行，这个木桶有一个神奇的功能，就是每隔6秒钟会生成一块新的令牌，如果此时，木桶中的令牌不足5块，那么新生成的令牌就存放在木桶中，如果木桶中已经存在5块令牌，新生成的令牌就无处安放了，只能溢出木桶（令牌被丢弃），如果此时有5个报文想要入关，那么这5个报文就去木桶里找令牌，正好一人一个，于是他们5个手持令牌，快乐的入关了，此时木桶空了，再有报文想要入关，已经没有对应的令牌可以使用了，但是，过了6秒钟，新的令牌生成了，此刻，正好来了一个报文想要入关，于是，这个报文拿起这个令牌，就入关了，在这个报文之后，如果很长一段时间内没有新的报文想要入关，木桶中的令牌又会慢慢的积攒了起来，直到达到5个令牌，并且一直保持着5个令牌，直到有人需要使用这些令牌，这就是令牌桶算法的大致逻辑。
```



# 扩展匹配条件之'–tcp-flags'

```
见名知义，”–tcp-flags”指的就是tcp头中的标志位，看来，在使用iptables时，我们可以通过此扩展匹配条件，去匹配tcp报文的头部的标识位，然后根据标识位的实际情况实现访问控制的功能。
```

### –sport

用于匹配tcp协议报文的源端口，可以使用冒号指定一个连续的端口范围

```
#示例
iptables -t filter -I OUTPUT -d 192.168.1.146 -p tcp -m tcp --sport 22 -j REJECT
iptables -t filter -I OUTPUT -d 192.168.1.146 -p tcp -m tcp --sport 22:25 -j REJECT
iptables -t filter -I OUTPUT -d 192.168.1.146 -p tcp -m tcp ! --sport 22 -j ACCEPT
```

### –dport

用于匹配tcp协议报文的目标端口，可以使用冒号指定一个连续的端口范围

```
#示例
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport 22:25 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport :22 -j REJECT
iptables -t filter -I INPUT -s 192.168.1.146 -p tcp -m tcp --dport 80: -j REJECT
```

### –tcp-flags

用于匹配报文的tcp头的标志位

```
#示例
iptables -t filter -I INPUT -p tcp -m tcp --dport 22 --tcp-flags SYN,ACK,FIN,RST,URG,PSH SYN -j REJECT
iptables -t filter -I OUTPUT -p tcp -m tcp --sport 22 --tcp-flags SYN,ACK,FIN,RST,URG,PSH SYN,ACK -j REJECT
iptables -t filter -I INPUT -p tcp -m tcp --dport 22 --tcp-flags ALL SYN -j REJECT
iptables -t filter -I OUTPUT -p tcp -m tcp --sport 22 --tcp-flags ALL SYN,ACK -j REJECT
```

### –syn

用于匹配tcp新建连接的请求报文，相当于使用”–tcp-flags SYN,RST,ACK,FIN  SYN”

```
#示例
iptables -t filter -I INPUT -p tcp -m tcp --dport 22 --syn -j REJECT
```



# 扩展之udp扩展与icmp扩展

## udp扩展

我们先来说说udp扩展模块，这个扩展模块中能用的匹配条件比较少，只有两个，就是–sport与–dport，即匹配报文的源端口与目标端口。

没错，tcp模块中也有这两个选项，名称都一模一样。

只不过udp扩展模块的–sport与–dport是用于匹配UDP协议报文的源端口与目标端口，比如，放行samba服务的137与138这两个UDP端口，示例如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_1.png)

前文说明过，当使用扩展匹配条件时，如果未指定扩展模块，iptables会默认调用与”-p”对应的协议名称相同的模块，所以，当使用”-p udp”时，可以省略”-m udp”，示例如下。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_2.png)

udp扩展中的–sport与–dport同样支持指定一个连续的端口范围，示例如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_3.png)

上图中的配置表示137到157之间的所有udp端口全部对外开放，其实与tcp扩展中的使用方法相同。

 

## icmp扩展

最常用的tcp扩展、udp扩展已经总结完毕，现在聊聊icmp扩展，没错，看到icmp，你肯定就想到了ping命令，因为ping命令使用的就是icmp协议。

ICMP协议的全称为Internet Control Message Protocol，翻译为互联网控制报文协议，它主要用于探测网络上的主机是否可用，目标是否可达，网络是否通畅，路由是否可用等。

假设，我们现在想要禁止所有icmp类型的报文进入本机，那么我们可以进行如下设置。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_5.png)

上例中，我们并没有使用任何扩展匹配条件，我们只是使用”-p icmp”匹配了所有icmp协议类型的报文。

如果进行了上述设置，别的主机向我们发送的ping请求报文无法进入防火墙，我们向别人发送的ping请求对应的回应报文也无法进入防火墙。所以，我们既无法ping通别人，别人也无法ping通我们。

 假设，此刻需求有变，我们只想要ping通别人，但是不想让别人ping通我们，刚才的配置就不能满足我们了，我们则可以进行如下设置（此处不考虑禁ping的情况）

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_6.png)

上图中，使用”-m icmp”表示使用icmp扩展，因为上例中使用了”-p icmp”，所以”-m  icmp”可以省略，使用”–icmp-type”选项表示根据具体的type与code去匹配对应的icmp报文，而上图中的”–icmp-type  8/0″表示icmp报文的type为8，code为0才会被匹配到，也就是只有ping请求类型的报文才能被匹配到，所以，别人对我们发起的ping请求将会被拒绝通过防火墙，而我们之所以能够ping通别人，是因为别人回应我们的报文的icmp type为0，code也为0，所以无法被上述规则匹配到，所以我们可以看到别人回应我们的信息。

 

因为type为8的类型下只有一个code为0的类型，所以我们可以省略对应的code，示例如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_7.png)

 

除了能够使用对应type/code匹配到具体类型的icmp报文以外，我们还能用icmp报文的描述名称去匹配对应类型的报文，示例如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_8.png)

没错，上例中使用的 –icmp-type “echo-request”与 –icmp-type 8/0的效果完全相同，参考本文最上方的表格即可获取对应的icmp类型的描述名称。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050117_1112_9.png)

注意：名称中的”空格”需要替换为”-“。







# [iptables扩展模块之state扩展](https://www.zsythink.net/archives/1597)

**NEW**：连接中的第一个包，状态就是NEW，我们可以理解为新连接的第一个包的状态为NEW。

**ESTABLISHED**：我们可以把NEW状态包后面的包的状态理解为ESTABLISHED，表示连接已建立。

或许用图说话更容易被人理解

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050317_1442_2.png)

**RELATED**：从字面上理解RELATED译为关系，但是这样仍然不容易理解，我们举个例子。

比如FTP服务，FTP服务端会建立两个进程，一个命令进程，一个数据进程。

命令进程负责服务端与客户端之间的命令传输（我们可以把这个传输过程理解成state中所谓的一个”连接”，暂称为”命令连接”）。

数据进程负责服务端与客户端之间的数据传输 ( 我们把这个过程暂称为”数据连接” )。

但是具体传输哪些数据，是由命令去控制的，所以，”数据连接”中的报文与”命令连接”是有”关系”的。

那么，”数据连接”中的报文可能就是RELATED状态，因为这些报文与”命令连接”中的报文有关系。

(注：如果想要对ftp进行连接追踪，需要单独加载对应的内核模块nf_conntrack_ftp，如果想要自动加载，可以配置/etc/sysconfig/iptables-config文件)

**INVALID**：如果一个包没有办法被识别，或者这个包没有任何状态，那么这个包的状态就是INVALID，我们可以主动屏蔽状态为INVALID的报文。

**UNTRACKED**：报文的状态为untracked时，表示报文未被追踪，当报文的状态为Untracked时通常表示无法找到相关的连接。



![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050317_1442_3.png)

当前主机IP为104，当放行ESTABLISHED与RELATED状态的包以后，并没有影响通过本机远程ssh到IP为77的主机上，但是无法从104上使用22端口主动连接到77上。

对于其他端口与IP来说，也是相同的，可以从104主动发送报文，并且能够收到响应报文，但是其他主机并不能主动向104发起请求。





# [黑白名单机制](https://www.zsythink.net/archives/1604)

链的默认策略为ACCEPT时，链中的规则对应的动作应该为DROP或者REJECT，表示只有匹配到规则的报文才会被拒绝，没有被规则匹配到的报文都会被默认接受，这就是”黑名单”机制。

同理，当链的默认策略为DROP时，链中的规则对应的动作应该为ACCEPT，表示只有匹配到规则的报文才会被放行，没有被规则匹配到的报文都会被默认拒绝，这就是”白名单”机制。

如果使用白名单机制，我们就要把所有人都当做坏人，只放行好人。

如果使用黑名单机制，我们就要把所有人都当成好人，只拒绝坏人。

白名单机制似乎更加安全一些，黑名单机制似乎更加灵活一些。

那么，我们就来做一个简单的白名单吧，也就是说，只放行被规则匹配到的报文，其他报文一律拒绝，那么，我们先来配置规则。

假设，我想要放行ssh远程连接相关的报文，也想要放行web服务相关的报文，那么，我们在INPUT链中添加如下规则。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050417_1551_1.png)

如上图所示，我们已经放行了特定的报文，只有上述两条规则匹配到的报文才会被放行，现在，我们只要将INPUT链的默认策略改为DROP，即可实现白名单机制。

示例如下。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050417_1551_2.png)

上图中，我们已经将INPUT链的默认策略改为DROP，并且已经实现了所谓的白名单机制，即默认拒绝所有报文，只放行特定的报文。

如果此时，我不小心执行了”iptables -F”操作，根据我们之前学到的知识去判断，我们还能够通过ssh工具远程到服务器上吗？

我想你已经判断出了正确答案，没错，按照上图中的情况，如果此时执行”iptables  -F”操作，filter表中的所有链中的所有规则都会被清空，而INPUT链的默认策略为DROP，所以所有报文都会被拒绝，不止ssh远程请求会被拒绝，其他报文也会被拒绝，我们来实验一下。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050417_1551_3.png)

如上图所示，在当前ssh远程工具中执行”iptables -F”命令后，由于INPUT链中已经不存在任何规则，所以，所有报文都被拒绝了，包括当前的ssh远程连接。

这就是默认策略设置为DROP的缺点，在对应的链中没有设置任何规则时，这样使用默认策略为DROP是非常不明智的，因为管理员也会把自己拒之门外，即使对应的链中存在放行规则，当我们不小心使用”iptables  -F”清空规则时，放行规则被删除，则所有数据包都无法进入，这个时候就相当于给管理员挖了个坑，所以，我们如果想要使用”白名单”的机制，最好将链的默认策略保持为”ACCEPT”，然后将”拒绝所有请求”这条规则放在链的尾部，将”放行规则”放在前面，这样做，既能实现”白名单”机制，又能保证在规则被清空时，管理员还有机会连接到主机，示例如下。

因为刚才的ssh连接已经被拒绝，所以，此时直接在控制台中设置iptables规则

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050417_1551_4.png)

如上图所示，先将INPUT链的默认策略设置为ACCEPT

然后继续配置需要放行的报文的规则，如下图所示，当所有放行规则设置完成后，在INPUT链的尾部，设置一条拒绝所有请求的规则。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/050417_1551_5.png)

上图中的设置，既将INPUT链的默认策略设置为了ACCEPT，同时又使用了白名单机制，因为如果报文符合放行条件，则会被前面的放行规则匹配到，如果报文不符合放行条件，则会被最后一条拒绝规则匹配到，此刻，即使我们误操作，执行了”iptables -F”操作，也能保证管理员能够远程到主机上进行维护，因为默认策略仍然是ACCEPT。



# 自定义链

```
当默认链中的规则非常多时，不方便我们管理。如果INPUT链中存放了200条规则，这200条规则有针对httpd服务的，有针对sshd服务的，有针对私网IP的，有针对公网IP的，假如，我们突然想要修改针对httpd服务的相关规则，难道我们还要从头看一遍这200条规则，找出哪些规则是针对httpd的吗？这显然不合理。

所以，iptables中，可以自定义链，通过自定义链即可解决上述问题。
```



### 创建自定义链

```
#示例：在filter表中创建IN_WEB自定义链
iptables -t filter -N IN_WEB
```

 

### 引用自定义链

```
#示例：在INPUT链中引用刚才创建的自定义链
iptables -t filter -I INPUT -p tcp --dport 80 -j IN_WEB
```

 

### 重命名自定义链

```
#示例：将IN_WEB自定义链重命名为WEB
iptables -E IN_WEB WEB
```

 

### 删除自定义链

删除自定义链需要满足两个条件

1、自定义链没有被引用

2、自定义链中没有任何规则

```
#示例：删除引用计数为0并且不包含任何规则的WEB链
iptables -D INPUT 1 # IN_WEB的那一行
iptables -X WEB
```



# [网络防火墙](https://www.zsythink.net/archives/1663)



![image-20210823174430512](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823174430512.png)





```shell
#如果想要iptables作为网络防火墙，iptables所在主机开启核心转发功能，以便能够转发报文。
#使用如下命令查看当前主机是否已经开启了核心转发，0表示为开启，1表示已开启
cat /proc/sys/net/ipv4/ip_forward
#使用如下两种方法均可临时开启核心转发，立即生效，但是重启网络配置后会失效。
方法一：echo 1 > /proc/sys/net/ipv4/ip_forward
方法二：sysctl -w net.ipv4.ip_forward=1
#使用如下方法开启核心转发功能，重启网络服务后永久生效。
配置/etc/sysctl.conf文件（centos7中配置/usr/lib/sysctl.d/00-system.conf文件），在配置文件中将 net.ipv4.ip_forward设置为1
 
#由于iptables此时的角色为"网络防火墙"，所以需要在filter表中的FORWARD链中设置规则。
#可以使用"白名单机制"，先添加一条默认拒绝的规则，然后再为需要放行的报文设置规则。
#配置规则时需要考虑"方向问题"，针对请求报文与回应报文，考虑报文的源地址与目标地址，源端口与目标端口等。
#示例为允许网络内主机访问网络外主机的web服务与sshd服务。
iptables -A FORWARD -j REJECT
iptables -I FORWARD -s 10.1.0.0/16 -p tcp --dport 80 -j ACCEPT
iptables -I FORWARD -d 10.1.0.0/16 -p tcp --sport 80 -j ACCEPT
iptables -I FORWARD -s 10.1.0.0/16 -p tcp --dport 22 -j ACCEPT
iptables -I FORWARD -d 10.1.0.0/16 -p tcp --sport 22 -j ACCEPT
 
#可以使用state扩展模块，对上述规则进行优化，使用如下配置可以省略许多"回应报文放行规则"。
iptables -A FORWARD -j REJECT
iptables -I FORWARD -s 10.1.0.0/16 -p tcp --dport 80 -j ACCEPT
iptables -I FORWARD -s 10.1.0.0/16 -p tcp --dport 22 -j ACCEPT
iptables -I FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT
```











# [iptables动作总结](https://www.zsythink.net/archives/1684)

## 动作REJECT

REJECT动作的常用选项为–reject-with

使用–reject-with选项，可以设置提示信息，当对方被拒绝时，会提示对方为什么被拒绝。

可用值如下

icmp-net-unreachable

icmp-host-unreachable

icmp-port-unreachable,

icmp-proto-unreachable

icmp-net-prohibited

icmp-host-pro-hibited

icmp-admin-prohibited

当不设置任何值时，默认值为icmp-port-unreachable。



## 动作LOG

使用LOG动作，可以将符合条件的报文的相关信息记录到日志中，但当前报文具体是被”接受”，还是被”拒绝”，都由后面的规则控制，换句话说，LOG动作只负责记录匹配到的报文的相关信息，不负责对报文的其他处理，如果想要对报文进行进一步的处理，可以在之后设置具体规则，进行进一步的处理。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051317_0959_5.png)

如上图所示，上述规则表示所有发往22号端口的tcp报文都符合条件，所以都会被记录到日志中，查看/var/log/messages即可看到对应报文的相关信息，但是上述规则只是用于示例，因为上例中使用的匹配条件过于宽泛，所以匹配到的报文数量将会非常之多，记录到的信息也不利于分析，所以在使用LOG动作时，匹配条件应该尽量写的精确一些，匹配到的报文数量也会大幅度的减少，这样冗余的日志信息就会变少，同时日后分析日志时，日志中的信息可用程度更高。

`iptables -I INPUT -p tcp --dport 22 -j LOG`

```shell
从刚才的示例中我们已经了解到，LOG动作会将报文的相关信息记录在/var/log/message文件中，当然，我们也可以将相关信息记录在指定的文件中，以防止iptables的相关信息与其他日志信息相混淆，修改/etc/rsyslog.conf文件（或者/etc/syslog.conf），在rsyslog配置文件中添加如下配置即可。
vim /etc/rsyslog.conf
kern.warning /var/log/iptables.log
加入上述配置后，报文的相关信息将会被记录到/var/log/iptables.log文件中。
完成上述配置后，重启rsyslog服务（或者syslogd）
service rsyslog restart
服务重启后，配置即可生效，匹配到的报文的相关信息将被记录到指定的文件中。
```

LOG动作也有自己的选项，常用选项如下（先列出概念，后面有示例）

–log-level选项可以指定记录日志的日志级别，可用级别有emerg，alert，crit，error，warning，notice，info，debug。

–log-prefix选项可以给记录到的相关信息添加”标签”之类的信息，以便区分各种记录到的报文信息，方便在分析时进行过滤。

注：–log-prefix对应的值不能超过29个字符。



比如，我想要将主动连接22号端口的报文的相关信息都记录到日志中，并且把这类记录命名为”want-in-from-port-22″,则可以使用如下命令

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051317_0959_6.png)

 

完成上述配置后，我在IP地址为192.168.1.98的客户端机上，尝试使用ssh工具连接上例中的主机，然后查看对应的日志文件（已经将日志文件设置为/var/log/iptables.log）

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051317_0959_7.png)

如上图所示，ssh连接操作的报文的相关信息已经被记录到了iptables.log日志文件中，而且这条日志中包含”标签”：want-in-from-port-22，如果有很多日志记录，我们就能通过这个”标签”进行筛选了，这样方便我们查看日志，同时，从上述记录中还能够得知报文的源IP与目标IP，源端口与目标端口等信息，从上述日志我们能够看出，192.168.1.98这个IP想要在14点11分连接到192.168.1.139（当前主机的IP）的22号端口，报文由eth4网卡进入，eth4网卡的MAC地址为00:0C:29:B7:F4:D1，客户端网卡的mac地址为F4-8E-38-82-B1-29。

# SNAT DNAT MASQUERADE

![image-20210823173427441](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20210823173427441.png)

## 动作：SNAT

在文章开头的场景中，我们已经描述过，网络内部的主机可以借助SNAT隐藏自己的IP地址，同时还能够共享合法的公网IP，让局域网内的多台主机共享公网IP访问互联网。

而此时的主机B就扮演了拥有NAT功能的设备，我们使用iptables的SNAT动作达到刚才所说的目的。

连接到B主机，添加如下规则。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_11.png)

如上图所示，上图中的规则表示将来自于10.1.0.0/16网段的报文的源地址改为公司的公网IP地址。

“-t nat”表示操作nat表，我们之前一直在灌输一个概念，就是不同的表有不同的功能，filter表的功能是过滤，nat表的功能就是地址转换，所以我们需要在nat表中定义nat规则。

“-A POSTROUTING”表示将SNAT规则添加到POSTROUTING链的末尾，在centos7中，SNAT规则只能存在于POSTROUTING链与INPUT链中，在centos6中，SNAT规则只能存在于POSTROUTING链中。

你可能会问，为什么SNAT规则必须定义在POSTROUTING链中，我们可以这样认为，POSTROUTING链是iptables中报文发出的最后一个”关卡”，我们应该在报文马上发出之前，修改报文的源地址，否则就再也没有机会修改报文的源地址了，在centos7中，SNAT规则也可以定义在INPUT链中，我们可以这样理解，发往本机的报文经过INPUT链以后报文就到达了本机，如果再不修改报文的源地址，就没有机会修改了。

“-s 10.1.0.0/16″表示报文来自于10.1.0.0/16网段，前文中一直在使用这个匹配条件，我想此处应该不用赘述了。

“-j SNAT”表示使用SNAT动作，对匹配到的报文进行处理，对匹配到的报文进行源地址转换。

“–to-source  192.168.1.146″表示将匹配到的报文的源IP修改为192.168.1.146，前文中，我们已经总结过，某些动作会有自己的选项，”–to-source”就是SNAT动作的常用选项，用于指定SNAT需要将报文的源IP修改为哪个IP地址。

 

好了，只要站在前文的基础上，理解上述语句应该是分分钟的事情，聪明如你应该已经学会了，那么我们来测试一下。

目前来说，我们只配置了一条SNAT规则，并没有设置任何DNAT，现在，我们从内网主机上ping外网主机，看看能不能ping通，登录内网主机C，在C主机上向A主机的外网IP发送ping请求(假外网IP)，示例如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_12.png)

如上图所示，”内网主机”已经可以依靠SNAT访问”互联网”了。

 

为了更加清晰的理解整个SNAT过程，在C主机上抓包看看，查看一下请求报文与响应报文的IP地址，如下，在C主机上同时打开两个命令窗口，一个命令窗口中向A主机发送ping请求，另一个窗口中，使用tcpdump命令对指定的网卡进行抓包，抓取icmp协议的包。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_13.png)

从上图可以看到，10.1.0.1发出ping包，192.168.1.147进行回应，正是A主机的IP地址（用于模拟公网IP的IP地址）

看来，只是用于配置SNAT的话，我们并不用 手动的进行DNAT设置，iptables会自动维护NAT表，并将响应报文的目标地址转换回来。

 

那么，我们去A主机上再次重复一遍刚才的操作，在A主机上抓包看看，如下图所示，C主机上继续向A主机的公网IP发送ping请求，在主机A的网卡上抓包看看。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_14.png)

从上图可以看出，C主机向A主机发起ping请求时得到了回应，但是在A主机上，并不知道是C主机发来的ping请求，A主机以为是B主机发来的ping请求，从抓包的信息来看，A主机以为B主机通过公网IP：192.168.1.146向自己发起了ping请求，而A主机也将响应报文回应给了B主机，所以，整个过程，A主机都不知道C主机的存在，都以为是B主机在向自己发送请求，即使不是在公网私网的场景中，我们也能够使用这种方法，隐藏网络内的主机，只不过此处，我们所描述的环境就是私网主机共享公网IP访问互联网，那么可以看到，私网中的主机已经共享了192.168.1.146这个”伪公网IP”，那么真的共享了吗？我们使用内网主机D试试，主机D是一台windows虚拟机，我们使用它向主机A发送ping请求，看看能不能ping通。如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_15.png)

windows主机也ping通了外网主机，在A主机上抓包，看到的仍然是B主机的IP地址。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_16.png)

那么，C主机与D主机能够访问外网服务吗？我们来看看。

在C主机上访问A主机的web服务，如下图所示，访问正常。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_17.png)

同理，在windows主机中访问A主机的web服务，如下图所示，访问正常。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_18.png)

 

好了，源地址转换，已经完成了，我们只依靠了一条iptables规则，就能够使内网主机能够共享公网IP访问互联网了。

 

## 动作DNAT

公司只有一个公网IP，但是公司的内网中却有很多服务器提供各种服务，我们想要通过公网访问这些服务，改怎么办呢？

没错，使用DNAT即可，我们对外宣称，公司的公网IP上既提供了web服务，也提供了windows远程桌面，不管是访问web服务还是远程桌面，只要访问这个公网IP就行了，我们利用DNAT，将公网客户端发送过来的报文的目标地址与端口号做了映射，将访问web服务的报文转发到了内网中的C主机中，将访问远程桌面的报文转发到了内网中的D主机中。

好了，理论说完了，来动手实践一下。

 

如下配置由 [ 运维工程师 王圣杰 ]  提供，我们一起来讨论一下。

如果我们想要实现刚才描述的场景，则需要在B主机中进行如下配置。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_19.png)

如上图所示，我们先将nat表中的规则清空了，从头来过，清空nat表规则后，定义了一条DNAT规则。

“-t nat -I PREROUTING”表示在nat表中的PREROUTING链中配置DNAT规则，DNAT规则只配置在PREROUTING链与OUTPUT链中。

“-d 192.168.1.146 -p tcp –dport  3389″表示报文的目标地址为公司的公网IP地址，目标端口为tcp的3389号端口，而我们知道，windows远程桌面使用的默认端口号就是3389，当外部主机访问公司公网IP的3389号端口时，报文则符合匹配条件。

“-j DNAT –to-destination  10.1.0.6:3389″表示将符合条件的报文进行DNAT，也就是目标地址转换，将符合条件的报文的目标地址与目标端口修改为10.1.0.6:3389，”–to-destination”就是动作DNAT的常用选项。

那么综上所述，上图中定义的规则的含义为，当外网主机访问公司公网IP的3389时，其报文的目标地址与端口将会被映射到10.1.0.6:3389上。

 

好了，DNAT规则定义完了，现在能够直接使用外网主机访问私网中的服务了吗？

理论上只要完成上述DNAT配置规则即可，但是在测试时，只配置DNAT规则后，并不能正常DNAT，经过测试发现，将相应的SNAT规则同时配置后，即可正常DNAT，于是我们又配置了SNAT

示例如下。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_20.png)

注：理论上只配置DNAT规则即可，但是如果在测试时无法正常DNAT，可以尝试配置对应的SNAT，此处按照配置SNAT的流程进行。

没错，与刚才定义SNAT时使用的规则完全一样。

 

好了，完成上述配置后，我们则可以通过B主机的公网IP，连接D主机（windows主机）的远程桌面了，示例如下。

找到公网中的一台windows主机，打开远程程序

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_21.png)

 

输入公司的公网IP，点击连接按钮

注意：没有指定端口的情况下，默认使用3389端口进行连接，同时，为了确保能够连接到windows虚拟主机，请将windows虚拟主机设置为允许远程连接。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_22.png)

 

输入远程连接用户的密码以后，即可连接到windows主机

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_23.png)

连接以后，远程连接程序显示我们连接到了公司的公网IP，但是当我们查看IP地址时，发现被远程机器的IP地址其实是公司私网中的D主机的IP地址。

上图证明，我们已经成功的通过公网IP访问到了内网中的服务。

 

同理，使用类似的方法，我们也能够在外网中访问到C主机提供的web服务。

示例如下。

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_24.png)

如上图所示，我们将公司公网IP的801号端口映射到了公司内网中C主机的80端口，所以，当外网主机访问公司公网IP的801端口时，报文将会发送到C主机的80端口上。

这次，我们不用再次定义SNAT规则了，因为之前已经定义过SNAT规则，上次定义的SNAT规则只要定义一次就行，而DNAT规则则需要根据实际的情况去定义。

 

好了，完成上述DNAT映射后，我们在A主机上访问B主机的801端口试试，如下

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_25.png)

可以看到，我们访问的是B主机的公网IP，但是返回结果显示的却是C主机提供的服务内容，证明DNAT已经成功。

 

而上述过程中，外网主机A访问的始终都是公司的公网IP，但是提供服务的却是内网主机，但是我们可以对外宣称，公网IP上提供了某些服务，快来访问吧！

我觉得我说明白了，你听明白了吗？

 

## 动作MASQUERADE

上文中，我们已经描述了SNAT，也就是源地址转换，那么我们现在来认识一个与SNAT类似的动作：MASQUERADE-I

当我们拨号网上时，每次分配的IP地址往往不同，不会长期分给我们一个固定的IP地址，如果这时，我们想要让内网主机共享公网IP上网，就会很麻烦，因为每次IP地址发生变化以后，我们都要重新配置SNAT规则，这样显示不是很人性化，我们通过MASQUERADE即可解决这个问题，MASQUERADE会动态的将源地址转换为可用的IP地址，其实与SNAT实现的功能完全一致，都是修改源地址，只不过SNAT需要指明将报文的源地址改为哪个IP，而MASQUERADE则不用指定明确的IP，会动态的将报-I的源地址修改为指定网卡上可用的IP地址，示例如下：

![img](https://wxy-md.oss-cn-shanghai.aliyuncs.com/051517_1411_26.png)

如上图所示，我们指定，通过外网网卡出去的报文在经过POSTROUTING链时，会自动将报文的源地址修改为外网网卡上可用的IP地址，这时，即使外网网卡中的公网IP地址发生了改变，也能够正常的、动态的将内部主机的报文的源IP映射为对应的公网IP。

 

可以把MASQUERADE理解为动态的、自动化的SNAT，如果没有动态SNAT的需求，没有必要使用MASQUERADE，因为SNAT更加高效。

# 常用套路

```yaml
1、规则的顺序非常重要。
如果报文已经被前面的规则匹配到，IPTABLES则会对报文执行对应的动作，通常是ACCEPT或者REJECT，报文被放行或拒绝以后，即使后面的规则也能匹配到刚才放行或拒绝的报文，也没有机会再对报文执行相应的动作了（前面规则的动作为LOG时除外），所以，针对相同服务的规则，更严格的规则应该放在前面。
 
2、当规则中有多个匹配条件时，条件之间默认存在”与”的关系。
如果一条规则中包含了多个匹配条件，那么报文必须同时满足这个规则中的所有匹配条件，报文才能被这条规则匹配到。

3、在不考虑1的情况下，应该将更容易被匹配到的规则放置在前面。
比如，你写了两条规则，一条针对sshd服务，一条针对web服务。
假设，一天之内，有20000个请求访问web服务，有200个请求访问sshd服务，
那么，应该将针对web服务的规则放在前面，针对sshd的规则放在后面，因为访问web服务的请求频率更高。
如果将sshd的规则放在前面，当报文是访问web服务时，sshd的规则也要白白的验证一遍，由于访问web服务的频率更高，白白耗费的资源就更多。
如果web服务的规则放在前面，由于访问web服务的频率更高，所以无用功会比较少。
换句话说就是，在没有顺序要求的情况下，不同类别的规则，被匹配次数多的、匹配频率高的规则应该放在前面。

4、当IPTABLES所在主机作为网络防火 墙时，在配置规则时，应着重考虑方向性，双向都要考虑，从外到内，从内到外。

5、在配置IPTABLES白名单时，往往会将链的默认策略设置为ACCEPT，通过在链的最后设置REJECT规则实现白名单机制，而不是将链的默认策略设置为DROP，如果将链的默认策略设置为DROP，当链中的规则被清空时，管理员的请求也将会被DROP掉。
```

