create table `test_user`
(
    `id`   int auto_increment,
    `name` varchar(45) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`)
);
INSERT INTO test_user (id, name)
VALUES (1, '张三');

CREATE TABLE `test_user_ext`
(
    `id`      int(11)     NOT NULL AUTO_INCREMENT,
    `user_id` varchar(45) NOT NULL DEFAULT '',
    `email`   varchar(50) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `test_user_ext_user_id_index` (`user_id`)
);
INSERT INTO test_user_ext (id, user_id, email)
VALUES (1, '1', 'damu@imooc.com');