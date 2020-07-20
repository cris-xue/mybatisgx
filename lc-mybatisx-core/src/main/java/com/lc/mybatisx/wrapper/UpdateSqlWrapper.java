package com.lc.mybatisx.wrapper;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 12:35
 */
public class UpdateSqlWrapper extends SqlWrapper {

    private List<WhereWrapper> whereWrapperList;

    // 是否开启乐观锁
    private boolean lock;

    public List<WhereWrapper> getWhereWrapperList() {
        return whereWrapperList;
    }

    public void setWhereWrapperList(List<WhereWrapper> whereWrapperList) {
        this.whereWrapperList = whereWrapperList;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
