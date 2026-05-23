package com.mybatisgx.model;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/3 11:13
 */
public class BatchParamInfo {

    /**
     * 批量处理数据参数信息
     */
    private MethodParamInfo dataParamInfo;
    /**
     * 批量处理尺寸参数信息
     */
    private MethodParamInfo sizeParamInfo;

    public MethodParamInfo getDataParamInfo() {
        return dataParamInfo;
    }

    public void setDataParamInfo(MethodParamInfo dataParamInfo) {
        this.dataParamInfo = dataParamInfo;
    }

    public MethodParamInfo getSizeParamInfo() {
        return sizeParamInfo;
    }

    public void setSizeParamInfo(MethodParamInfo sizeParamInfo) {
        this.sizeParamInfo = sizeParamInfo;
    }
}
