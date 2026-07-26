package com.mybatisgx.template.select;

import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.template.MgxqlWhereHandler;
import com.mybatisgx.template.TemplateHandler;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单表查询模板处理
 *
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectTemplateHandler implements TemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private MgxqlSelectTemplateHandler mgxqlSelectTemplateHandler = new MgxqlSelectTemplateHandler();
    private MgxqlWhereHandler mgxqlWhereHandler = new MgxqlWhereHandler();
    private MgxsqlScanner mgxsqlScanner = new MgxsqlScanner();
    private HavingTemplateHandler havingTemplateHandler = new HavingTemplateHandler();
    private MgxqlOrderByTemplateHandler mgxqlOrderByTemplateHandler = new MgxqlOrderByTemplateHandler();
    private MgxqlGroupByTemplateHandler mgxqlGroupByTemplateHandler = new MgxqlGroupByTemplateHandler();

    public SelectTemplateHandler(MybatisgxConfiguration configuration) {
    }

    @Override
    public String execute(MethodInfo methodInfo) {
        return buildSelectXNode(methodInfo);
    }

    private String buildSelectXNode(MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());

        MapperInfo mapperInfo = methodInfo.getMapperInfo();
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();

        // 设置返回结果集或者返回类型
        if (mgxqlSelectTemplateHandler.hasAggregate(selectStatement)) {
            selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
        } else {
            selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
        }

        // 构建上下文参数
        ColumnEntityRelation fullTree = selectStatement.getMgxqlEntityRelationTree();
        AliasContext aliasContext = AliasContext.build(selectStatement, fullTree);

        // 4.9 整 SELECT 走 mgxsql：各子句拼成完整 mgxsql 子集文本，一次性喂 MgxqlScanner 转 MyBatis XML 动态标签。
        StringBuilder mgxsqlBuilder = new StringBuilder();

        // 构建 SELECT/FROM/JOIN（jsqlparser PlainSelect.toString()，静态 SQL，scanner 原样透传）
        FromClause fromClause = selectStatement.getFromClause();
        if (fromClause != null) {
            PlainSelect plainSelect = mgxqlSelectTemplateHandler.buildSelectSql(selectStatement, aliasContext);
            mgxsqlBuilder.append(plainSelect.toString());
        }

        // 构建 WHERE（MgxqlWhereHandler 产 where[body] 子集文本；逻辑删除作为附加条件纳入 body）
        WhereClause whereClause = selectStatement.getWhereClause();
        String logicDeleteCondition = buildLogicDeleteCondition(mapperInfo.getEntityInfo());
        MgxqlSourceType sourceType = selectStatement.getMgxqlSourceType();
        boolean autoGuard = Boolean.TRUE.equals(methodInfo.getDynamic())
                && (sourceType == MgxqlSourceType.ENTITY || sourceType == MgxqlSourceType.METHOD_NAME);
        String whereSql = mgxqlWhereHandler.renderWhereClause(
                whereClause != null ? whereClause.getRootExpression() : null, aliasContext, logicDeleteCondition, autoGuard);
        if (!whereSql.isEmpty()) {
            mgxsqlBuilder.append(" ").append(whereSql);
        }

        // GROUP BY 子句渲染
        GroupByClause groupByClause = selectStatement.getGroupByClause();
        if (groupByClause != null) {
            mgxsqlBuilder.append(mgxqlGroupByTemplateHandler.execute(groupByClause, aliasContext));
        }

        // HAVING 子句渲染（占位符 #{param} 暂留——scanner 对 HAVING :param 的支持待后续处理，见 docs/mgxql-render-subset-scanner-todo.md）
        HavingExpression havingExpression = selectStatement.getHavingExpression();
        if (havingExpression != null) {
            String havingSql = havingTemplateHandler.execute(havingExpression, aliasContext);
            if (!havingSql.isEmpty()) {
                mgxsqlBuilder.append(havingSql);
            }
        }

        // ORDER BY 子句渲染
        OrderByClause orderByClause = selectStatement.getOrderByClause();
        if (orderByClause != null) {
            mgxsqlBuilder.append(mgxqlOrderByTemplateHandler.execute(orderByClause, aliasContext));
        }

        // LIMIT 子句渲染（拼文本）
        LimitClause limitClause = selectStatement.getLimitClause();
        if (limitClause != null) {
            mgxsqlBuilder.append(buildLimitSql(limitClause));
        }

        // scanner 产出 XML 片段，解析为 XML 节点置入 <select>（mybatisgx 注册链不走 XMLLanguageDriver，
        // <select> 子节点直接作为动态标签，不包 <script> 文本）。
        String selectBodyXml = mgxsqlScanner.process(mgxsqlBuilder.toString());
        Element bodyRoot;
        try {
            Document bodyDoc = DocumentHelper.parseText("<root>" + selectBodyXml + "</root>");
            bodyRoot = bodyDoc.getRootElement();
        } catch (org.dom4j.DocumentException e) {
            throw new MybatisgxException("mgxql 整链渲染：解析 MgxsqlScanner 产出 XML 失败: " + e.getMessage() + " | 原文: " + selectBodyXml);
        }
        // 先复制子节点列表再 detach，避免遍历 bodyRoot.content() 时并发修改（detach 会从原列表移除）
        // scanner 产出既包含 <where>/<if> 等 Element，也包含 SELECT/FROM/ORDER BY/LIMIT 静态 SQL 文本节点。
        java.util.List<org.dom4j.Node> bodyNodes = new java.util.ArrayList<>();
        for (Object child : bodyRoot.content()) {
            if (child instanceof org.dom4j.Node) {
                bodyNodes.add((org.dom4j.Node) child);
            }
        }
        for (org.dom4j.Node child : bodyNodes) {
            selectElement.add(child.detach());
        }

        return document.asXML();
    }

    /**
     * 构建逻辑删除附加条件文本（静态值，纳 WHERE body 子集文本）。
     */
    private String buildLogicDeleteCondition(EntityInfo entityInfo) {
        if (entityInfo == null) {
            return "";
        }
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        if (logicDeleteColumnInfo == null) {
            return "";
        }
        LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
        if (logicDelete == null) {
            return "";
        }
        return " and " + logicDeleteColumnInfo.getDbColumnName() + " = '" + logicDelete.show() + "'";
    }

    /**
     * 构建 LIMIT 子句文本（limit offset, size）。
     */
    private String buildLimitSql(LimitClause limitClause) {
        if (limitClause == null) {
            return "";
        }
        return " limit " + limitClause.getOffset() + ", " + limitClause.getSize();
    }
}
