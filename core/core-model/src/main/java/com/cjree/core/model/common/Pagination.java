package com.cjree.core.model.common;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjree.core.model.validate.QueryPage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@Slf4j
public class Pagination<T> {

    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
    private final int offset;
    private final int limit;
    /**
     * 总数
     */
    private int total;
    /**
     * 每页显示条数，默认 10
     */
    @NotNull(groups = {QueryPage.class})
    @Min(value = 1, message = "每页显示条数不能小于1")
    private int size = 10;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 当前页
     */
    @NotNull(groups = {QueryPage.class})
    @Min(value = 1, message = "当前页不能小于1")
    private int current = 1;
    /**
     * 查询总记录数（默认 true）
     */
    private boolean searchCount = true;
    /**
     * 开启排序（默认true）只在代码逻辑判断并不截取sql分析
     */
    private boolean openSort = true;
    /**
     * 优化 Count Sql 设置 false 执行 select count(1) from (listSql)
     */
    private boolean optimizeCountSql = true;
    /**
     * SQL 排序 ASC 集合
     */
    private List<String> ascs = new ArrayList<String>(0);
    /**
     * SQL 排序 DESC 集合
     */
    private List<String> descs = new ArrayList<String>(0);
    /**
     * 是否为升序 ASC（ 默认：true ）
     *
     * @see #ascs
     * @see #descs
     */
    private boolean isAsc = true;
    /**
     * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
     * DESC 表示按倒序排序(即：从大到小排序)<br>
     * ASC 表示按正序排序(即：从小到大排序)
     *
     * @see #ascs
     * @see #descs
     */
    private String orderByField;
    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();
    /**
     * 查询参数（ 不会传入到 xml 层，这里是 Controller 层与 service 层传递参数预留 ）
     */
    private Map<String, Object> condition;
    public Pagination() {
        this.offset = NO_ROW_OFFSET;
        this.limit = NO_ROW_LIMIT;
    }
    public Pagination(long current, long size) {
        this((int) current, (int) size);
    }

    public Pagination(int current, int size) {
        this(current, size, true);
    }

    public Pagination(int current, int size, boolean searchCount) {
        this(current, size, searchCount, true);
    }

    public Pagination(int current, int size, boolean searchCount, boolean openSort) {
        this.offset = offsetCurrent(current, size);
        this.limit = size;
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.searchCount = searchCount;
        this.openSort = openSort;
    }

    public Pagination(int current, int size, String orderByField) {
        this(current, size);
        this.setOrderByField(orderByField);
    }

    public Pagination(int current, int size, String orderByField, boolean isAsc) {
        this(current, size, orderByField);
        this.setAsc(isAsc);
    }

    public static Pagination fromPage(Page page) {
        Pagination pagination = new Pagination();
        pagination.setCurrent((int) page.getCurrent());
        pagination.setSize((int) page.getSize());
        pagination.setTotal((int) page.getTotal());
        pagination.setRecords(page.getRecords());
        return pagination;
    }

    public static <T> Pagination<T> toPagination(Page<T> page) {
        Pagination<T> pagination = new Pagination<>();
        pagination.setCurrent((int) page.getCurrent());
        pagination.setSize((int) page.getSize());
        pagination.setTotal((int) page.getTotal());
        pagination.setRecords(page.getRecords());
        return pagination;
    }

    public static <T> Page<T> toPage(Pagination<T> pagination) {
        Page page = new Page();
        page.setCurrent(pagination.getCurrent());
        page.setSize(pagination.getSize());
        page.addOrder(OrderItem.ascs(pagination.getAscs().toArray(new String[]{})));
        page.addOrder(OrderItem.descs(pagination.getDescs().toArray(new String[]{})));
        return page;
    }

    /**
     * 计算当前分页偏移量
     *
     * @param current 当前页
     * @param size    每页显示数量
     * @return
     */
    public int offsetCurrent(int current, int size) {
        if (current > 0) {
            return (current - 1) * size;
        }
        return 0;
    }

    /**
     * Pagination 分页偏移量
     */
    public int offsetCurrent(Pagination<T> page) {
        if (null == page) {
            return 0;
        }
        return offsetCurrent(page.getCurrent(), page.getSize());
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        if (pages == 0) {
            if (this.size == 0) {
                pages = 0;
            } else {
                int pageCount = this.total / this.size;
                if (this.total % (long) this.size != 0L) {
                    ++pageCount;
                }
                pages = pageCount;
            }
        }
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public boolean isOpenSort() {
        return openSort;
    }

    public void setOpenSort(boolean openSort) {
        this.openSort = openSort;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }

    public List<String> getAscs() {
        return ascs;
    }

    public void setAscs(List<String> ascs) {
        this.ascs = ascs;
    }

    public List<String> getDescs() {
        return descs;
    }

    public void setDescs(List<String> descs) {
        this.descs = descs;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<T> getRecords() {
        return records;
    }

    public Pagination<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public Pagination<T> setCondition(Map<String, Object> condition) {
        this.condition = condition;
        return this;
    }

}
