-- 查询第一页的时候，花费92ms
-- 查询第30001页的时候，花费174ms
explain
select *
from employees
limit 300000,10;

-- 方案1：覆盖索引 (108ms)
explain
select emp_no
from employees
limit 300000,10;

-- 方案2：覆盖索引+join(109ms)
select *
from employees e
         inner join
     (select emp_no from employees limit 300000,10) t
     using (emp_no);

-- 方案3：覆盖索引+子查询（126ms）
select *
from employees
where emp_no >=
      (select emp_no from employees limit 300000,1)
limit 10;

-- 方案4：范围查询+limit语句
select *
from employees
limit 10;

select *
from employees
where emp_no > 10010
limit 10;

-- 方案5：如果能获得起始主键值 & 结束主键值
select *
from employees
where emp_no between 20000 and 20010;

-- 方案6：禁止传入过大的页码