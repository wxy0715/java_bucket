create table test_table
(
    id int not null primary key auto_increment,
    a  int not null,
    b  int not null,
    UNIQUE (id),
    INDEX (id)
) ENGINE = InnoDB;
-- 发生了重复索引，改进方案：
create table test_table
(
    id int not null primary key auto_increment,
    a  int not null,
    b  int not null
) ENGINE = InnoDB;

explain
select *
from salaries
where from_date = '1986-06-26'
order by emp_no;

-- index(from_date): type=ref    extra=null，使用了索引
-- index(from_date) 某种意义上来说就相当于index(from_date, emp_no)
-- index(from_date, to_date): type=ref    extra=Using filesort，order by子句无法使用索引
-- index(from_date, to_date)某种意义上来说就相当于index(from_date, to_date, emp_no)

