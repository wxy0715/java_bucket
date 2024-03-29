# noinspection SqlResolveForFile

-- 长字段的调优
select *
from employees
where first_name = 'Facello';

insert into employees (emp_no, birth_date, first_name, last_name, gender, hire_date, first_name_hash)
    value (
           999999, now(),
           '大目......................',
           '大', 'M', now(),
           CRC32('大目......................')
    );
-- first_name_hash的值应该具备以下要求：
-- 1. 字段的长度应该比较的小，SHA1/MD5是不合适的
-- 2. 应当尽量避免hash冲突，就目前来说，流行使用CRC32()或者FNV64()
select *
from employees
where first_name_hash = CRC32('Facello')
  and first_name = 'Facello';

-- 长字段的调优
select *
from employees
where first_name like 'Facello%';
-- 前缀索引
alter table employees add key (first_name(5));
-- 完整列的选择性：0.0042[这个字段的最大选择性了]
select count(distinct first_name)/count(*) from employees;

-- 5: 0.0038   6:0.0041   7:0.0042
select count(distinct left(first_name, 8))/count(*) from employees;
-- 结论
alter table employees add key (first_name(7));
-- 局限性：无法做order by、group by；无法使用覆盖索引
-- "后缀索引"：额外创建一个字段，比如说first_name_reverse，在存储的时候，把first_name的值
-- 翻转过来再存储。比方说：Facello  ==> ollecaF存储到first_name_reverse