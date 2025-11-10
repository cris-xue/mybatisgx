package com.mybatisgx.dao;

/**
 * 分页排序接口
 *
 * @author ccxuef
 * @date 2025/7/7 14:27
 */
public class Pageable {

    private Integer pageNo;

    private Integer pageSize;

    private String orderBy;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
