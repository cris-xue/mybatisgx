package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.dsl.mgxql.model.expression.SqlExpression;

import java.util.List;

/**
 * 绑定参数条目，描述单个"列 ↔ 参数路径"的映射关系
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class BoundParamEntry {

    /**
     * 左侧 SQL 表达式
     */
    private SqlExpression sqlExpression;

    /**
     * 右侧参数路径（可为 null，表示无参数）
     */
    private List<String> paramPath;

    /**
     * 右侧字面量值（paramPath 为 null 时使用）
     */
    private Object literalValue;

    /**
     * 类型处理器（可选）
     */
    private String typeHandler;

    /**
     * 多 entry 间的逻辑连接符
     */
    private LogicOperator logicOperator = LogicOperator.NULL;

    public BoundParamEntry() {
    }

    public BoundParamEntry(SqlExpression sqlExpression, List<String> paramPath) {
        this.sqlExpression = sqlExpression;
        this.paramPath = paramPath;
    }

    public SqlExpression getSqlExpression() {
        return sqlExpression;
    }

    public void setSqlExpression(SqlExpression sqlExpression) {
        this.sqlExpression = sqlExpression;
    }

    public List<String> getParamPath() {
        return paramPath;
    }

    public void setParamPath(List<String> paramPath) {
        this.paramPath = paramPath;
    }

    public Object getLiteralValue() {
        return literalValue;
    }

    public void setLiteralValue(Object literalValue) {
        this.literalValue = literalValue;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }
}
