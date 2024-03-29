/*
Copyright (C) 1998-2014 Christian Schmidt

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import java.net.*;
import java.util.*;

/**
 * @author wxy
 * @description 传参信息,配置参数
 * @create 2021/8/5 15:54
 */
class Configuration {
    Properties properties;

    int bindPort;
    InetAddress bindAddress;

    // Variables read from configuration file.
    boolean onlyAuto;
    String autoHostname;
    int autoPort;
    String masqueradeHostname;
    boolean isUrlSyntaxEnabled;
    // 主动模式绑定的端口
    int[] serverBindPorts;
    // 被动模式绑定的端口
    int[] clientBindPorts;
    boolean serverOneBindPort, clientOneBindPort;
    boolean validateDataConnection;
    // 是否在控制台打印调试信息，1表示打印
    boolean debug;

    // Lists of subnets.
    List useActive, usePassive;
    List allowFrom, denyFrom, allowTo, denyTo;

    // Messages.
    String msgConnect;
    String msgConnectionRefused;
    String msgOriginAccessDenied;
    String msgDestinationAccessDenied;
    String msgIncorrectSyntax;
    String msgInternalError;
    String msgMasqHostDNSError;
    List<String> denyCommand;
    List<String> denyUser;
    List<String> denyKeywork;
    List<String> denyLastname;
    Integer uploadFilesize;

    public Configuration(Properties properties) throws UnknownHostException {
        this.properties = properties;
        // 判断给没给代理监听端口,默认值8089
        bindPort = getInt("bind_port", 8089);
        String ba = getString("bind_address");
        bindAddress = ba == null ? null : InetAddress.getByName(ba.trim());
        // 主动模式绑定的端口
        serverBindPorts = getPortRanges("server_bind_ports");
        // 被动模式绑定的端口
        clientBindPorts = getPortRanges("client_bind_ports");
        serverOneBindPort = serverBindPorts != null && serverBindPorts.length == 2 &&
                            serverBindPorts[0] == serverBindPorts[1];
        clientOneBindPort = clientBindPorts != null && clientBindPorts.length == 2 &&
                            clientBindPorts[0] == clientBindPorts[1];

        masqueradeHostname = getString("masquerade_host");
        if (masqueradeHostname != null) {
            //This is just to throw an UnknownHostException
            //if the config is incorrectly set up.
            InetAddress.getByName(masqueradeHostname.trim());
        }

        useActive  = getSubnets("use_active");
        usePassive = getSubnets("use_passive");
        allowFrom  = getSubnets("allow_from");
        denyFrom   = getSubnets("deny_from");
        allowTo    = getSubnets("allow_to");
        denyTo     = getSubnets("deny_to");

        onlyAuto   = getBool("only_auto", false);
        autoHostname = getString("auto_host");
        if (autoHostname != null) {
            autoHostname = autoHostname.trim();
        }
        autoPort = getInt("auto_port", 21);

        isUrlSyntaxEnabled = getBool("enable_url_syntax", true);
        validateDataConnection = getBool("validate_data_connection", true);
        debug = getBool("output_debug_info", false);


        msgConnect = "220 " +
            getString("msg_connect", "Java FTP Proxy Server (usage: USERID=user@site) ready.");

        msgConnectionRefused = "421 " +
            getString("msg_connection_refused", "Connection refused, closing connection.");

        msgOriginAccessDenied = "531 " +
            getString("msg_origin_access_denied", "Access denied - closing connection.");

        msgDestinationAccessDenied = "531 " +
            getString("msg_destination_access_denied", "Access denied - closing connection.");

        msgIncorrectSyntax = "531 " +
            getString("msg_incorrect_syntax", "Incorrect usage - closing connection.");

        msgInternalError = "421 " +
            getString("msg_internal_error", "Internal error, closing connection.");

        msgMasqHostDNSError = "421 " +
            getString("msg_masqerade_hostname_dns_error",
                      "Unable to resolve address for " + masqueradeHostname +
                       " - closing connection.");

        denyCommand = getStringArray("deny_command");
        denyUser = getStringArray("deny_user");
        denyKeywork = getStringArray("deny_keywork");
        denyLastname = getStringArray("deny_lastname");
        uploadFilesize = getInt("upload_filesize", -1);
        if (uploadFilesize == -1) {
            uploadFilesize = null;
        }
    }

    public List<String> getStringArray(String name)
    {
        return getStringArray(name, null);
    }

    public List<String> getStringArray(String name, String defaultValue)
    {
        String value = properties.getProperty(name, defaultValue);
        properties.remove(name);
        List<String> list = new LinkedList<>();
        for (StringTokenizer st = new StringTokenizer(value, ","); st.hasMoreTokens(); list.add(st.nextToken().trim().toUpperCase()));
        return list;
    }

    public boolean getBool(String name, boolean defaultValue) {
            String value = getString(name);
            return value == null ? defaultValue : value.trim().equals("1");
    }

    public int getInt(String name, int defaultValue) {
            String value = properties.getProperty(name);
            properties.remove(name);
            return value == null ? defaultValue : Integer.parseInt(value.trim());
    }

    public String getString(String name) {
            return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
            String value = properties.getProperty(name, defaultValue);
            properties.remove(name);
            return value;
    }

    public List getSubnets(String name) {
            String s = getString(name);
        if (s == null) {
            return null;
        }

        List list = new LinkedList();
        StringTokenizer st = new StringTokenizer(s.trim(), ",");
        while (st.hasMoreTokens()) {
            list.add(new Subnet(st.nextToken().trim()));
        }

        return list;
    }

    /**
     * Returns an array of length 2n, where n is the number of port
     * ranges specified. Index 2i will contain the first port number
     * in the i'th range, and index 2i+1 will contain the last.
     * E.g. the string "111,222-333,444-555,666" will result in the
     * following array: {111, 111, 222, 333, 444, 555, 666, 666}
     */
    public int[] getPortRanges(String name) {
            String s = getString(name);
        if (s == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(s.trim(), ",");
        int[] ports = new int[st.countTokens() * 2];

        if (ports.length == 0) {
            return null;
        }

        int lastPort = 0;
        for (int p = 0; st.hasMoreTokens(); p += 2) {
            String range = st.nextToken().trim();
            int i = range.indexOf('-');

            if (i == -1) {
                ports[p] = ports[p + 1] = Integer.parseInt(range);
            } else {
                ports[p] = Integer.parseInt(range.substring(0, i));
                ports[p + 1] = Integer.parseInt(range.substring(i + 1));
            }
            if (ports[p] < lastPort || ports[p] > ports[p + 1]) {
                throw new RuntimeException("Ports should be listed in increasing order.");
            }
            lastPort = ports[p + 1];
        }

        return ports;
    }
}

