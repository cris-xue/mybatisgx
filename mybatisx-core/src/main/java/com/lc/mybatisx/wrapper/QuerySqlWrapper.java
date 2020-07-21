package com.lc.mybatisx.wrapper;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class QuerySqlWrapper extends SqlWrapper {

    private List<WhereWrapper> whereWrapperList;

    public List<WhereWrapper> getWhereWrapperList() {
        return whereWrapperList;
    }

    public void setWhereWrapperList(List<WhereWrapper> whereWrapperList) {
        this.whereWrapperList = whereWrapperList;
    }
}
