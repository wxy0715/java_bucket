package com.cjree.core.basic.util;

import com.cjree.core.model.common.Pagination;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内存分页工具
 * @author wangxingyu
 * @date 2022/12/15
 */
public class PageUtil {
    /**对数据进行分页*/
    public static<T> Pagination<T> getListToPageData(Integer page, Integer rows, Collection<T> list) {
        if (page == 0) {
            page = 1;
        }
        Pagination<T> data = new Pagination<>();
        List<T> datepagings = datepaging(list, page, rows);
        //第几页，1开始
        data.setCurrent(page);
        //每页显示的条数
        data.setSize(rows);
        //总页数
        int size = list.size();
        int totalPage = size / rows;
        data.setPages(totalPage);
        //总条数
        data.setTotal(size);
        //每页的内容
        data.setRecords(datepagings);
        return data;
    }

    /**数据分页stream实现*/
    private static <T> List<T> datepaging(Collection<T> list, Integer page, Integer rows) {
        return list.stream()
                .skip((long) rows *(page-1))
                .limit(rows)
                .collect(Collectors.toList());
    }
}
