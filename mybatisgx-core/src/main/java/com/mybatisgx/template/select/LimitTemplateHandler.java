package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.LimitClause;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.ext.session.MybatisgxConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页模板处理器
 * @author 薛承城
 * @date 2025/11/22 19:16
 */
public class LimitTemplateHandler {

    private static final Map<String, MethodRowLimitHandler> METHOD_ROW_LIMIT_MAP = new ConcurrentHashMap<>();
    private final MybatisgxConfiguration configuration;

    static {
        register("MySQL", new LimitTemplateHandler.MysqlMethodRowLimitHandler());
        register("MariaDB", new LimitTemplateHandler.MysqlMethodRowLimitHandler());
        register("OceanBase_MySQL", new LimitTemplateHandler.MysqlMethodRowLimitHandler());
        register("SinoDB", new LimitTemplateHandler.MysqlMethodRowLimitHandler());

        register("Oracle", new LimitTemplateHandler.OracleMethodRowLimitHandler());
        register("Dameng", new LimitTemplateHandler.OracleMethodRowLimitHandler());
        register("UXDB", new LimitTemplateHandler.OracleMethodRowLimitHandler());
        register("OceanBase", new LimitTemplateHandler.OracleMethodRowLimitHandler());

        register("PostgreSQL", new LimitTemplateHandler.PgsqlMethodRowLimitHandler());
        register("GaussDB", new LimitTemplateHandler.PgsqlMethodRowLimitHandler());
        register("Vastbase", new LimitTemplateHandler.PgsqlMethodRowLimitHandler());
        register("Kingbase", new LimitTemplateHandler.PgsqlMethodRowLimitHandler());
        register("GBase", new LimitTemplateHandler.PgsqlMethodRowLimitHandler());
    }

    public LimitTemplateHandler(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
    }

    public void execute(List<Object> selectXmlItemList, MethodRowLimitInfo methodRowLimitInfo) {
        String databaseId = this.configuration.getDatabaseId().toLowerCase();
        MethodRowLimitHandler methodRowLimitHandler = METHOD_ROW_LIMIT_MAP.get(databaseId);
        if (methodRowLimitHandler == null) {
            throw new MybatisgxException("Unsupported databaseId '%s', please register MethodRowLimitHandler first.", databaseId);
        }
        methodRowLimitHandler.apply(selectXmlItemList, methodRowLimitInfo);
    }

    /**
     * 从 MGXQL LimitClause 渲染分页 SQL，复用已有方言逻辑。
     * <p>
     * LimitClause 的 offset 是行偏移量（0-based），构造 MethodRowLimitInfo 时标记为 rowOffset=true，
     * 使方言 Handler 直接使用行偏移而不做 offset*size 转换。
     */
    public void execute(List<Object> selectXmlItemList, LimitClause limitClause) {
        MethodRowLimitInfo methodRowLimitInfo = new MethodRowLimitInfo(limitClause.getOffset(), limitClause.getSize(), true);
        this.execute(selectXmlItemList, methodRowLimitInfo);
    }

    private static class MysqlMethodRowLimitHandler implements MethodRowLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s, %s";

        public void apply(List<Object> selectXmlItemList, MethodRowLimitInfo methodRowLimitInfo) {
            Integer offset = methodRowLimitInfo.getOffset();
            Integer size = methodRowLimitInfo.getSize();
            int skipRows = methodRowLimitInfo.isRowOffset() ? offset : offset * size;
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, skipRows, size);
            selectXmlItemList.add(limitSqlExpression);
        }
    }

    private static class OracleMethodRowLimitHandler implements MethodRowLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION_START = "SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (";
        private static final String LIMIT_SQL_EXPRESSION_END = ") t WHERE ROWNUM <= %s) WHERE rn > %s";

        public void apply(List<Object> selectXmlItemList, MethodRowLimitInfo methodRowLimitInfo) {
            selectXmlItemList.add(0, LIMIT_SQL_EXPRESSION_START);
            Integer offset = methodRowLimitInfo.getOffset();
            Integer size = methodRowLimitInfo.getSize();
            int skipRows = methodRowLimitInfo.isRowOffset() ? offset : offset * size;
            String limitSqlExpressionEnd = String.format(LIMIT_SQL_EXPRESSION_END, skipRows + size, skipRows);
            selectXmlItemList.add(limitSqlExpressionEnd);
        }
    }

    private static class PgsqlMethodRowLimitHandler implements MethodRowLimitHandler {

        private static final String LIMIT_SQL_EXPRESSION = " limit %s OFFSET %s";

        public void apply(List<Object> selectXmlItemList, MethodRowLimitInfo methodRowLimitInfo) {
            Integer offset = methodRowLimitInfo.getOffset();
            Integer size = methodRowLimitInfo.getSize();
            int skipRows = methodRowLimitInfo.isRowOffset() ? offset : offset * size;
            String limitSqlExpression = String.format(LIMIT_SQL_EXPRESSION, size, skipRows);
            selectXmlItemList.add(limitSqlExpression);
        }
    }

    public static void register(String databaseId, MethodRowLimitHandler methodRowLimitHandler) {
        METHOD_ROW_LIMIT_MAP.put(databaseId.toLowerCase(), methodRowLimitHandler);
    }
}
