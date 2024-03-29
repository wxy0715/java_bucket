select * from users a
                  right join orders b on a.id = b.user_id


select * from users a cross join orders b;
-- 156行 【笛卡尔连接】
select count(*)from users;
select count(*)from orders;
select 6*26;

-- 如果cross join带有on子句，就相当于inner join
select * from users a cross join orders b on a.id = b.user_id;


show variables like 'join_buffer_size';

set global join_buffer_size = 1024*1024*50;

-- Using join buffer (Block Nested Loop) => 使用了BNLJ
explain select * from users a left join orders b on a.id = b.user_id;

-- 可能会伴随大量的随机IO=> 数据按照主键排列，而不是from_date字段排列
/*
 * [1979-06-06,1980-06-06,(30000,1979-06-06)]
 * [1978-06-06,1979-06-06,(20000,1978-06-06)]
 * [1968-06-06,1969-06-06,(80000,1968-06-06)]
 * 按照主键排序之后：
 * [1978-06-06,1979-06-06,(20000,1978-06-06)]
 * [1979-06-06,1980-06-06,(30000,1979-06-06)]
 * [1968-06-06,1969-06-06,(80000,1968-06-06)]
 * -- 一旦开启MRR，会在extra里面展示Using MRR
 */
explain
select * from salaries
where from_date <='1980-01-01';

show variables like '%optimizer_switch%';

-- index_merge=on,index_merge_union=on,index_merge_sort_union=on,index_merge_intersection=on,engine_condition_pushdown=on,index_condition_pushdown=on,mrr=on,mrr_cost_based=on,block_nested_loop=on,batched_key_access=off,materialization=on,semijoin=on,loosescan=on,firstmatch=on,duplicateweedout=on,subquery_materialization_cost_based=on,use_index_extensions=on,condition_fanout_filter=on,derived_merge=on,use_invisible_indexes=off,skip_scan=on,hash_join=on
show variables like '%read_rnd_buffer_size%';

set optimizer_switch ='mrr_cost_based=off';

set optimizer_switch ='batched_key_access=on';

-- 当使用BKA的时候，会在extra里面展示Using join buffer (Batched Key Access)
explain
select * from salaries a,employees b
where a.from_date = b.birth_date;


-- MySQL 8.0.20 Using join buffer (hash join)
explain select * from users a left join orders b on a.id = b.user_id;