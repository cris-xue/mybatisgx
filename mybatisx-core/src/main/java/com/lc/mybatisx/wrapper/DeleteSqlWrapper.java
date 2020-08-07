package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 12:35
 */
public class DeleteSqlWrapper extends SqlWrapper {

    private WhereWrapper whereWrapper;

    /**
     * 乐观锁包装器
     */
    private VersionWrapper versionWrapper;

    private Boolean versionQuery = false;

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

    public VersionWrapper getVersionWrapper() {
        return versionWrapper;
    }

    public void setVersionWrapper(VersionWrapper versionWrapper) {
        this.versionWrapper = versionWrapper;
    }

    public Boolean getVersionQuery() {
        return versionQuery;
    }

    public void setVersionQuery(Boolean versionQuery) {
        this.versionQuery = versionQuery;
    }
}
