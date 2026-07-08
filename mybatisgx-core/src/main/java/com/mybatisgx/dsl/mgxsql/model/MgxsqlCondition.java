package com.mybatisgx.dsl.mgxsql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 条件结构模型
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlCondition {

    /**
     * 条件体文本
     */
    private String body;

    /**
     * 参数路径列表（从 #{} 中提取）
     */
    private List<String> paramPaths = new ArrayList<>();

    /**
     * 逻辑连接词（and/or）
     */
    private String logicConnector;

    /**
     * 是否为可选条件
     */
    private boolean optional;

    /**
     * 表达式条件（?(expr) 中的 expr，原样渲染）
     */
    private String expression;

    /**
     * LIKE 模式类型：both（%#{x}%）、right（#{x}%）、left（%#{x}）、none
     */
    private LikeType likeType;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getParamPaths() {
        return paramPaths;
    }

    public void setParamPaths(List<String> paramPaths) {
        this.paramPaths = paramPaths;
    }

    public void addParamPath(String paramPath) {
        this.paramPaths.add(paramPath);
    }

    public String getLogicConnector() {
        return logicConnector;
    }

    public void setLogicConnector(String logicConnector) {
        this.logicConnector = logicConnector;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public LikeType getLikeType() {
        return likeType;
    }

    public void setLikeType(LikeType likeType) {
        this.likeType = likeType;
    }

    /**
     * LIKE 模式类型
     */
    public enum LikeType {

        /**
         * 双侧模糊：%#{x}%
         */
        BOTH,

        /**
         * 右侧模糊：#{x}%
         */
        RIGHT,

        /**
         * 左侧模糊：%#{x}
         */
        LEFT,

        /**
         * 非模糊
         */
        NONE
    }
}
