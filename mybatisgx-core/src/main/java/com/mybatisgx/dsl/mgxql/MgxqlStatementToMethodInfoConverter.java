package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.ConditionExpression;
import com.mybatisgx.dsl.mgxql.model.ConditionNode;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.AggregateFunctionInfo;
import com.mybatisgx.model.EntityRefInfo;
import com.mybatisgx.model.FromInfo;
import com.mybatisgx.model.GroupByFieldInfo;
import com.mybatisgx.model.HavingInfo;
import com.mybatisgx.model.JoinInfo;
import com.mybatisgx.model.MethodMgxqlInfo;
import com.mybatisgx.model.MethodRowLimitInfo;
import com.mybatisgx.model.MgxqlSourceType;
import com.mybatisgx.model.SelectItemInfo;
import com.mybatisgx.model.SelectItemType;
import com.mybatisgx.model.SelectOrderByInfo;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * MgxqlStatement 到 MethodMgxqlInfo 的完整装配器，覆盖 SELECT / FROM / WHERE /
 * GROUP BY / HAVING / ORDER BY / LIMIT 全部子句
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlStatementToMethodInfoConverter {

    /**
     * 装配 MethodMgxqlInfo
     *
     * @param mgxqlStatement MGXQL 语句模型
     * @param sourceType     MGXQL 来源类型
     * @param dsl            原始 DSL 字符串，原样存入 MethodMgxqlInfo.dsl
     * @param expectedCmd    调用方预期的 SqlCommandType，与 stmt.commandType 不一致时抛异常
     * @return MethodMgxqlInfo
     */
    public MethodMgxqlInfo convert(MgxqlStatement mgxqlStatement,
                                   MgxqlSourceType sourceType,
                                   String dsl,
                                   SqlCommandType expectedCmd) {
        if (mgxqlStatement.getCommandType() != expectedCmd) {
            throw new MybatisgxException(
                    "MGXQL 的 commandType(%s) 与预期(%s) 不一致，dsl: %s",
                    mgxqlStatement.getCommandType().name(),
                    expectedCmd.name(),
                    dsl
            );
        }
        MethodMgxqlInfo mgxqlInfo = new MethodMgxqlInfo();
        mgxqlInfo.setMgxqlSourceType(sourceType);
        mgxqlInfo.setSqlCommandType(mgxqlStatement.getCommandType());
        mgxqlInfo.setDsl(dsl);
        mgxqlInfo.setConditionInfoList(this.convertWhereClause(mgxqlStatement));
        mgxqlInfo.setSelectItemInfo(this.convertSelectItems(mgxqlStatement));
        mgxqlInfo.setFromInfo(this.convertFromClause(mgxqlStatement));
        mgxqlInfo.setGroupByInfoList(this.convertGroupByClause(mgxqlStatement));
        mgxqlInfo.setSelectOrderByInfoList(this.convertOrderByClause(mgxqlStatement));
        mgxqlInfo.setMethodRowLimitInfo(this.convertLimitClause(mgxqlStatement));
        mgxqlInfo.setHavingInfoList(this.convertHavingClause(mgxqlStatement));
        return mgxqlInfo;
    }

    private List<ConditionInfo> convertWhereClause(MgxqlStatement mgxqlStatement) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        WhereClause whereClause = mgxqlStatement.getWhereClause();
        if (whereClause == null || whereClause.getRootExpression() == null) {
            return conditionInfoList;
        }
        ConditionExpression rootExpression = whereClause.getRootExpression();
        for (int i = 0; i < rootExpression.getNodes().size(); i++) {
            ConditionNode node = rootExpression.getNodes().get(i);
            ConditionInfo conditionInfo = this.convertNode(node, i, ConditionOriginType.STATEMENT_METHOD_NAME);
            conditionInfoList.add(conditionInfo);
        }
        return conditionInfoList;
    }

    private ConditionInfo convertNode(ConditionNode node, int index, ConditionOriginType conditionOriginType) {
        ConditionInfo conditionInfo = new ConditionInfo(index, conditionOriginType);
        if (node.isNested()) {
            conditionInfo.setLeftBracket(node.getLeftBracket());
            conditionInfo.setRightBracket(node.getRightBracket());
            conditionInfo.setLogicOperator(node.getLogicOperator());
            List<ConditionInfo> subConditionInfoList = new ArrayList<>();
            for (int i = 0; i < node.getSubExpression().getNodes().size(); i++) {
                ConditionNode subNode = node.getSubExpression().getNodes().get(i);
                ConditionInfo subConditionInfo = this.convertNode(subNode, i, conditionOriginType);
                subConditionInfoList.add(subConditionInfo);
            }
            conditionInfo.setConditionInfoList(subConditionInfoList);
        } else {
            conditionInfo.setColumnName(node.getFieldName());
            conditionInfo.setComparisonOperator(node.getOperator());
            conditionInfo.setComparisonNotOperator(node.getNotOperator());
            conditionInfo.setLogicOperator(node.getLogicOperator());
            if (node.getParamValuePath() != null) {
                conditionInfo.setParamValueCommonPathItemList(new ArrayList<>(node.getParamValuePath()));
            }
            conditionInfo.setOptional(node.isOptional());
        }
        return conditionInfo;
    }

    private SelectItemInfo convertSelectItems(MgxqlStatement mgxqlStatement) {
        List<com.mybatisgx.dsl.mgxql.model.SelectItem> selectItems = mgxqlStatement.getSelectItems();
        if (selectItems == null || selectItems.isEmpty()) {
            return null;
        }
        com.mybatisgx.dsl.mgxql.model.SelectItem first = selectItems.get(0);
        SelectItemInfo info = new SelectItemInfo();
        switch (first.getType()) {
            case COLUMN_ALL:
            case COLUMN:
                info.setSelectItemType(SelectItemType.COLUMN);
                break;
            case COUNT:
            case MAX:
            case MIN:
            case AVG:
            case SUM:
                info.setSelectItemType(SelectItemType.COUNT);
                break;
            default:
                break;
        }
        return info;
    }

    private FromInfo convertFromClause(MgxqlStatement mgxqlStatement) {
        com.mybatisgx.dsl.mgxql.model.FromClause fromClause = mgxqlStatement.getFromClause();
        if (fromClause == null) {
            return null;
        }
        FromInfo fromInfo = new FromInfo();
        com.mybatisgx.dsl.mgxql.model.FromEntity primary = fromClause.getPrimaryEntity();
        if (primary != null) {
            fromInfo.setPrimaryEntity(new EntityRefInfo(primary.getEntityName(), primary.getAlias()));
        }
        List<JoinInfo> joins = new ArrayList<>();
        for (com.mybatisgx.dsl.mgxql.model.JoinEntity je : fromClause.getJoinEntities()) {
            joins.add(new JoinInfo(je.getEntityName(), je.getAlias(), je.getJoinType(), je.getOnLeftAlias(), je.getOnRightAlias()));
        }
        fromInfo.setJoins(joins);
        return fromInfo;
    }

    private List<GroupByFieldInfo> convertGroupByClause(MgxqlStatement mgxqlStatement) {
        com.mybatisgx.dsl.mgxql.model.GroupByClause groupByClause = mgxqlStatement.getGroupByClause();
        if (groupByClause == null || groupByClause.getFields() == null || groupByClause.getFields().isEmpty()) {
            return null;
        }
        List<GroupByFieldInfo> list = new ArrayList<>();
        for (com.mybatisgx.dsl.mgxql.model.FieldReference fr : groupByClause.getFields()) {
            list.add(new GroupByFieldInfo(fr.getEntityAlias(), fr.getFieldName()));
        }
        return list;
    }

    private List<SelectOrderByInfo> convertOrderByClause(MgxqlStatement mgxqlStatement) {
        com.mybatisgx.dsl.mgxql.model.OrderByClause orderByClause = mgxqlStatement.getOrderByClause();
        if (orderByClause == null || orderByClause.getItems() == null || orderByClause.getItems().isEmpty()) {
            return null;
        }
        List<SelectOrderByInfo> list = new ArrayList<>();
        for (com.mybatisgx.dsl.mgxql.model.OrderByItem item : orderByClause.getItems()) {
            com.mybatisgx.dsl.mgxql.model.FieldReference field = item.getField();
            String column = field.getEntityAlias() != null && !field.getEntityAlias().isEmpty()
                    ? field.getEntityAlias() + "." + field.getFieldName()
                    : field.getFieldName();
            list.add(new SelectOrderByInfo(column, item.getDirection()));
        }
        return list;
    }

    private MethodRowLimitInfo convertLimitClause(MgxqlStatement mgxqlStatement) {
        com.mybatisgx.dsl.mgxql.model.LimitClause limitClause = mgxqlStatement.getLimitClause();
        if (limitClause == null) {
            return null;
        }
        return new MethodRowLimitInfo(limitClause.getOffset(), limitClause.getSize());
    }

    private List<HavingInfo> convertHavingClause(MgxqlStatement mgxqlStatement) {
        com.mybatisgx.dsl.mgxql.model.HavingClause havingClause = mgxqlStatement.getHavingClause();
        if (havingClause == null || havingClause.getConditions() == null || havingClause.getConditions().isEmpty()) {
            return null;
        }
        List<HavingInfo> list = new ArrayList<>();
        for (com.mybatisgx.dsl.mgxql.model.HavingCondition hc : havingClause.getConditions()) {
            HavingInfo info = new HavingInfo();
            com.mybatisgx.dsl.mgxql.model.SelectItem aggItem = hc.getAggregateFunction();
            if (aggItem != null) {
                GroupByFieldInfo fieldRef = null;
                if (aggItem.getAggregateFieldRef() != null) {
                    fieldRef = new GroupByFieldInfo(
                            aggItem.getAggregateFieldRef().getEntityAlias(),
                            aggItem.getAggregateFieldRef().getFieldName()
                    );
                }
                info.setAggregateFunction(new AggregateFunctionInfo(aggItem.getType(), fieldRef));
            }
            info.setOperator(hc.getOperator());
            if (hc.getParamValuePath() != null) {
                info.setParamValuePath(new ArrayList<>(hc.getParamValuePath()));
            }
            info.setLiteralValue(hc.getHavingValue());
            list.add(info);
        }
        return list;
    }
}
