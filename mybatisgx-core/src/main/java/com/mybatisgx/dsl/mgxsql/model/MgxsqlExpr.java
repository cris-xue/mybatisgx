package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql AST Expression 层节点标记接口。
 * <p>ParamExpr、HashParamExpr、DollarParamExpr、LocalVarExpr 实现此接口，
 * 标识其为表达式叶子节点。PassthroughText / SqlText / XmlTagText 不实现此接口。
 *
 * @author 薛承城
 * @description Expression 层标记接口
 * @date 2026/7/25
 */
public interface MgxsqlExpr extends MgxsqlNode {
}
