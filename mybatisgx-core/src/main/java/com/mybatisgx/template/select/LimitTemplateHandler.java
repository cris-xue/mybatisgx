package com.mybatisgx.template.select;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.SelectPageInfo;

import java.util.List;

/**
 * 分页模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class LimitTemplateHandler {

    private MybatisgxConfiguration configuration;

    public LimitTemplateHandler(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
    }

    public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
        if ("mysql".equals(configuration.getDatabaseId())) {
            MysqlLimitHandler mysqlLimitHandler = new MysqlLimitHandler();
            mysqlLimitHandler.execute(selectXmlItemList, selectPageInfo);
        }
        if ("oracle".equals(configuration.getDatabaseId())) {
            OracleLimitHandler oracleLimitHandler = new OracleLimitHandler();
            oracleLimitHandler.execute(selectXmlItemList, selectPageInfo);
        }
        if ("pgsql".equals(configuration.getDatabaseId())) {
            PgsqlLimitHandler pgsqlLimitHandler = new PgsqlLimitHandler();
            pgsqlLimitHandler.execute(selectXmlItemList, selectPageInfo);
        }
    }

    public static class MysqlLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s, %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, index * size, size);
            selectXmlItemList.add(limitSqlExpression);
        }
    }

    public static class OracleLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION_START = "SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (";
        private static final String LIMIT_SQL_EXPRESSION_END = ") t WHERE ROWNUM <= %s) WHERE rn > %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            selectXmlItemList.add(0, LIMIT_SQL_EXPRESSION_START);
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpressionEnd = String.format(LIMIT_SQL_EXPRESSION_END, (index + 1) * size, index * size);
            selectXmlItemList.add(limitSqlExpressionEnd);
        }
    }

    public static class PgsqlLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s OFFSET %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, size, index * size);
            selectXmlItemList.add(limitSqlExpression);
        }
    }
}
