select count(*)
from salaries;
-- 120ms
-- innodb 版本8.0.18 > 8.0.13，可以针对无条件的count语句去优化
show create table salaries;
select version();
-- mysql 5.6，相同数据量，相同SQL需要花费841ms


explain
select count(*)
from salaries;

-- 方案1：创建一个更小的非主键索引
-- 方案2：把数据库引擎换成MyISAM => 实际项目用的很少，一般不会修改数据库引擎
-- 方案3：汇总表 table[table_name, count] => employees, 2000000
-- 好处：结果比较准确 table[emp_no, count]
-- 缺点：增加了维护的成本
-- 方案4：sql_calc_found_rows
select *
from salaries
limit 0,10;
select count(*)
from salaries;

-- 在做完本条查询之后，自动地去执行COUNT
select sql_calc_found_rows *
from salaries
limit 0,10;
select found_rows() as salary_count;
-- 缺点：mysql 8.0.17已经废弃这种用法，未来会被删除
-- 注意点：需要在MYSQL终端执行，IDEA无法正常返回结果。

-- 方案5：缓存 select count(*) from salaries; 存放到缓存
-- 优点：性能比较高；结果比较准确，有误差但是比较小（除非在缓存更新的期间，新增或者删除了大量数据）
-- 缺点：引入了额外的组件，增加了架构的复杂度

-- 方案6：information_schema.tables
select *
from `information_schema`.TABLES
where TABLE_SCHEMA = 'employees'
  and TABLE_NAME = 'salaries';
-- 好处：不操作salaries表，不论salaries有多少数据，都可以迅速地返回结果
-- 缺点：估算值，并不是准确值

-- 方案7：
show table status where Name = 'salaries';
-- 好处：不操作salaries表，不论salaries有多少数据，都可以迅速地返回结果
-- 缺点：估算值，并不是准确值

-- 方案8：
explain
select *
from salaries;
-- 好处：不操作salaries表，不论salaries有多少数据，都可以迅速地返回结果
-- 缺点：估算值，并不是准确值

explain
select count(*)
from salaries
where emp_no > 10010;

-- 799ms
select min(emp_no)
from salaries;

explain select count(*) - (select count(*) from salaries where emp_no <= 10010)
        from salaries;