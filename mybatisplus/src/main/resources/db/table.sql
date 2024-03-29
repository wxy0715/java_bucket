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