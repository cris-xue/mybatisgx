package com.lc.mybatisx.model.wrapper;

import com.lc.mybatisx.wrapper.LimitWrapper;
import com.lc.mybatisx.wrapper.OrderWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class QuerySqlWrapper extends SqlWrapper {

    /**
     *
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
