package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.ConditionExpression;
import com.mybatisgx.dsl.mgxql.model.ConditionNode;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.ConditionOriginType;

import java.util.ArrayList;
import java.util.List;

/**
 * MgxqlStatement到MethodInfo的转换器，将MGXQL的WHERE条件模型转换为ConditionInfo列表
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlStatementToMethodInfoConverter {

    /**
     * 将MgxqlStatement的WHERE条件转换为ConditionInfo列表
     *
     * @param mgxqlStatement     MGXQL语句模型
     * @param conditionOriginType 条件来源类型
     * @return ConditionInfo列表
     */
    public List<ConditionInfo> convert(MgxqlStatement mgxqlStatement, ConditionOriginType conditionOriginType) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        WhereClause whereClause = mgxqlStatement.getWhereClause();
        if (whereClause == null || whereClause.getRootExpression() == null) {
            return conditionInfoList;
        }
        ConditionExpression rootExpression = whereClause.getRootExpression();
        for (int i = 0; i < rootExpression.getNodes().size(); i++) {
            ConditionNode node = rootExpression.getNodes().get(i);
            ConditionInfo conditionInfo = this.convertNode(node, i, conditionOriginType);
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
}
