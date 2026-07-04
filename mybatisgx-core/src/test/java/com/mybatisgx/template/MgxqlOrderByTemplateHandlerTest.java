package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.OrderByClause;
import com.mybatisgx.dsl.mgxql.model.OrderByItem;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.template.select.MgxqlOrderByTemplateHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MGXQL ORDER BY 模板处理器单元测试
 *
 * @author 薛承城
 * @date 2026/6/25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlOrderByTemplateHandlerTest {

    private MgxqlOrderByTemplateHandler handler;

    @Before
    public void setUp() {
        handler = new MgxqlOrderByTemplateHandler();
    }

    private ColumnInfo createColumnInfo(String dbColumnName) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setDbColumnName(dbColumnName);
        return columnInfo;
    }

    @Test
    public void test01_singleFieldAsc_shouldRender() {
        OrderByClause clause = new OrderByClause();
        FieldReference field = new FieldReference(null, "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addItem(new OrderByItem(field, "asc"));

        String result = handler.execute(clause);
        Assert.assertEquals(" ORDER BY user_name asc", result);
    }

    @Test
    public void test02_multipleFields_shouldRender() {
        OrderByClause clause = new OrderByClause();
        FieldReference field1 = new FieldReference(null, "name");
        field1.setColumnInfo(createColumnInfo("user_name"));
        clause.addItem(new OrderByItem(field1, "desc"));
        FieldReference field2 = new FieldReference(null, "age");
        field2.setColumnInfo(createColumnInfo("user_age"));
        clause.addItem(new OrderByItem(field2, "asc"));

        String result = handler.execute(clause);
        Assert.assertEquals(" ORDER BY user_name desc, user_age asc", result);
    }

    @Test
    public void test03_aliasPrefix_shouldRender() {
        OrderByClause clause = new OrderByClause();
        FieldReference field = new FieldReference("u", "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addItem(new OrderByItem(field, "desc"));

        String result = handler.execute(clause);
        Assert.assertEquals(" ORDER BY u.user_name desc", result);
    }

    @Test
    public void test04_columnInfoNullFallback_shouldRenderFieldName() {
        OrderByClause clause = new OrderByClause();
        FieldReference field = new FieldReference(null, "name");
        clause.addItem(new OrderByItem(field, "asc"));

        String result = handler.execute(clause);
        Assert.assertEquals(" ORDER BY name asc", result);
    }
}
