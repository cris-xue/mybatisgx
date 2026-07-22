package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.BracketDirectiveNode;
import com.mybatisgx.dsl.mgxql.model.ChooseNode;
import com.mybatisgx.dsl.mgxql.model.CollectionInfo;
import com.mybatisgx.dsl.mgxql.model.ComparisonOperator;
import com.mybatisgx.dsl.mgxql.model.IfDirectiveNode;
import com.mybatisgx.dsl.mgxql.model.LogicOperator;
import com.mybatisgx.dsl.mgxql.model.WhenNode;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.WhereElement;
import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.dsl.mgxql.model.expression.SqlExpression;
import com.mybatisgx.template.select.AliasContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * mgxql → mgxsql 子集文本渲染器（design D2/D4/D5/D7/D9/D11，task 4.1-4.8）。
 * <p>
 * 将 mgxql 已绑定（含 BoundParam + fieldRef.columnInfo）的 {@link WhereExpression} 平面变体树
 * 序列化为 <b>mgxsql 子集文本</b>，交由 mgxsql 消费阶段（{@code MgxsqlScanner}）转 MyBatis XML 动态标签。
 * <p>
 * <b>语义（用户 2026-07-21 澄清）</b>：渲染产出是「半个 SQL」——
 * 左侧字段 <b>必须是数据库列名</b>（Java 字段经 resolveColumnSql 映射，含 tableAlias），
 * 右侧参数用 <b>Java 字段名</b>（paramValuePath 逐字 {@code :p1.p2}，命名空间不重写，design D7）。
 * <p>
 * <b>动态门输出（design D5/D9）</b>：统一用 {@code #[...]} 块族表达 auto-guard 条件体——
 * 首元素（NULL）→ {@code #[ body ]}、AND → {@code #[and body ]}、OR → {@code #[or body ]}；
 * {@link IfDirectiveNode} → {@code #if(guard)[ body ]}；{@link ChooseNode} → {@code #choose[#when(guard)[ body ]+ #otherwise[ body ]?]}。
 * auto-guard 收集（isNotEmpty）归 mgxsql 消费阶段，本渲染器逐字输出 body，不重算 guard（design D5）。
 * <p>
 * <b>列名映射</b>：优先用 {@link com.mybatisgx.dsl.mgxql.model.BoundParamEntry#getSqlExpression()}（绑定产物，
 * ConditionColumnExpression，含 dbColumnName + tableAlias），经 {@link AliasContext#resolveTableAlias(String)}
 * 把 mgxql 别名转 DB 别名；BoundParam 缺失时回退 {@code fieldRef.columnInfo.dbColumnName}。
 *
 * @author 薛承城
 * @date 2026/7/21
 */
public class MgxqlSubsetRenderer {

    /**
     * 渲染 WHERE 表达式为 mgxsql 子集文本（不含 {@code where[...]} 外壳，仅 body）。
     * 调用方按需包裹 {@code where[...]} / {@code set[...]}（task 4.8）。
     *
     * @param expression  WHERE 根表达式（已绑定）
     * @param aliasContext 别名上下文（复合查询 mgxql 别名 → DB 别名），可空
     * @return mgxsql 子集文本（body）
     */
    public String render(WhereExpression expression, AliasContext aliasContext) {
        if (expression == null || expression.getNodes() == null || expression.getNodes().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (WhereElement element : expression.getNodes()) {
            renderElement(sb, element, aliasContext);
        }
        return sb.toString();
    }

    /**
     * 渲染单个 WhereElement：普通条件或动态门变体。
     */
    private void renderElement(StringBuilder sb, WhereElement element, AliasContext aliasContext) {
        if (element.isCondition()) {
            renderCondition(sb, element.asCondition(), aliasContext);
            return;
        }
        // 动态门变体：body 内首元素 logicOperator 已由 block_prefix 设定（design D9），块自身的 logicOperator 作为块前缀连接词
        if (element instanceof BracketDirectiveNode) {
            renderBracket(sb, (BracketDirectiveNode) element, aliasContext);
        } else if (element instanceof IfDirectiveNode) {
            renderIf(sb, (IfDirectiveNode) element, aliasContext);
        } else if (element instanceof ChooseNode) {
            renderChoose(sb, (ChooseNode) element, aliasContext);
        }
    }

    // ==================== 普通条件渲染 ====================

    /**
     * 普通条件渲染（task 4.2/4.6/4.7）：列名 op :param / %:param% / in (:param) / is [not] null。
     * logicOperator 作为条件间连接词（and/or）前缀；首条件（NULL）无前缀。
     */
    private void renderCondition(StringBuilder sb, WhereConditionNode node, AliasContext aliasContext) {
        // 括号分组（design Q2，复用 subExpression 载体）：( body )，内部序列递归渲染
        if (node.isNested()) {
            sb.append(logicPrefix(node.getLogicOperator())).append("(");
            sb.append(render(node.getSubExpression(), aliasContext));
            sb.append(")");
            return;
        }
        // 连接词前缀（design D9）：NULL（首元素）无前缀，AND/OR 产 "and "/"or "
        sb.append(logicPrefix(node.getLogicOperator()));
        ComparisonOperator operator = node.getOperator();
        if (operator != null && operator.isNullComparisonOperator()) {
            // is null / is not null：column is [not] null（无右侧参数）
            sb.append(renderColumn(node, aliasContext)).append(" ").append(operator.getValue());
            return;
        }
        // 有 operator + 右值（参数或数字）
        String column = renderColumn(node, aliasContext);
        if (operator == ComparisonOperator.LIKE
                || operator == ComparisonOperator.STARTING_WITH
                || operator == ComparisonOperator.ENDING_WITH) {
            sb.append(column).append(" like ").append(renderLikeValue(node, operator));
            return;
        }
        if (operator == ComparisonOperator.IN) {
            sb.append(column).append(renderInValue(node));
            return;
        }
        // 关系/通用：column op value
        sb.append(column).append(" ").append(operator.getValue()).append(" ").append(renderRightValue(node));
    }

    /**
     * 普通条件连接词前缀（design D9）：NULL（首元素）→ ""、AND → " and "、OR → " or "（前导空格分隔前条件）。
     */
    private String logicPrefix(LogicOperator logicOperator) {
        if (logicOperator == LogicOperator.AND) {
            return " and ";
        }
        if (logicOperator == LogicOperator.OR) {
            return " or ";
        }
        return "";
    }

    /**
     * 渲染左侧数据库列名（含 tableAlias）。
     */
    private String renderColumn(WhereConditionNode node, AliasContext aliasContext) {
        ConditionColumnExpression colExpr = extractConditionColumn(node);
        if (colExpr != null) {
            String dbAlias = colExpr.getTableAlias();
            if (StringUtils.isNotBlank(dbAlias)) {
                String resolved = aliasContext != null ? aliasContext.resolveTableAlias(dbAlias) : dbAlias;
                return resolved + "." + colExpr.getDbColumnName();
            }
            return colExpr.getDbColumnName();
        }
        // 回退：fieldRef.columnInfo（校验阶段写回）
        if (node.getFieldRef() != null && node.getFieldRef().getColumnInfo() != null) {
            return node.getFieldRef().getColumnInfo().getDbColumnName();
        }
        return node.getFieldName();
    }

    /**
     * 从 BoundParam.entries 取 ConditionColumnExpression（绑定产物）。
     */
    private ConditionColumnExpression extractConditionColumn(WhereConditionNode node) {
        if (node.getBoundParam() == null || node.getBoundParam().getEntries() == null
                || node.getBoundParam().getEntries().isEmpty()) {
            return null;
        }
        SqlExpression expr = node.getBoundParam().getEntries().get(0).getSqlExpression();
        if (expr instanceof ConditionColumnExpression) {
            return (ConditionColumnExpression) expr;
        }
        return null;
    }

    /**
     * 渲染右侧参数值（Java 字段名 :param 或数字字面量）。design D7：paramPath 逐字，命名空间不重写。
     */
    private String renderRightValue(WhereConditionNode node) {
        if (node.getConditionValue() != null) {
            return node.getConditionValue().toString();
        }
        return ":" + StringUtils.join(node.getParamValuePath(), ".");
    }

    /**
     * LIKE 右值（task 4.6）：据算子还原 % 位置。mgxsql 消费阶段自动生成 &lt;bind&gt;。
     */
    private String renderLikeValue(WhereConditionNode node, ComparisonOperator operator) {
        String param = ":" + StringUtils.join(node.getParamValuePath(), ".");
        if (operator == ComparisonOperator.STARTING_WITH) {
            return param + "%";
        }
        if (operator == ComparisonOperator.ENDING_WITH) {
            return "%" + param;
        }
        return "%" + param + "%";
    }

    /**
     * IN 右值（task 4.7/5.4）：
     * <ul>
     *   <li>简单 IN：{@code in (:ids)}。mgxsql 消费阶段自动生成 &lt;foreach&gt;。</li>
     *   <li>复杂 IN（单字段，design D6 task 5.4）：{@code in (item:coll)=>$item.field}。
     *       itemName/collectionName/valueExpr 取自 CollectionInfo（绑定产物 BoundParam.collectionInfo，回退解析阶段 node.collectionInfo）。</li>
     * </ul>
     */
    private String renderInValue(WhereConditionNode node) {
        CollectionInfo collectionInfo = resolveCollectionInfo(node);
        String param = ":" + StringUtils.join(node.getParamValuePath(), ".");
        if (collectionInfo != null && collectionInfo.getValueExpr() != null) {
            // 复杂 IN：in (item:coll)=>$item.field（单字段）。collectionName 取参数名，valueExpr 前置 $。
            String itemName = collectionInfo.getItemName() != null ? collectionInfo.getItemName() : "item";
            return " in (" + itemName + ":" + StringUtils.join(node.getParamValuePath(), ".") + ")=>$" + collectionInfo.getValueExpr();
        }
        return " in (" + param + ")";
    }

    /**
     * 取 IN 集合信息：优先 BoundParam.collectionInfo（绑定产物），回退 node.collectionInfo（解析阶段填）。
     */
    private CollectionInfo resolveCollectionInfo(WhereConditionNode node) {
        if (node.getBoundParam() != null && node.getBoundParam().getCollectionInfo() != null) {
            return node.getBoundParam().getCollectionInfo();
        }
        return node.getCollectionInfo();
    }

    // ==================== 动态门变体渲染 ====================

    /**
     * BracketDirectiveNode（task 4.3）→ {@code #[ body ]}（auto-guard 归 mgxsql，逐字不动，design D5）。
     * 块 logicOperator 作为块连接词前缀：AND → {@code #[and body ]}、OR → {@code #[or body ]}、NULL → {@code #[ body ]}。
     */
    private void renderBracket(StringBuilder sb, BracketDirectiveNode node, AliasContext aliasContext) {
        sb.append("#[").append(blockConnector(node.getLogicOperator()));
        sb.append(render(node.getBody(), aliasContext));
        sb.append("]");
    }

    /**
     * IfDirectiveNode（task 4.4）→ {@code #if(guard)[ body ]}（guard 原样，去冒号/转义由 mgxsql 消费阶段）。
     */
    private void renderIf(StringBuilder sb, IfDirectiveNode node, AliasContext aliasContext) {
        sb.append(blockConnector(node.getLogicOperator())).append("#if(").append(node.getGuard()).append(")[");
        sb.append(render(node.getBody(), aliasContext));
        sb.append("]");
    }

    /**
     * ChooseNode（task 5.1）→ {@code #choose[#when(guard)[ body ]+ #otherwise[ body ]?]}。
     */
    private void renderChoose(StringBuilder sb, ChooseNode node, AliasContext aliasContext) {
        sb.append(blockConnector(node.getLogicOperator())).append("#choose[");
        for (WhenNode when : node.getWhens()) {
            sb.append("#when(").append(when.getGuard()).append(")[");
            sb.append(render(when.getBody(), aliasContext));
            sb.append("] ");
        }
        if (node.getOtherwise() != null) {
            sb.append("#otherwise[");
            sb.append(render(node.getOtherwise(), aliasContext));
            sb.append("]");
        }
        sb.append("]");
    }

    /**
     * 块连接词前缀（design D9）：AND → "and "、OR → "or "、NULL（首元素）→ ""。
     * 用于 #[and body] / #[or body] / if/choose 块的前置连接。
     */
    private String blockConnector(LogicOperator logicOperator) {
        if (logicOperator == LogicOperator.AND) {
            return "and ";
        }
        if (logicOperator == LogicOperator.OR) {
            return "or ";
        }
        return "";
    }
}
