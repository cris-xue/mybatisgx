package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.BetweenEnd;
import com.lc.mybatisx.annotation.BetweenStart;
import org.apache.ibatis.annotations.Param;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamNode extends StructNode {

    private String name;

    private Param param;

    private BetweenStart betweenStart;

    private BetweenEnd betweenEnd;

    private Boolean isUse = false;

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

    public BetweenStart getBetweenStart() {
        return betweenStart;
    }

    public void setBetweenStart(BetweenStart betweenStart) {
        this.betweenStart = betweenStart;
    }

    public BetweenEnd getBetweenEnd() {
        return betweenEnd;
    }

    public void setBetweenEnd(BetweenEnd betweenEnd) {
        this.betweenEnd = betweenEnd;
    }

    public Boolean getUse() {
        return isUse;
    }

    public void setUse(Boolean use) {
        isUse = use;
    }
}
