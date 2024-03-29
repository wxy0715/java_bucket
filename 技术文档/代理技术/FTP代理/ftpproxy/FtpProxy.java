/*
Java FTP Proxy Server 1.3.0
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

Find the latest version at http://aggemam.dk/ftpproxy
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class FtpProxy extends Thread {
    private final static String defaultConfigFile = "ftpproxy.conf";

    final static int DEFAULT_BACKLOG = 50;
    final static int DATABUFFERSIZE = 512;

    // 代理的监听socket
    Socket skControlClient,
    // 真正的服务器的监听socket
            skControlServer;
    BufferedReader brClient, brServer;
    PrintStream psClient, osServer;

    ServerSocket ssDataClient, ssDataServer;
    Socket skDataClient, skDataServer;

    // IP of interface facing client and server respectively.
    String sLocalClientIP;
    String sLocalServerIP;

    private Configuration config;

    DataConnect dcData;
    boolean serverPassive = false;
    boolean userLoggedIn = false;
    boolean connectionClosed = false;

    final static Map lastPorts = new HashMap();

    // Constants for debug output.
    final static PrintStream pwDebug = System.out;
    final static String server2proxy = "S->P: ";
    final static String proxy2server = "S<-P: ";
    final static String proxy2client = "P->C: ";
    final static String client2proxy = "P<-C: ";
    final static String server2client = "S->C: ";
    final static String client2server = "S<-C: ";



    // Use CRLF instead of println() to ensure that CRLF is used
    // on all platforms.
    public static String CRLF = "\r\n";


    public FtpProxy(Configuration config, Socket skControlClient) {
        this.config = config;
        this.skControlClient = skControlClient;

        // sLocalClientIP is initialized in main(), to handle
        // masquerade_host where the IP address for the host is dynamic.
    }

    public static void main(String[] args) {
        // 加载ftpproxy配置文件
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(defaultConfigFile));
        } catch (IOException e) {
            System.err.println("Configuration file error: " + e.getMessage());
            System.exit(0);
        }
        Configuration config;
        try {
            // 把配置文件写到实体类
            config = new Configuration(properties);
        } catch (Exception e) {
            System.err.println("Invalid configuration: " + e.getMessage());
            System.exit(0);
            // To make it compile.
            return;
        }

        // The configuration class removes the configuration variables when
        // reading them - any remaining variables are unknown and thus invalid.
        if (properties.size() > 0) {
            // 确保所有参数都有效
            System.err.println("Invalid configuration variable: " + properties.propertyNames().nextElement());
            System.exit(0);
        }

        int port = config.bindPort;

        try {
            ServerSocket ssControlClient;

            if (config.bindAddress == null) {
                ssControlClient = new ServerSocket(port);
            } else {
                // 如果监听的不是一个端口,那么就开通50个队列
                ssControlClient = new ServerSocket(port, DEFAULT_BACKLOG, config.bindAddress);
            }

            if (config.debug) {
                pwDebug.println("Listening on port " + port);
            }

            while (true) {
                Socket skControlClient = ssControlClient.accept();
                if (config.debug) {
                    pwDebug.println("New connection");
                }
                new FtpProxy(config, skControlClient).start();
            }
        } catch (IOException e) {
            if (config.debug) {
                e.printStackTrace(pwDebug);
            } else {
                System.err.println(e.toString());
            }
        }
    }

    @Override
    public void run() {
        try {
            // 代理启一个port获取客户端输出
            brClient = new BufferedReader(new InputStreamReader(skControlClient.getInputStream()));
            if (config.debug) {
                pwDebug.println("代理启动端口:"+skControlClient.getPort());
            }
            psClient = new PrintStream(skControlClient.getOutputStream());
            // 获取客户端是不是在白名单内
            if ((config.allowFrom != null &&
                 !isInSubnetList(config.allowFrom, skControlClient.getInetAddress())) ||
                isInSubnetList(config.denyFrom, skControlClient.getInetAddress())) {

                String toClient = config.msgOriginAccessDenied;
                psClient.print(toClient + CRLF);
                if (config.debug) pwDebug.println(proxy2client + toClient);
                skControlClient.close();
                return;
            }

            try {
                if (config.masqueradeHostname == null) {
                    sLocalClientIP = skControlClient.getLocalAddress().getHostAddress().replace('.', ',');
                } else {
                    sLocalClientIP = InetAddress.getByName(config.masqueradeHostname.trim()).
                        getHostAddress().replace('.', ',');
                }
            } catch (UnknownHostException e) {
                String toClient = config.msgMasqHostDNSError;
                psClient.print(toClient + CRLF);
                if (config.debug) pwDebug.println(proxy2client + toClient);
                skControlClient.close();
                return;
            }

            String username = null;
            String hostname = null;
            int serverport = 21;
            // 只代理填的地址
            if (config.onlyAuto && config.autoHostname != null) {
                // Value will not be used.
                username = null;
                hostname = config.autoHostname;
                serverport = config.autoPort;

            }
            else {
                if (config.onlyAuto) { // and autoHostname == null
                    throw new RuntimeException("only_auto is enabled, but no auto_host is set");
                }
                // 代理所有地址
                String toClient = config.msgConnect;
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);

                // The username is read from the client.
                String fromClient = brClient.readLine();
                if (config.debug) pwDebug.println(client2proxy + fromClient);

                String userString = fromClient.substring(5);

                int a = userString.lastIndexOf('@');
                int c = userString.lastIndexOf(':');
                if (c < a) c = -1;

                if (a == -1 && config.isUrlSyntaxEnabled) {
                    int c1 = userString.lastIndexOf('*');
                    if (c1 != -1) {
                        c = c1;
                        a = userString.lastIndexOf('*', c - 1);
                        if (c == a) c = -1;
                    }
                }
                if (a == -1) {
                    username = userString;
                    hostname = config.autoHostname;
                    serverport = config.autoPort;
                } else if (c == -1) {
                    username = userString.substring(0, a);
                    hostname = userString.substring(a + 1);
                } else {
                    username = userString.substring(0, a);
                    hostname = userString.substring(a + 1, c);
                    serverport = Integer.parseInt(userString.substring(c + 1));
                }
            }

            // Don't know which host to connect to.
            if (hostname == null) {
                String toClient = config.msgIncorrectSyntax;
                if (config.debug) pwDebug.println(proxy2client + toClient);
                psClient.print(toClient + CRLF);
                skControlClient.close();
                return;
            }

            InetAddress serverAddress = InetAddress.getByName(hostname);
            // 代理的服务端白名单
            if ((config.allowTo != null &&
                 !isInSubnetList(config.allowTo, serverAddress)) ||
                isInSubnetList(config.denyTo, serverAddress)) {

                String toClient = config.msgDestinationAccessDenied;

                psClient.print(toClient + CRLF);
                skControlClient.close();
                return;
            }
            // 服务器被动吗
            serverPassive = config.useActive != null && !isInSubnetList(config.useActive, serverAddress) ||
                            isInSubnetList(config.usePassive, serverAddress);

            if (config.debug) pwDebug.println("Connecting to " + hostname + " on port " + serverport);

            try {
                skControlServer = new Socket(serverAddress, serverport);
                if (config.debug) {
                    pwDebug.println("代理启动端口:"+skControlServer.getLocalPort()+"连接服务器");
                }
            } catch (ConnectException e) {
                String toClient = config.msgConnectionRefused;
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);
                return;
            }

            brServer = new BufferedReader(new InputStreamReader(skControlServer.getInputStream()));
            osServer = new PrintStream(skControlServer.getOutputStream(), true);
            sLocalServerIP = skControlServer.getLocalAddress().getHostAddress().replace('.' ,',');

            if (!config.onlyAuto) {
                String fromServer = readResponseFromServer(false);

                if (fromServer.startsWith("421")) {
                    String toClient = fromServer;
                    psClient.print(toClient + CRLF);
                    psClient.flush();
                    return;
                }
                String toServer = "USER " + username;
                osServer.print(toServer + CRLF);
                osServer.flush();
                if (config.debug) pwDebug.println(proxy2server + toServer);
            }

            readResponseFromServer(true);

            for (;;) {
                String s = brClient.readLine();
                if (s == null) {
                    break;
                }
                readCommandFromClient(s);
                // Exit if connection closed (response == 221,421,530).
                if (connectionClosed) {
                    break;
                }
            }

        } catch (Exception e) {
            String toClient = config.msgInternalError;
            if (config.debug) {
                pwDebug.println(proxy2client + toClient + e.toString());
                e.printStackTrace(pwDebug);
            }
            psClient.print(toClient + CRLF);
            psClient.flush();

        } finally {
            if (ssDataClient != null && !config.clientOneBindPort) {
                try {ssDataClient.close();} catch (IOException ioe) {}
            }
            if (ssDataServer != null && !config.serverOneBindPort) {
                try {ssDataServer.close();} catch (IOException ioe) {}
            }
            if (skDataClient != null) try {skDataClient.close();} catch (IOException ioe) {}
            if (skDataServer != null) try {skDataServer.close();} catch (IOException ioe) {}
            if (psClient != null) psClient.close();
            if (osServer != null) osServer.close();
            if (dcData != null) dcData.close();
        }
    }

    private void readCommandFromClient(String fromClient) throws IOException {
        String cmd = fromClient.toUpperCase();

        if (!userLoggedIn && (cmd.startsWith("PASV") || cmd.startsWith("PORT"))) {
            // Do not process PASV if user has not logged in yet.
            psClient.print("530 Not logged in." + CRLF);
            psClient.flush();
            return;
        }

        if (cmd.startsWith("PASV") || cmd.startsWith("EPSV")) {
            if (config.debug) pwDebug.println(client2proxy + fromClient);

            if (ssDataClient != null && !config.clientOneBindPort) {
                try { ssDataClient.close(); } catch (IOException ioe) {}
            }
            if (skDataClient != null) try { skDataClient.close(); } catch (IOException ioe) {}
            if (dcData != null) dcData.close();

            if (ssDataClient == null || !config.clientOneBindPort) {
                ssDataClient = getServerSocket(config.clientBindPorts, skControlClient.getLocalAddress());
            }

            if (ssDataClient != null) {
                int port = ssDataClient.getLocalPort();

                String toClient;
                if (cmd.startsWith("EPSV")) {
                  toClient = "229 Entering Extended Passive Mode (|||" + port + "|)";
                } else {
                  toClient = "227 Entering Passive Mode (" + sLocalClientIP + "," +
                        (int) (port / 256) + "," + (port % 256) + ")";
                }
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);

                setupServerConnection(ssDataClient);

            } else {
                String toClient = "425 Cannot allocate local port..";
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);
            }

        } else if (cmd.startsWith("PORT")) {
            int port = parsePort(fromClient);

            if (ssDataClient != null && !config.clientOneBindPort) {
                try {ssDataClient.close();} catch (IOException ioe) {}
                ssDataClient = null;
            }
            if (skDataClient != null) try {skDataClient.close();} catch (IOException ioe) {}
            if (dcData != null) dcData.close();


            if (config.debug) pwDebug.println(client2proxy + fromClient);

            try {
                skDataClient = new Socket(skControlClient.getInetAddress(), port);

                String toClient = "200 PORT command successful.";
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);

                setupServerConnection(skDataClient);

            } catch (IOException e) {
                String toClient = "425 PORT command failed - try using PASV instead.";
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);

                return;
            }


        } else {
            osServer.print(fromClient + CRLF);
            osServer.flush();
            if (config.debug) {
                pwDebug.print(client2server);
                if (cmd.startsWith("PASS")) {
                    pwDebug.println("PASS *******");
                } else {
                    pwDebug.println(fromClient);
                }
            }

            readResponseFromServer(true);
        }
    }

    private String readResponseFromServer(boolean forwardToClient) throws IOException {
        String fromServer = brServer.readLine();
        String firstLine = fromServer;

        int response = Integer.parseInt(fromServer.substring(0, 3));
        if (fromServer.charAt(3) == '-') {
            String multiLine = fromServer.substring(0, 3) + ' ';
            while (!fromServer.startsWith(multiLine)) {
                if (forwardToClient) {
                    psClient.print(fromServer + CRLF);
                    psClient.flush();
                }
                if (config.debug) pwDebug.println((forwardToClient ? server2client : server2proxy) + fromServer);

                fromServer = brServer.readLine();
            }
        }

        // Check for successful login.
        if (response == 230) {
            userLoggedIn = true;
        } else if (response == 221 || response == 421 || response == 530) {
            if (userLoggedIn) {
                connectionClosed = true;
            }
            userLoggedIn = false;
        }

        if (forwardToClient || response == 110) {
            psClient.print(fromServer + CRLF);
            psClient.flush();
        }
        if (config.debug) pwDebug.println((forwardToClient ? server2client : server2proxy) + fromServer);

        if (response >= 100 && response <= 199) {
            firstLine = readResponseFromServer(true);
        }

        return firstLine;
    }

    private void setupServerConnection(Object s) throws IOException {
        if (skDataServer != null) {
            try {skDataServer.close();} catch (IOException ioe) {}
        }

        if (serverPassive) {
            String toServer = "PASV";
            osServer.print(toServer + CRLF);
            osServer.flush();
            if (config.debug) pwDebug.println(proxy2server + toServer);

            String fromServer = readResponseFromServer(false);

            int port = parsePort(fromServer);

            if (config.debug) pwDebug.println("Server: " + skControlServer.getInetAddress() + ":" + port);

            skDataServer = new Socket(skControlServer.getInetAddress(), port);

            (dcData = new DataConnect(s, skDataServer)).start();
        } else {
            if (ssDataServer != null && !config.serverOneBindPort) {
                try {ssDataServer.close();} catch (IOException ioe) {}
            }

            if (ssDataServer == null || !config.serverOneBindPort) {
                ssDataServer = getServerSocket(config.serverBindPorts, skControlServer.getLocalAddress());
            }

            if (ssDataServer != null) {
                int port = ssDataServer.getLocalPort();
                String toServer = "PORT " + sLocalServerIP + ',' + (int) (port / 256) + ',' + (port % 256);

                osServer.print(toServer + CRLF);
                osServer.flush();
                if (config.debug) pwDebug.println(proxy2server + toServer);

                readResponseFromServer(false);

                (dcData = new DataConnect(s, ssDataServer)).start();

            } else {
                String toClient = "425 Cannot allocate local port.";
                psClient.print(toClient + CRLF);
                psClient.flush();
                if (config.debug) pwDebug.println(proxy2client + toClient);
            }
        }
    }

    public static boolean isInSubnetList(List list, InetAddress ia) {
        if (list == null) return false;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            Subnet subnet = (Subnet) iterator.next();

            if (subnet.isInSubnet(ia)) return true;
        }
        return false;
    }

    public static int parsePort(String s) throws IOException {
        int port;
        try {
            int i = s.lastIndexOf('(');
            int j = s.lastIndexOf(')');
            if ((i != -1) && (j != -1) && (i < j)) {
                s = s.substring(i + 1, j);
            }

            i = s.lastIndexOf(',');
            j = s.lastIndexOf(',', i - 1);

            port = Integer.parseInt(s.substring(i + 1));
            port += 256 * Integer.parseInt(s.substring(j + 1, i));
        } catch (Exception e) {
            throw new IOException();
        }
        return port;
    }

    public static synchronized ServerSocket getServerSocket(int[] portRanges, InetAddress ia) throws IOException {
        ServerSocket ss = null;
        if (portRanges != null) {
            // Current index of portRanges array.
            int i;
            int port;

            Integer lastPort = (Integer) lastPorts.get(portRanges);
            if (lastPort != null) {
                port = lastPort.intValue();
                for (i = 0; i < portRanges.length && port > portRanges[i + 1]; i += 2);
                port++;
            } else {
                port = portRanges[0];
                i = 0;
            }

            for (int lastTry = -2; port != lastTry; port++) {
                if (port > portRanges[i + 1]) {
                    i = (i + 2) % portRanges.length;
                    port = portRanges[i];
                }
                if (lastTry == -1) lastTry = port;
                try {
                    ss = new ServerSocket(port, 1, ia);
                    lastPorts.put(portRanges, new Integer(port));
                    break;
                } catch(BindException e) {
                    // Port already in use.
                }
            }
        } else {
            ss = new ServerSocket(0, 1, ia);
        }
        return ss;
    }

    public class DataConnect extends Thread {
        private byte buffer[] = new byte[DATABUFFERSIZE];
        private final Socket[] sockets = new Socket[2];
        private boolean isInitialized;
        private final Object[] o;
        private boolean validDataConnection;

        private Object mutex = new Object();

        // Each argument may be either a Socket or a ServerSocket.
        public DataConnect (Object o1, Object o2) {
            this.o = new Object[] {o1, o2};
        }

        @Override
        public void run() {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            validDataConnection = false;

            try {
                // n = 0 - Thread Copy socket 0 to socket 1
                // n = 1 - Thread Copy socket 1 to socket 0
                int n = isInitialized ? 1 : 0;
                if (!isInitialized) {
                    for (int i = 0; i < 2; i++) {
                        if (o[i] instanceof ServerSocket) {
                            ServerSocket ss = (ServerSocket) o[i];
                            sockets[i] = ss.accept();
                            if (ss == ssDataServer && !config.serverOneBindPort ||
                                ss == ssDataClient && !config.clientOneBindPort) {

                                ss.close();
                            }
                        } else {
                            sockets[i] = (Socket) o[i];
                        }
                        // Check to see if DataConnection is from same IP address
                        // as the ControlConnection.
                        if (skControlClient.getInetAddress().getHostAddress().
                            compareTo(sockets[i].getInetAddress().getHostAddress()) == 0) {

                            validDataConnection = true;
                        }
                    }
                    // Check to see if Data InetAddress == Control InetAddress, otherwise
                    // somebody else opened a connection!  Close all the connections.
                    if (config.validateDataConnection && !validDataConnection) {
                        pwDebug.println("Invalid DataConnection - not from Control Client");
                        throw new SocketException("Invalid DataConnection - not from Control Client");
                    }

                    isInitialized = true;

                    // In some cases thread socket[0] -> socket[1] thread can
                    // finish before socket[1] -> socket[0] has a chance to start,
                    // so synchronize on a semaphore
                    synchronized(mutex) {
                        new Thread(this).start();
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            // Never occurs.
                        }
                    }

                }

                bis = new BufferedInputStream(sockets[n].getInputStream());
                bos = new BufferedOutputStream(sockets[1 - n].getOutputStream());

                synchronized(mutex) {
                   mutex.notify();
                }

                for (;;) {
                    for (int i; (i = bis.read(buffer, 0, DATABUFFERSIZE)) != -1; ) {
                        bos.write(buffer, 0, i);
                    }
                    break;
                }
                bos.flush();
            } catch (SocketException e) {
                // Socket closed.
            } catch (IOException e) {
                if (config.debug) e.printStackTrace(pwDebug);
            }
            close();
        }

        public void close() {
            try { sockets[0].close(); } catch (Exception e) {}
            try { sockets[1].close(); } catch (Exception e) {}
        }
    }
}

