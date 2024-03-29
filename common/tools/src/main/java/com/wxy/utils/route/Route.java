package com.wxy.utils.route;

import com.wxy.utils.file.FileIoUtils;
import com.wxy.utils.shell.RemoteConnect;
import com.wxy.utils.shell.SshConnection;
import com.wxy.utils.shell.SshExecuter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Log
@EqualsAndHashCode(callSuper = false)
public class Route {

	/**目标地址*/
	private String destination;

	/**网关*/
	private String gateway;

	/**子网掩码*/
	private String genmask;

	/**标记;
	 U: 路由是活动的;
	 H: 目标是一个主机;
	 G: 路由指向网关;
	 R: 恢复动态路由产生的表项;
	 D: 由路由的后台程序动态地安装;
	 M: 由路由的后台程序修改;
	 !: 拒绝路由;*/
	private String flags;

	/**路由距离，到达指定网络所需的中转数*/
	private String metric;

	/**路由项引用次数*/
	private String fef;

	/**此路由项被路由软件查找的次数*/
	private String use;

	/**该路由表项对应的输出接口*/
	private String iface;

	public static List<Route> instance(){
		String result = SshExecuter.execShReturnString("route -n | awk 'NR>2' ");
		List<Route> routeList = new ArrayList<>(10);
		if (StringUtils.isNoneBlank(result)) {
			String[] split = result.split(";");
			for (String item : split) {
				String[] s = item.split("\\s+");
				Route route = new Route();
				// 条件查询
				route.setDestination(s[0]);
				route.setGateway(s[1]);
				route.setGenmask(s[2]);
				route.setFlags(s[3]);
				route.setMetric(s[4]);
				route.setFef(s[5]);
				route.setUse(s[6]);
				route.setIface(s[7]);
				routeList.add(route);
			}
		}
		return  routeList;
	}

	public static List<String> getStaticRouter() {
		List<String> stringList = new ArrayList<>(16);
		// 读取文件
		try {
			FileIoUtils.getFileByNameCreate("/etc/sysconfig/static-routes",0);
			// 执行系统命令
			String result = SshExecuter.execShReturnString("cat /etc/sysconfig/static-routes");
			if (StringUtils.isNoneBlank(result)) {
				String[] split = result.split(";");
				stringList.addAll(Arrays.asList(split));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringList;
	}

	public static List<String> editStaticRouter(){
		List<String> stringList = new ArrayList<>(16);
		// 读取文件

		return stringList;
	}

	public static List<String> addStaticRouter(){
		List<String> stringList = new ArrayList<>(16);
		// 读取文件

		return stringList;
	}

	public void mysqlTest(){
		RemoteConnect connect = new RemoteConnect()
				.setIp("127.0.0.1")
				.setPort(22)
				.setUserName("root")
				.setPassword("root");
		SshConnection sshConnection = new SshConnection(connect);
		String result1 = sshConnection.execute("/opt/sgg/mariadb/bin/mysql -usgg -psailing -D  sgg -e \"show tables\" | awk 'NR>1'");
		List<String> all = Arrays.stream(result1.split("\n")).collect(Collectors.toList());
		String result = sshConnection.execute("/opt/sgg/mariadb/bin/mysql -usgg -psailing -D  sgg -e \"show tables like 'log_%'\" | awk 'NR>1'");
		List<String> list = new ArrayList<>();
		Arrays.stream(result.split("\n")).forEach(item -> list.add(" --ignore-table=sgg." + item));
		Arrays.stream(result.split("\n")).forEach(all::remove);
		System.out.println(String.join("",list));
		System.out.println(all);

	}

	public static void main(String[] args) {
		RemoteConnect connect = new RemoteConnect()
				.setIp("127.0.0.1")
				.setPort(22)
				.setUserName("root")
				.setPassword("root");
		SshConnection sshConnection = new SshConnection(connect);
		String result = sshConnection.execute("route -n | awk 'NR>2' ");
		List<Route> routeList = new ArrayList<>();
		if (StringUtils.isNoneBlank(result)) {
			String[] split = result.split("\n");
			for (String item : split) {
				String[] s = item.split("\\s+");
				Route route = new Route();
				route.setDestination(s[0]);
				route.setGateway(s[1]);
				route.setGenmask(s[2]);
				route.setFlags(s[3]);
				route.setMetric(s[4]);
				route.setFef(s[5]);
				route.setUse(s[6]);
				route.setIface(s[7]);
				routeList.add(route);
			}
		}
		System.out.println(routeList);

		// 查看静态路由
		sshConnection.execute("touch /etc/sysconfig/static-routes");
		String result1 = sshConnection.execute("cat /etc/sysconfig/static-routes");
		List<String> stringList = new ArrayList<>();
		if (StringUtils.isNoneBlank(result1)) {
			String[] split = result1.split("\n");
			stringList.addAll(Arrays.asList(split));
			System.out.println(stringList);
		}

		// 添加静态路由
		stringList.add("any net 192.101.1.1 gw 0.0.0.0");
		StringBuffer stringBuffer = new StringBuffer();
		stringList.forEach(item->{
			stringBuffer.append(item).append("\n");
		});
		System.out.println(stringBuffer);
		//sshConnection.execute("cat > /etc/sysconfig/static-routes << EOF \n"+stringBuffer+"EOF");
		//sshConnection.execute("cat >> /etc/sysconfig/static-routes << EOF \n"+stringBuffer+"EOF");
		// 重启网络

		// 修改静态路由
		sshConnection.execute("sed -i 's/any net 192.101.1.1 gw 0.0.0.0/any net 192.101.1.1 gw 0.0.0.1/g' /etc/sysconfig/static-routes");
		// 重启网络

	}

	/** 参数
	 -c	显示更多信息
	 -n	不解析名字
	 -v	显示详细的处理信息
	 -F	显示发送信息
	 -C	显示路由缓存

	 add	添加一条路由规则
	 del	删除一条路由规则
	 -net	目的地址是一个网络
	 -host	目的地址是一个主机
	 target	目的网络或主机
	 netmask	目的地址的网络掩码
	 gw	路由数据包通过的网关
	 dev	为路由指定的网络接口

	 示例
	 添加默认网关 为10.0.0.1
	 root@text:~# route add default gw 10.0.0.1
	 删除默认网关
	 root@text:~# route del default gw 10.0.0.1
	 添加到主机的路由
	 root@text:~# route add -host 192.168.10.2 dev eth3
	 root@text:~# route -n
	 Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
	 192.168.10.2     *               255.255.255.255 UH    0      0        0 eth3
	 删除到主机的路由
	 root@text:~# route del -host 192.168.10.2 dev eth3
	 添加到网络的路由
	 root@text:~# route add -net 192.168.55.0 netmask 255.255.255.0 eth3
	 删除到网络的路由
	 root@text:~# route del -net 192.168.55.0 netmask 255.255.255.0 eth3
	* */
}
