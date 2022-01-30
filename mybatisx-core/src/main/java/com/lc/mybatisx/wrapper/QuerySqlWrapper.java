package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：查询sql包装器
 * @date ：2019/11/30 20:29
 */
public class QuerySqlWrapper extends SqlWrapper {

    /**
     * 条件
     */
    private WhereWrapper whereWrapper;
    /**
     *
     */
    private PageWrapper pageWrapper;
    /**
     *
     */
    private OrderWrapper orderWrapper;

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

    public PageWrapper getPageWrapper() {
        return pageWrapper;
    }

    public void setPageWrapper(PageWrapper pageWrapper) {
        this.pageWrapper = pageWrapper;
    }

    public OrderWrapper getOrderWrapper() {
        return orderWrapper;
    }

    public void setOrderWrapper(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }
}
