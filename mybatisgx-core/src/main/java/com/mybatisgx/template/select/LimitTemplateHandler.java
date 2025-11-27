package com.mybatisgx.template.select;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.SelectPageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class LimitTemplateHandler {

    private static final Map<String, AbstractLimitHandler> LIMIT_OBJECT_MAP = new HashMap<>();
    private MybatisgxConfiguration configuration;

    public LimitTemplateHandler(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
        LIMIT_OBJECT_MAP.put("MySQL", new MysqlLimitHandler());
        LIMIT_OBJECT_MAP.put("Oracle", new OracleLimitHandler());
        LIMIT_OBJECT_MAP.put("PostgreSQL", new PgsqlLimitHandler());
    }

    public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
        AbstractLimitHandler abstractLimitHandler = LIMIT_OBJECT_MAP.get(configuration.getDatabaseId());
        abstractLimitHandler.execute(selectXmlItemList, selectPageInfo);
    }

    public static abstract class AbstractLimitHandler {

        abstract void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo);
    }

    public static class MysqlLimitHandler extends AbstractLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s, %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, index * size, size);
            selectXmlItemList.add(limitSqlExpression);
        }
    }

    public static class OracleLimitHandler extends AbstractLimitHandler {

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

    public static class PgsqlLimitHandler extends AbstractLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s OFFSET %s";

        public void execute(List<Object> selectXmlItemList, SelectPageInfo selectPageInfo) {
            Integer index = selectPageInfo.getIndex();
            Integer size = selectPageInfo.getSize();
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, size, index * size);
            selectXmlItemList.add(limitSqlExpression);
        }
    }
}
