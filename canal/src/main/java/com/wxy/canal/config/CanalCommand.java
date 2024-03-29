package com.wxy.canal.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CanalCommand implements CommandLineRunner {
    ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(20, 50, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1000), r -> new Thread(r,"sync_binlog_thread_"+r.hashCode()), new ThreadPoolExecutor.CallerRunsPolicy());

    @Value("${canal.server.hostname}")
    private String canalServerHost;

    @Value("${canal.server.port}")
    private Integer canalServerPort;

    @Value("${canal.server.enabled}")
    private String canalServerEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!Objects.equals(canalServerEnabled, "YES")) {
            return;
        }
        log.info("执行canal");
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalServerHost, canalServerPort), "example", "canal",
                "canal");

        int batchSize = 1000;
        try {
            connector.connect();
            // 回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
            connector.rollback();
            log.info("执行canal2");
            connector.subscribe();
            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                // 获取批量ID
                long batchId = message.getId();
                // 获取批量的数量
                int size = message.getEntries().size();
                // 如果没有数据
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    printEntry(message.getEntries());
                }
                // 提交确认
                connector.ack(batchId);
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

        } finally {
            connector.disconnect();
        }
    }

    @Autowired
    private TableContext tableContext;

    private void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                // 开启/关闭事务的实体类型，跳过
                continue;
            }
            // RowChange对象，包含了一行数据变化的所有特征
            // 比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            //打印header信息

            // 判断是否是DDL语句
            if (rowChange.getIsDdl()) {
                log.info("================》;isDdl: true,sql:" + rowChange.getSql());
            }
            // 获取RowChange对象里的每一行数据，打印出来
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                String tableName = entry.getHeader().getTableName();
                if (ObjectUtils.isEmpty(tableName)){
                    continue;
                }
                BaseAbstractStrategy type = tableContext.getType(tableName);
                if (ObjectUtils.isEmpty(type)){
                    continue;
                }
                if (eventType == CanalEntry.EventType.DELETE) {
                    threadPoolExecutor.execute(()->type.syncDelete(type.coverData(rowData.getAfterColumnsList())));
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    threadPoolExecutor.execute(()-> {
                        try {
                            type.syncInsert(type.coverData(rowData.getAfterColumnsList()));
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    threadPoolExecutor.execute(()-> {
                        try {
                            type.syncUpdate(type.coverData(rowData.getAfterColumnsList()));
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                log.info("线程池信息:{}个线程，共收到{}个任务,已完成{}个任务",threadPoolExecutor.getActiveCount(),threadPoolExecutor.getTaskCount(),threadPoolExecutor.getCompletedTaskCount());
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}
