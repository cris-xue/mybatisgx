package com.lc.mybatisx.model.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class QuerySqlWrapper extends SqlWrapper {

    /**
     * 数据
     */
    private DataWrapper dataWrapper;
    /**
     * 条件
     */
    private WhereWrapper whereWrapper;
    /**
     *
     */
    private LimitWrapper limitWrapper;
    /**
     *
     */
    private OrderWrapper orderWrapper;

    public DataWrapper getDataWrapper() {
        return dataWrapper;
    }

    public void setDataWrapper(DataWrapper dataWrapper) {
        this.dataWrapper = dataWrapper;
    }

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

    public LimitWrapper getLimitWrapper() {
        return limitWrapper;
    }

    public void setLimitWrapper(LimitWrapper limitWrapper) {
        this.limitWrapper = limitWrapper;
    }

    public OrderWrapper getOrderWrapper() {
        return orderWrapper;
    }

    public void setOrderWrapper(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }
}
