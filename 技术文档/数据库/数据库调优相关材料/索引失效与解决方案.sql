# noinspection SqlResolveForFile

/*
 * 可能导致索引失效的场景：
 * 1. 索引列不独立。独立是指：列不能是表达式的一部分，也不能是函数的参数
 * 2. 使用了左模糊
 * 3. 使用OR查询的部分字段没有索引
 * 4. 字符串条件未使用''引起来
 * 5. 不符合最左前缀原则的查询
 * 6. 索引字段建议添加NOT NULL约束
 * 7. 隐式转换导致索引失效
 */

-- 示例1：索引字段不独立(索引字段进行了表达式计算)
explain
select *
from employees
where emp_no + 1 = 10003;
-- 解决方案：事先计算好表达式的值，再传过来，避免在SQLwhere条件 = 的左侧做计算
explain
select *
from employees
where emp_no = 10002;


-- ---------------------
-- ---------------------
-- 示例2：索引字段不独立(索引字段是函数的参数)
explain
select *
from employees
where SUBSTRING(first_name, 1, 3) = 'Geo';
-- 解决方案：预先计算好结果，再传过来，在where条件的左侧，不要使用函数；或者使用等价的SQL去实现
explain
select *
from employees
where first_name like 'Geo%';


-- ---------------------
-- ---------------------
-- 示例3：使用了左模糊
explain
select *
from employees
where first_name like '%Geo%';
-- 解决方案：尽量避免使用左模糊，如果避免不了，可以考虑使用搜索引擎去解决
explain
select *
from employees
where first_name like 'Geo%';


-- ---------------------
-- ---------------------
-- 示例4：使用OR查询的部分字段没有索引
explain
select *
from employees
where first_name = 'Georgi'
   or last_name = 'Georgi';
-- 解决方案：分别为first_name以及last_name字段创建索引


-- ---------------------
-- ---------------------
-- 示例5：字符串条件未使用''引起来
explain
select *
from dept_emp
where dept_no = 3;
-- 解决方案：规范地编写SQL
explain
select *
from dept_emp
where dept_no = '3';

-- ---------------------
-- ---------------------
-- 示例6：不符合最左前缀原则的查询
-- 存在index(last_name, first_name)
explain select *
        from employees
        where first_name = 'Facello';
-- 解决方案：调整索引的顺序，变成index(first_name,last_name)/index(first_name)


-- ---------------------
-- ---------------------
-- 示例7：索引字段建议添加NOT NULL约束
-- 单列索引无法储null值，复合索引无法储全为null的值
-- 查询时，采用is null条件时，不能利用到索引，只能全表扫描
-- MySQL官方建议尽量把字段定义为NOT NULL：https://dev.mysql.com/doc/refman/8.0/en/data-size.html
explain
select *
from `foodie-shop-dev`.users
where mobile is null;
-- 解决方案：把索引字段设置成NOT NULL，甚至可以把所有字段都设置成NOT NULL并为字段设置默认值


-- ---------------------
-- ---------------------
-- 示例8：隐式转换导致索引失效
-- 目前没这样的表，演示不了，同学们可以试试把de.emp_no的字段类型改成varchar
select emp.*, d.dept_name
from employees emp
         left join dept_emp de
                   on emp.emp_no = de.emp_no
         left join departments d
                   on de.dept_no = d.dept_no
where de.emp_no = '100001';
-- 解决方案：在创建表的时候尽量规范一点，比如统一用int，或者bigint