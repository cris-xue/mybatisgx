package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 12:35
 */
public class UpdateSqlWrapper extends SqlWrapper {

    private WhereWrapper whereWrapper;

    /**
     * 是否开启乐观锁
     */
    private boolean version;

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }
}
