package com.mybatisgx.template.select;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.test.entity.User;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodReturnInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * SelectTemplateHandler 集成测试：验证 GROUP BY / ORDER BY / LIMIT 子句渲染顺序与空值跳过
 *
 * @author 薛承城
 * @date 2026/6/25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectTemplateHandlerIntegrationTest {

    private static EntityInfo userEntityInfo;
    private static SelectTemplateHandler handler;

    @BeforeClass
    public static void setUpClass() {
        MybatisgxConfiguration config = new MybatisgxConfiguration();
        config.setDatabaseId("MySQL");
        MybatisgxObjectFactory.register(config, null);
        handler = new SelectTemplateHandler(config);
        userEntityInfo = new EntityInfoHandler().execute(User.class);
    }

    private ColumnInfo createColumnInfo(String dbColumnName) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setDbColumnName(dbColumnName);
        return columnInfo;
    }

    private SelectStatement buildCountSelectStatement() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem countItem = new SelectItem();
        countItem.setType(SelectItemType.COUNT);
        countItem.setFieldName("*");
        stmt.addSelectItem(countItem);
        return stmt;
    }

    private MethodInfo buildMethodInfo(SelectStatement stmt) {
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityInfo(userEntityInfo);
        MethodReturnInfo returnInfo = new MethodReturnInfo();
        returnInfo.setTypeName("java.lang.Long");
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMethodName("testMethod");
        methodInfo.setMapperInfo(mapperInfo);
        methodInfo.setMgxqlStatement(stmt);
        methodInfo.setMethodReturnInfo(returnInfo);
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(SqlCommandType.SELECT);
        return methodInfo;
    }

    @Test
    public void test01_fullClauseOrder_shouldRenderInOrder() {
        SelectStatement stmt = buildCountSelectStatement();

        GroupByClause groupBy = new GroupByClause();
        FieldReference groupField = new FieldReference(null, "age");
        groupField.setColumnInfo(createColumnInfo("age"));
        groupBy.addField(groupField);
        stmt.setGroupByClause(groupBy);

        OrderByClause orderBy = new OrderByClause();
        FieldReference orderField = new FieldReference(null, "name");
        orderField.setColumnInfo(createColumnInfo("name"));
        orderBy.addItem(new OrderByItem(orderField, "desc"));
        stmt.setOrderByClause(orderBy);

        stmt.setLimitClause(new LimitClause(0, 10));

        String xml = handler.execute(buildMethodInfo(stmt));

        int groupByPos = xml.indexOf("GROUP BY");
        int orderByPos = xml.indexOf("ORDER BY");
        int limitPos = xml.indexOf("limit");
        Assert.assertTrue("GROUP BY 应存在", groupByPos > 0);
        Assert.assertTrue("ORDER BY 应存在", orderByPos > 0);
        Assert.assertTrue("LIMIT 应存在", limitPos > 0);
        Assert.assertTrue("GROUP BY 应在 ORDER BY 之前", groupByPos < orderByPos);
        Assert.assertTrue("ORDER BY 应在 LIMIT 之前", orderByPos < limitPos);
    }

    @Test
    public void test02_orderByAndLimitOnly_shouldNotRenderGroupBy() {
        SelectStatement stmt = buildCountSelectStatement();

        OrderByClause orderBy = new OrderByClause();
        FieldReference orderField = new FieldReference(null, "name");
        orderField.setColumnInfo(createColumnInfo("name"));
        orderBy.addItem(new OrderByItem(orderField, "desc"));
        stmt.setOrderByClause(orderBy);

        stmt.setLimitClause(new LimitClause(0, 10));

        String xml = handler.execute(buildMethodInfo(stmt));

        Assert.assertTrue("ORDER BY 应存在", xml.contains("ORDER BY"));
        Assert.assertTrue("LIMIT 应存在", xml.contains("limit"));
        Assert.assertFalse("GROUP BY 不应存在", xml.contains("GROUP BY"));
    }

    @Test
    public void test03_noClauses_shouldNotRenderAnyClause() {
        SelectStatement stmt = buildCountSelectStatement();

        String xml = handler.execute(buildMethodInfo(stmt));

        Assert.assertFalse("GROUP BY 不应存在", xml.contains("GROUP BY"));
        Assert.assertFalse("ORDER BY 不应存在", xml.contains("ORDER BY"));
        Assert.assertFalse("LIMIT 不应存在", xml.contains("limit"));
    }
}
