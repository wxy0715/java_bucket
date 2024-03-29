# noinspection SqlResolveForFile

select *
from employees
where first_name = 'Georgi';
-- index(first_name)

select *
from employees
where first_name = 'Georgi'
  and last_name = 'Cools';
-- index(first_name, last_name)
-- index(last_name, first_name) ==> where first_name = 'Grorgi'无法使用该索引
-- 最左前缀原则

update employees
set first_name = 'Jim'
where emp_no = '100001';

delete
from employees
where first_name = 'Georgi';

-- 查询各个部门下有过多少位员工(忽略start_date和end_date)
select dept_no, count(*)
from dept_emp
group by dept_no;

select distinct(first_name)
from employees;

-- 查询工号为100001的雇员待过的部门
select emp.*, d.dept_name
from employees emp
         left join dept_emp de
                   on emp.emp_no = de.emp_no
         left join departments d
                   on de.dept_no = d.dept_no
where de.emp_no = '100001';