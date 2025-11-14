package com.mybatisgx.dao;

import java.util.List;

/**
 * 分页排序接口
 *
 * @author ccxuef
 * @date 2025/7/7 14:27
 */
public class Pageable {

    private Integer pageNo;

    private Integer pageSize;

    private List<Sort> sorts;

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

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public class Sort {
        private String column;
        private String order;
    }
}
