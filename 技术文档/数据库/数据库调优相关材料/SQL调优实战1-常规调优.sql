-- 关闭MySQL对存储过程的限制
set global log_bin_trust_function_creators = 0;

-- 执行insert into select...，重复repeat_times次
drop procedure if exists prepare_test_data;
DELIMITER $$
CREATE PROCEDURE prepare_test_data(IN repeat_times INT(10))
BEGIN
    DECLARE i INT DEFAULT 0;
    loopname:
    LOOP
        SET i = i + 1;

        INSERT INTO carousel (id, image_url, background_color, item_id, cat_id, type, sort, is_show,
                              create_time, update_time)
        SELECT uuid(),
               image_url,
               background_color,
               item_id,
               cat_id,
               type,
               sort,
               is_show,
               create_time,
               update_time
        from carousel;

        insert into category (name, type, father_id, logo, slogan, cat_image, bg_color)
        select name, type, father_id, logo, slogan, cat_image, bg_color
        from category;

        insert into items(id, item_name, cat_id, root_cat_id, sell_counts, on_off_status, content,
                          created_time, updated_time)
        select uuid(),
               item_name,
               cat_id,
               root_cat_id,
               sell_counts,
               on_off_status,
               content,
               created_time,
               updated_time
        from items;

        insert into items_comments (id, user_id, item_id, item_name, item_spec_id, sepc_name, comment_level,
                                    content, created_time, updated_time)
        select uuid(),
               user_id,
               item_id,
               item_name,
               item_spec_id,
               sepc_name,
               comment_level,
               content,
               created_time,
               updated_time
        from items_comments;

        insert into items_img(id, item_id, url, sort, is_main, created_time, updated_time)
        select uuid(), item_id, url, sort, is_main, created_time, updated_time
        from items_img;

        insert into items_param(id, item_id, produc_place, foot_period, brand, factory_name,
                                factory_address, packaging_method, weight, storage_method, eat_method,
                                created_time, updated_time)
        select uuid(),
               item_id,
               produc_place,
               foot_period,
               brand,
               factory_name,
               factory_address,
               packaging_method,
               weight,
               storage_method,
               eat_method,
               created_time,
               updated_time
        from items_param;

        insert into items_spec (id, item_id, name, stock, discounts, price_discount, price_normal,
                                created_time, updated_time)
        select uuid(),
               item_id,
               name,
               stock,
               discounts,
               price_discount,
               price_normal,
               created_time,
               updated_time
        from items_spec;

        insert into order_items (id, order_id, item_id, item_img, item_name, item_spec_id, item_spec_name,
                                 price, buy_counts)
        select uuid(),
               order_id,
               item_id,
               item_img,
               item_name,
               item_spec_id,
               item_spec_name,
               price,
               buy_counts
        from order_items;

        insert into order_status (order_id, order_status, created_time, pay_time, deliver_time,
                                  success_time, close_time, comment_time)
        select uuid(),
               order_status,
               created_time,
               pay_time,
               deliver_time,
               success_time,
               close_time,
               comment_time
        from order_status;

        insert into orders (id, user_id, receiver_name, receiver_mobile, receiver_address, total_amount,
                            real_pay_amount, post_amount, pay_method, left_msg, extand, is_comment,
                            is_delete, created_time, updated_time)
        select uuid(),
               user_id,
               receiver_name,
               receiver_mobile,
               receiver_address,
               total_amount,
               real_pay_amount,
               post_amount,
               pay_method,
               left_msg,
               extand,
               is_comment,
               is_delete,
               created_time,
               updated_time
        from orders;

        insert into stu (name, age)
        select name, age
        from stu;

        insert into user_address (id, user_id, receiver, mobile, province, city, district, detail, extand,
                                  is_default, created_time, updated_time)
        select uuid(),
               user_id,
               receiver,
               mobile,
               province,
               city,
               district,
               detail,
               extand,
               is_default,
               created_time,
               updated_time
        from user_address;

        insert into users(id, username, password, nickname, realname, face, mobile, email, sex, birthday,
                          created_time, updated_time)
        select uuid(),
               username,
               password,
               nickname,
               realname,
               face,
               mobile,
               email,
               sex,
               birthday,
               created_time,
               updated_time
        from users;

        IF i = repeat_times THEN
            LEAVE loopname;
        END IF;
    END LOOP loopname;
END $$;

-- 把foodie-dev项目里面所有的表数据量变成原先的2^10倍
call prepare_test_data(10);

show variables like '%long_query_time%';

set long_query_time = 0.2;


explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by i.sell_counts desc
LIMIT 10;

-- 优化之前需要花费1 s 323 ms

-- 优化1：创建index(item_id,price_discount)后，花费878ms
SELECT item_id, MIN(price_discount) as price_discount
from items_spec
GROUP BY item_id

-- 优化2：创建index(is_main, item_id)后，花费442ms

-- 优化3：创建index(sell_counts,item_name)，并使用straight_join后变成117ms


explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by i.item_name asc
LIMIT 10;
-- 优化：在前面的基础上，额外创建index(item_name,sell_counts) -> 87ms


explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by tempSpec.price_discount asc
LIMIT 10;
-- 289ms
-- 调大sort_buffer_size -> 145ms
set sort_buffer_size = 4 * 1024 * 1024;

-- 终极优化方案：反模式设计，引入冗余，把商品的最低优惠价（MIN(price_discount)）冗余到items表


explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i straight_join items_img ii
     on i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by
    --
    i.sell_counts desc
    -- i.item_name asc
    -- tempSpec.price_discount asc
LIMIT 10;
-- 激进优化方案1：尽量使用右模糊，避免全模糊
-- 激进优化方案2：彻底使用冗余优化SQL
 -- 把商品的最低优惠价，直接冗余到items表
 -- 把商品主图也冗余到items表【商品主表字段较大，实际项目中如果要冗余较大的字段，应该谨慎考虑，看是否有必要】
/* price_discount, img_url
select id, item_name, sell_counts,img_url,price_discount
from items
where item_name like '%好吃蛋糕甜点蒸蛋糕%' order by
    sell_counts desc
    -- item_name asc
    -- price_discount asc*/
-- 激进优化方案3：考虑使用非关系型数据库(elasticsearch/mongodb)
-- 激进优化方案4：业务妥协


SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i straight_join items_img ii
     on i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%xxx%';
drop table users;
select *
from items
where item_name like 'yyy%'
order by
    --
    i.sell_counts desc
    -- i.item_name asc
    -- tempSpec.price_discount asc
LIMIT 10;