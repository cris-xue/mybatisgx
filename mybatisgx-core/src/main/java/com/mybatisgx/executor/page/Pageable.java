package com.mybatisgx.executor.page;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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

    private List<Sort> sorts = new ArrayList();

    private Pageable(Integer pageNo, Integer pageSize, List<Sort> sorts) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public static Pageable of(Integer pageNo, Integer pageSize) {
        return new Pageable(pageNo, pageSize, null);
    }

    public static Pageable of(Integer pageNo, Integer pageSize, List<Sort> sorts) {
        return new Pageable(pageNo, pageSize, sorts);
    }

    public static Pageable of(List<Sort> sorts) {
        return new Pageable(null, null, sorts);
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public void addSort(String column, String order) {
        this.sorts.add(new Sort(column, order));
    }

    public String getOrderBy() {
        List<String> orderByItemList = new ArrayList<>();
        for (Sort sort : sorts) {
            orderByItemList.add(String.format("%s %s", sort.getColumn(), sort.getDirection()));
        }
        return StringUtils.join(orderByItemList, ",");
    }

    public static class Sort {

        private String column;

        private String direction;

        public Sort(String column, String direction) {
            this.column = column;
            this.direction = direction;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
}
