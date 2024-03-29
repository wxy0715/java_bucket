/*
 * 为什么一会儿是ALL一会儿是INDEX
 * - MySQL优化器发现全表扫描开销更低时，会直接用全表扫描
 * - 可以使用索引避免排序
 */
explain
select *
from employees
order by first_name, last_name
limit 10;

/*
 * 可以使用索引避免排序
 * [Bader,last_name1, emp_no]
 * [Bader,last_name2, emp_no]
 * [Bader,last_name3, emp_no]
 * [Bader,last_name4, emp_no]
 * [Bader,last_name5, emp_no]
 * ..
 */
explain
select *
from employees
where first_name = 'Bader'
order by last_name;

/*
 * 可以使用索引避免排序
 * ['Angel', lastname1, emp_no1]
 * ['Anni', lastname1, emp_no1]
 * ['Anz', lastname1, emp_no1]
 * ['Bader', lastname1, emp_no1]
 */
explain
select *
from employees
where first_name < 'Bader'
order by first_name;

/*
 * 可以使用索引避免排序
 */
explain
select *
from employees
where first_name = 'Bader'
  and last_name > 'Peng'
order by last_name;

/*
 * 无法利用索引避免排序【排序字段存在于多个索引中】
 * - first_name => (first_name,last_name)
 * - emp_no => 主键
 */
explain
select *
from employees
order by first_name, emp_no
limit 10;

/*
 * 无法利用索引避免排序【升降序不一致】
 */
explain
select *
from employees
order by first_name desc, last_name asc
limit 10;

/*
 * 无法利用索引避免排序【使用key_part1范围查询，使用key_part2排序】
 * ['Angel', lastname1, emp_no1]
 * ['Anni', lastname1, emp_no1]
 * ['Anz', lastname1, emp_no1]
 * ['Bader', lastname1, emp_no1]
 */
explain
select *
from employees
where first_name < 'Bader'
order by last_name;



-- sort buffer = 256k
-- 满足条件的(id, order_column) = 100m
-- [(10001,'Angel'),(88888,'Keeper'),(100001,'Zaker')] => file1
-- [(77777,'Jim'),(99999,'Lucy'),(5555, 'Hanmeimei')] => file2
-- [(10001,'Angel'),(5555, 'Hanmeimei'),(77777,'Jim'),(88888,'Keeper'),(99999,'Lucy'),(100001,'Zaker')]


SET OPTIMIZER_TRACE="enabled=on",END_MARKERS_IN_JSON=on;
SET optimizer_trace_offset=-30, optimizer_trace_limit=30;

select *
from employees
where first_name < 'Bader'
order by last_name;

select * from `information_schema`.OPTIMIZER_TRACE
where QUERY like '%Bader%';

show status like '%sort_merge_passes%'

-- 调优之前271ms

set sort_buffer_size = 1024*1024;
-- 调优之后168ms