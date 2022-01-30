package com.lc.mybatisx.wrapper;

import com.lc.mybatisx.wrapper.VersionWrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class InsertSqlWrapper extends SqlWrapper {

    /**
     * 乐观锁包装器
     */
    private VersionWrapper versionWrapper;

    public VersionWrapper getVersionWrapper() {
        return versionWrapper;
    }

    public void setVersionWrapper(VersionWrapper versionWrapper) {
        this.versionWrapper = versionWrapper;
    }
}
