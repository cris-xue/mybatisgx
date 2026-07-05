package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.GroupByClause;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.template.select.AliasContext;
import com.mybatisgx.template.select.MgxqlGroupByTemplateHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MGXQL GROUP BY 模板处理器单元测试
 *
 * @author 薛承城
 * @date 2026/6/25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlGroupByTemplateHandlerTest {

    private MgxqlGroupByTemplateHandler handler;

    @Before
    public void setUp() {
        handler = new MgxqlGroupByTemplateHandler();
    }

    private ColumnInfo createColumnInfo(String dbColumnName) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setDbColumnName(dbColumnName);
        return columnInfo;
    }

    @Test
    public void test01_singleField_shouldRender() {
        GroupByClause clause = new GroupByClause();
        FieldReference field = new FieldReference(null, "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addField(field);

        String result = handler.execute(clause);
        Assert.assertEquals(" GROUP BY user_name", result);
    }

    @Test
    public void test02_multipleFields_shouldRender() {
        GroupByClause clause = new GroupByClause();
        FieldReference field1 = new FieldReference(null, "name");
        field1.setColumnInfo(createColumnInfo("user_name"));
        clause.addField(field1);
        FieldReference field2 = new FieldReference(null, "age");
        field2.setColumnInfo(createColumnInfo("user_age"));
        clause.addField(field2);

        String result = handler.execute(clause);
        Assert.assertEquals(" GROUP BY user_name, user_age", result);
    }

    @Test
    public void test03_aliasPrefix_shouldRender() {
        GroupByClause clause = new GroupByClause();
        FieldReference field = new FieldReference("u", "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addField(field);

        String result = handler.execute(clause);
        Assert.assertEquals(" GROUP BY u.user_name", result);
    }

    @Test
    public void test04_aliasWithAliasContext_shouldResolve() {
        GroupByClause clause = new GroupByClause();
        FieldReference field = new FieldReference("u", "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addField(field);

        AliasContext aliasContext = buildTestAliasContext();
        String result = handler.execute(clause, aliasContext);
        Assert.assertEquals(" GROUP BY user_1_1.user_name", result);
    }

    @Test
    public void test05_aliasWithNullAliasContext_shouldUseOriginal() {
        GroupByClause clause = new GroupByClause();
        FieldReference field = new FieldReference("u", "name");
        field.setColumnInfo(createColumnInfo("user_name"));
        clause.addField(field);

        String result = handler.execute(clause, null);
        Assert.assertEquals(" GROUP BY u.user_name", result);
    }

    private AliasContext buildTestAliasContext() {
        SelectStatement selectStatement = new SelectStatement();
        FromClause fromClause = new FromClause();
        FromEntity primaryEntity = new FromEntity();
        primaryEntity.setEntityName("User");
        primaryEntity.setAlias("u");
        EntityInfo userEntityInfo = new EntityInfo.Builder().setTableName("t_user").setClazz(Object.class).build();
        primaryEntity.setEntityInfo(userEntityInfo);
        fromClause.setPrimaryEntity(primaryEntity);
        selectStatement.setFromClause(fromClause);

        ColumnEntityRelation rootRelation = new ColumnEntityRelation();
        rootRelation.setEntityInfo(userEntityInfo);
        rootRelation.setTableNameAlias("user_1_1");

        return AliasContext.build(selectStatement, rootRelation);
    }
}
