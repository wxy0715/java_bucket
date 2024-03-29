set global slow_query_log = 'ON';

set global log_output = 'FILE,TABLE';

set global long_query_time = 0.001;

set global log_queries_not_using_indexes = 'ON';

select * from employees;

select * from `mysql`.slow_log;


show variables like '%long_query_time%'