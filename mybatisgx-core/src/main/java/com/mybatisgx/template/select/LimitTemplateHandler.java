package com.mybatisgx.template.select;

import com.mybatisgx.model.SelectPageInfo;

import java.util.List;

/**
 * 分页模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class LimitTemplateHandler {

    public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
        OrderLimitHandler orderLimitHandler = new OrderLimitHandler();
        orderLimitHandler.execute(selectXmlItemList, selectPageInfo);
    }

    public static class MysqlLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s, %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, selectPageInfo.getIndex(), selectPageInfo.getSize());
            selectXmlItemList.add(limitSqlExpression);
        }
    }

    public static class OrderLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION_START = "SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (";
        private static final String LIMIT_SQL_EXPRESSION_END = ") t WHERE ROWNUM <= %s) WHERE rn > %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            selectXmlItemList.add(0, LIMIT_SQL_EXPRESSION_START);
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpressionEnd = String.format(LIMIT_SQL_EXPRESSION_END, index * size, (index - 1) * size);
            selectXmlItemList.add(limitSqlExpressionEnd);
        }
    }
}
