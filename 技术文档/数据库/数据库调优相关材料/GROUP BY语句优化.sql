-- 查询每个员工拿到过的最少的工资是多少[比惨大会]
/*
 * 分析这条SQL如何执行：
 * [emp_no, salary] 组合索引
 * [10001,50000]
 * [10001,51000]
 * ...
 * [10002,30000]
 * [10002,32000]
 * ...
 * 1. 先扫描emp_no = 10001的数据，并计算出最小的salary是多少，[10001,50000]
 * 2. 扫描emp_no = 10002，并计算出最小的salary是多少，[10002,30000]
 * 3. 遍历出每个员工的最小薪资，并返回
 ===
 * 改进：（松散索引扫描）
 * 1. 先扫描emp_no = 10001的数据，取出第一条 => 就是这个员工工资最小的数据
 * 2. 直接跳过所有的emp_no = 10001的数据，继续扫描emp_no = 10002的数据，取第一条
 * 3. 以此类推
 * explain的extra展示Using index for group-by => 说明使用了松散索引扫描
 */
explain
select emp_no, min(salary)
from salaries
group by emp_no

-- 紧凑索引扫描(explain-extra没有明显的标识)
explain
select emp_no, sum(salary)
from salaries
group by emp_no;

-- 一旦出现临时表，将会在explain-extra显示Using temporary
explain
select max(hire_date)
from employees
group by hire_date;