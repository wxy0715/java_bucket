explain
select *
from employees e
         left join salaries s on e.emp_no = s.emp_no
where e.emp_no = 10001;

explain
select *
from employees e
         left join dept_emp de on e.emp_no = de.emp_no
         left join departments d on de.dept_no = d.dept_no
where e.emp_no = 10001;

explain
select * from salaries straight_join employees e on salaries.emp_no = e.emp_no
where e.emp_no = 10001;



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

-- 当join字段的类型不同时，索引无法使用
explain
select * from test_user u left join test_user_ext tue on u.id = tue.user_id
where u.id = 1;

-- 拆分前：
select *
from employees e
         left join dept_emp de on e.emp_no = de.emp_no
         left join departments d on de.dept_no = d.dept_no
where e.emp_no = 10001;
-- 拆分后
select * from employees where emp_no = 10001;
select * from dept_emp where emp_no = 10001;
select * from departments where dept_no = 'd005';