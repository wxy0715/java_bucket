CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    sex CHAR(3) NULL DEFAULT '男' COMMENT '性别',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    wallets VARCHAR(3000) NULL DEFAULT NULL COMMENT '钱包',
    other_info VARCHAR(3000) NULL DEFAULT NULL COMMENT '其他信息',
    tenant_id varchar(100) default NULL COMMENT '租户ID',
    version INT(11) NULL DEFAULT 1 COMMENT '乐观锁',
    available VARCHAR(3) NOT NULL default 'YES' comment '逻辑删除字段 YES/NO',
    PRIMARY KEY (id)
);

-- for AT mode you must to init this sql for you business database. the seata server not need it.
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';