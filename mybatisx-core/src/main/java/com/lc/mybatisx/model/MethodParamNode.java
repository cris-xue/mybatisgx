package com.lc.mybatisx.model;

import org.apache.ibatis.annotations.Param;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamNode extends StructNode {

    private String name;

    private Param param;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}
