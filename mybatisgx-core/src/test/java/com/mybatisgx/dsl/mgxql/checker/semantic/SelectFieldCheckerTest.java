package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.checker.CheckerContext;
import com.mybatisgx.dsl.mgxql.checker.SelectFieldChecker;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.test.entity.User;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * SELECT字段存在性校验器单元测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectFieldCheckerTest {

    private SelectFieldChecker checker;
    private EntityInfo userEntityInfo;
    private CheckerContext context;

    @Before
    public void setUp() {
        checker = new SelectFieldChecker();
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        userEntityInfo = entityInfoHandler.execute(User.class);
        context = new CheckerContext(userEntityInfo);
    }

    private SelectStatement buildSelectStmt() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);
        return stmt;
    }

    @Test
    public void test01_selectFieldExists_shouldPass() {
        SelectStatement stmt = buildSelectStmt();
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("name");
        stmt.addSelectItem(item);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test02_selectFieldNotExists_shouldError() {
        SelectStatement stmt = buildSelectStmt();
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("nonExistentField");
        stmt.addSelectItem(item);

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("nonExistentField"));
    }

    @Test
    public void test03_orderByFieldExists_shouldPass() {
        SelectStatement stmt = buildSelectStmt();
        OrderByClause orderBy = new OrderByClause();
        orderBy.addItem(new OrderByItem(new FieldReference(null, "name"), null));
        stmt.setOrderByClause(orderBy);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_groupByFieldNotExists_shouldError() {
        SelectStatement stmt = buildSelectStmt();
        GroupByClause groupBy = new GroupByClause();
        groupBy.addField(new FieldReference(null, "nonExistentField"));
        stmt.setGroupByClause(groupBy);

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasError = false;
        for (String error : context.getErrors()) {
            if (error.contains("GROUP BY") && error.contains("nonExistentField")) {
                hasError = true;
            }
        }
        Assert.assertTrue(hasError);
    }

    @Test
    public void test05_selectAggregateFieldExists_shouldPass() {
        SelectStatement stmt = buildSelectStmt();
        SelectItem aggItem = new SelectItem();
        aggItem.setType(SelectItemType.MAX);
        aggItem.setAggregateFieldRef(new FieldReference(null, "age"));
        stmt.addSelectItem(aggItem);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test06_orderByFieldBindsColumnInfo_shouldPass() {
        SelectStatement stmt = buildSelectStmt();
        OrderByClause orderBy = new OrderByClause();
        orderBy.addItem(new OrderByItem(new FieldReference(null, "name"), "desc"));
        stmt.setOrderByClause(orderBy);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
        ColumnInfo boundColumnInfo = stmt.getOrderByClause().getItems().get(0).getField().getColumnInfo();
        Assert.assertNotNull("ORDER BY 字段应绑定 columnInfo", boundColumnInfo);
        Assert.assertEquals(userEntityInfo.getColumnInfo("name").getDbColumnName(), boundColumnInfo.getDbColumnName());
    }

    @Test
    public void test07_groupByFieldBindsColumnInfo_shouldPass() {
        SelectStatement stmt = buildSelectStmt();
        GroupByClause groupBy = new GroupByClause();
        groupBy.addField(new FieldReference(null, "age"));
        stmt.setGroupByClause(groupBy);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
        ColumnInfo boundColumnInfo = stmt.getGroupByClause().getFields().get(0).getColumnInfo();
        Assert.assertNotNull("GROUP BY 字段应绑定 columnInfo", boundColumnInfo);
        Assert.assertEquals(userEntityInfo.getColumnInfo("age").getDbColumnName(), boundColumnInfo.getDbColumnName());
    }

    @Test
    public void test08_orderByFieldNotExists_shouldError() {
        SelectStatement stmt = buildSelectStmt();
        OrderByClause orderBy = new OrderByClause();
        orderBy.addItem(new OrderByItem(new FieldReference(null, "nonExistentField"), null));
        stmt.setOrderByClause(orderBy);

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasError = false;
        for (String error : context.getErrors()) {
            if (error.contains("ORDER BY") && error.contains("nonExistentField")) {
                hasError = true;
            }
        }
        Assert.assertTrue(hasError);
    }
}
