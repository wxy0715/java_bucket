package com.wxy.utils.http.rpc;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorThread extends Thread {

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
