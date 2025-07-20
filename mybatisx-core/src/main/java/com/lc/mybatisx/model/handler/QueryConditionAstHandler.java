package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.syntax.query.condition.QueryConditionLexer;
import com.lc.mybatisx.syntax.query.condition.QueryConditionParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条件解析
 *
 * @author ccxuef
 * @date 2025/7/19 20:42
 */
public class QueryConditionAstHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryConditionAstHandler.class);
    private static final Map<String, String> TOKEN_MAP = new HashMap<>();

    static {
        TOKEN_MAP.put("By", "");
        TOKEN_MAP.put("And", "and");
        TOKEN_MAP.put("Or", "or");

        TOKEN_MAP.put("Lt", "<");
        TOKEN_MAP.put("Lteq", "<=");
        TOKEN_MAP.put("Gt", ">");
        TOKEN_MAP.put("Gteq", ">=");
        TOKEN_MAP.put("In", "in");
        TOKEN_MAP.put("Is", "=");
        TOKEN_MAP.put("Eq", "=");
        TOKEN_MAP.put("Not", "<>");
        TOKEN_MAP.put("Like", "like");
        TOKEN_MAP.put("Between", "between");
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo) {
        this.execute(entityInfo, methodInfo, false, methodInfo.getQueryCondition());
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, Boolean conditionEntity, String queryCondition) {
        CharStream charStream = CharStreams.fromString(queryCondition);
        QueryConditionLexer methodNameLexer = new QueryConditionLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        QueryConditionParser methodNameParser = new QueryConditionParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.query_condition_statement();
        this.getTokens(entityInfo, methodInfo, conditionEntity, sqlStatementContext);
    }

    private void getTokens(EntityInfo entityInfo, MethodInfo methodInfo, Boolean conditionEntity, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            if (parseTreeChild instanceof QueryConditionParser.Condition_group_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseConditionGroupClause(entityInfo, conditionEntity, parseTreeChild);
                methodInfo.setConditionInfoList(conditionInfoList);
            } else if (parseTreeChild instanceof QueryConditionParser.EndContext) {
                return;
            } else {
                this.getTokens(entityInfo, methodInfo, conditionEntity, parseTreeChild);
            }
        }
    }

    private List<ConditionInfo> parseConditionGroupClause(EntityInfo entityInfo, Boolean conditionEntity, ParseTree whereClause) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        int childCount = whereClause.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree whereChildItem = whereClause.getChild(i);
            if (whereChildItem instanceof QueryConditionParser.Condition_clauseContext) {
                ConditionInfo conditionInfo = new ConditionInfo();
                conditionInfo.setIndex(i);
                this.parseCondition(entityInfo, conditionInfo, conditionEntity, whereChildItem);
                conditionInfoList.add(conditionInfo);
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
        return conditionInfoList;
    }

    private void parseCondition(EntityInfo entityInfo, ConditionInfo conditionInfo, Boolean conditionEntity, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            if (parseTreeChild instanceof QueryConditionParser.Logic_op_clauseContext) {
                conditionInfo.setConditionEntity(conditionEntity);
                conditionInfo.setOrigin(parseTreeChild.getText());
                conditionInfo.setLogicOp(TOKEN_MAP.get(token));
            } else if (parseTreeChild instanceof QueryConditionParser.Left_bracket_clauseContext) {
                conditionInfo.setLeftBracket("(");
            } else if (parseTreeChild instanceof QueryConditionParser.Right_bracket_clauseContext) {
                conditionInfo.setRightBracket(")");
            } else if (parseTreeChild instanceof QueryConditionParser.Field_condition_op_clauseContext) {
                String javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                conditionInfo.setConditionEntityJavaColumnName(javaColumnName);
                this.parseCondition(entityInfo, conditionInfo, conditionEntity, parseTreeChild);
            } else if (parseTreeChild instanceof QueryConditionParser.Field_clauseContext) {
                String javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                ColumnInfo columnInfo = entityInfo.getColumnInfo(javaColumnName);
                if (columnInfo == null) {
                    throw new RuntimeException("方法条件或者实体中条件与数据库库实体无法对应：" + javaColumnName);
                }
                conditionInfo.setDbColumnName(columnInfo.getDbColumnName());
                conditionInfo.setJavaColumnName(columnInfo.getJavaColumnName());
            } else if (parseTreeChild instanceof QueryConditionParser.Comparison_op_clauseContext) {
                conditionInfo.setComparisonOp(TOKEN_MAP.get(token));
            } else if (parseTreeChild instanceof QueryConditionParser.Condition_group_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseConditionGroupClause(entityInfo, conditionEntity, parseTreeChild);
                ConditionGroupInfo conditionGroupInfo = new ConditionGroupInfo();
                conditionGroupInfo.setConditionInfoList(conditionInfoList);
                conditionInfo.setConditionGroupInfo(conditionGroupInfo);
            } else {
                LOGGER.error("不支持的语法:{} -- {}", parseTree.getText(), token);
                throw new RuntimeException("不支持的语法");
            }
        }
    }
}
