package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 14:03
 */
public class VersionWrapper extends ModelWrapper {

    /**
     * 是否开启乐观锁
     */
    private Boolean version = false;

    public Boolean getVersion() {
        return version;
    }

    public void setVersion(Boolean version) {
        this.version = version;
    }
}
