package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.BetweenEnd;
import com.lc.mybatisx.annotation.BetweenStart;
import org.apache.ibatis.annotations.Param;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamInfo extends StructNode {

    /**
     * 参数名，userName
     */
    private String paramName;
    /**
     * 数据库中的字段名，user_name = #{userName}
     */
    private String dbFieldName;

    private Param param;

    private BetweenStart betweenStart;

    private BetweenEnd betweenEnd;

    private Boolean isUse = false;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
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
