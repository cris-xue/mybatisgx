package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.LimitClause;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * LimitTemplateHandler execute(List, LimitClause) 方法单元测试
 *
 * @author 薛承城
 * @date 2026/6/25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LimitTemplateHandlerLimitClauseTest {

    private LimitTemplateHandler createHandler(String databaseId) {
        MybatisgxConfiguration config = new MybatisgxConfiguration();
        config.setDatabaseId(databaseId);
        return new LimitTemplateHandler(config);
    }

    @Test
    public void test01_mysqlDialect_shouldRender() {
        LimitTemplateHandler handler = createHandler("MySQL");
        List<Object> selectXmlItemList = new ArrayList<>();
        handler.execute(selectXmlItemList, new LimitClause(0, 10));
        Assert.assertEquals(" limit 0, 10", selectXmlItemList.get(0));
    }

    @Test
    public void test02_oracleDialect_shouldRender() {
        LimitTemplateHandler handler = createHandler("Oracle");
        List<Object> selectXmlItemList = new ArrayList<>();
        handler.execute(selectXmlItemList, new LimitClause(0, 10));
        Assert.assertEquals("SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (", selectXmlItemList.get(0));
        Assert.assertEquals(") t WHERE ROWNUM <= 10) WHERE rn > 0", selectXmlItemList.get(1));
    }

    @Test
    public void test03_pgsqlDialect_shouldRender() {
        LimitTemplateHandler handler = createHandler("PostgreSQL");
        List<Object> selectXmlItemList = new ArrayList<>();
        handler.execute(selectXmlItemList, new LimitClause(2, 10));
        Assert.assertEquals(" limit 10 OFFSET 20", selectXmlItemList.get(0));
    }

    @Test(expected = MybatisgxException.class)
    public void test04_unsupportedDialect_shouldThrow() {
        LimitTemplateHandler handler = createHandler("SQLite");
        List<Object> selectXmlItemList = new ArrayList<>();
        handler.execute(selectXmlItemList, new LimitClause(0, 10));
    }
}
