create table if not exists t_instance
(
    `id`            bigint                                                   not null comment 'id',
    `instanceName`  varchar(200)                                             null comment '实例名称',
    `instanceKey`   varchar(3000)                                            null comment '实例密钥',
    `isEnable`      tinyint(1)                  default 0                    null comment '是否可用',
    `creator`       bigint default null comment '创建人id',
    `updater`       bigint default null comment '更新人id',
    `remark`        varchar(256) default null comment '备注',
    `available`     varchar(3)  not null default 'YES' comment '逻辑删除',
    `create_date`   datetime    default current_timestamp comment '创建时间',
    `update_date`   datetime    default current_timestamp on update current_timestamp comment '更新时间',
    `version_date`  datetime    default current_timestamp on update current_timestamp comment '乐观锁',
    primary key(`id`)
) comment '实例表' charset = utf8mb4 collate = utf8mb4_general_ci ENGINE = INNODB;

create table if not exists t_instance_link
(
    `id`            bigint                                                   not null comment 'id',
    `instanceId`    bigint                                                   not null comment '实例id',
    `requesterId`   varchar(256)                                             not null comment '请求方id',
    `requesterKey`  varchar(3000)                                            null comment '请求方密钥',
    `isEnable`      tinyint(1)                  default 0                    null comment '是否可用',
    `isActive`      tinyint(1)                  default 0                    null comment '是否激活',
    `creator`       bigint default null comment '创建人id',
    `updater`       bigint default null comment '更新人id',
    `remark`        varchar(256) default null comment '备注',
    `available`     varchar(3)  not null default 'YES' comment '逻辑删除',
    `create_date`   datetime    default current_timestamp comment '创建时间',
    `update_date`   datetime    default current_timestamp on update current_timestamp comment '更新时间',
    `version_date`  datetime    default current_timestamp on update current_timestamp comment '乐观锁',
    primary key(`id`),
    index (`requesterId`)
) comment '实例链接表' charset = utf8mb4 collate = utf8mb4_general_ci ENGINE = INNODB;

create table if not exists `t_example` (
    `id`            bigint not null comment 'id',
    `code`          varchar(64) default null comment 'code',
    `name`          varchar(64) default null comment 'name',
    `person_name`   varchar(64) default null comment 'person_name',
    `ic_code`       varchar(64) default null comment 'ic_code',
    `creator`       bigint default null comment '创建人id',
    `updater`       bigint default null comment '更新人id',
    `remark`        varchar(256) default null comment '备注',
    `available`     varchar(3)  not null default 'YES' comment '逻辑删除',
    `create_date`   datetime    default current_timestamp comment '创建时间',
    `update_date`   datetime    default current_timestamp on update current_timestamp comment '更新时间',
    `version_date`  datetime    default current_timestamp on update current_timestamp comment '乐观锁',
    primary key (`id`),
    key `available` (`available`)
)comment='示例表' charset = utf8mb4 collate = utf8mb4_general_ci ENGINE = INNODB;