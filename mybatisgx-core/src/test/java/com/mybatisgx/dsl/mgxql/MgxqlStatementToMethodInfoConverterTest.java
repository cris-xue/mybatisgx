package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.test.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlStatementToMethodInfoConverterTest {

    private MgxqlSyntaxProcessor processor;
    private MgxqlStatementToMethodInfoConverter converter;
    private EntityInfo entityInfo;

    @Before
    public void setUp() {
        processor = new MgxqlSyntaxProcessor();
        converter = new MgxqlStatementToMethodInfoConverter();
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        entityInfo = entityInfoHandler.execute(User.class);
    }

    private MgxqlStatement parse(String expression) {
        return processor.executeAndCheck(entityInfo, new MethodInfo(), null, ConditionOriginType.STATEMENT_METHOD_NAME, expression);
    }

    private List<ConditionInfo> convert(MgxqlStatement stmt) {
        return converter.convert(stmt, ConditionOriginType.STATEMENT_METHOD_NAME);
    }

    @Test
    public void test01_simpleCondition() {
        MgxqlStatement stmt = parse("delete where name = :name");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        ConditionInfo ci = list.get(0);
        Assert.assertEquals("name", ci.getColumnName());
        Assert.assertEquals(ComparisonOperator.EQ, ci.getComparisonOperator());
        Assert.assertEquals(1, ci.getParamValueCommonPathItemList().size());
        Assert.assertEquals("name", ci.getParamValueCommonPathItemList().get(0));
    }

    @Test
    public void test02_andConditions() {
        MgxqlStatement stmt = parse("delete where name = :name and age > :age");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("name", list.get(0).getColumnName());
        Assert.assertEquals(LogicOperator.NULL, list.get(0).getLogicOperator());
        Assert.assertEquals("age", list.get(1).getColumnName());
        Assert.assertEquals(LogicOperator.AND, list.get(1).getLogicOperator());
        Assert.assertEquals(ComparisonOperator.GT, list.get(1).getComparisonOperator());
    }

    @Test
    public void test03_orConditions() {
        MgxqlStatement stmt = parse("update where name = :name or age > :age");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(LogicOperator.OR, list.get(1).getLogicOperator());
    }

    @Test
    public void test04_nestedBracketConditions() {
        MgxqlStatement stmt = parse("delete where (name = :name or age > :age) and status = :status");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(2, list.size());
        ConditionInfo bracketGroup = list.get(0);
        Assert.assertEquals("(", bracketGroup.getLeftBracket());
        Assert.assertEquals(")", bracketGroup.getRightBracket());
        Assert.assertNotNull(bracketGroup.getConditionInfoList());
        Assert.assertEquals(2, bracketGroup.getConditionInfoList().size());
        Assert.assertEquals("name", bracketGroup.getConditionInfoList().get(0).getColumnName());
        Assert.assertEquals("age", bracketGroup.getConditionInfoList().get(1).getColumnName());
        ConditionInfo statusCondition = list.get(1);
        Assert.assertEquals("status", statusCondition.getColumnName());
        Assert.assertEquals(LogicOperator.AND, statusCondition.getLogicOperator());
    }

    @Test
    public void test05_optionalCondition() {
        MgxqlStatement stmt = parse("update where ?name = :name");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Boolean.TRUE, list.get(0).getOptional());
    }

    @Test
    public void test06_nonOptionalCondition() {
        MgxqlStatement stmt = parse("update where name = :name");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Boolean.FALSE, list.get(0).getOptional());
    }

    @Test
    public void test07_numberLiteralCondition() {
        MgxqlStatement stmt = parse("delete where age > 18");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("age", list.get(0).getColumnName());
        Assert.assertEquals(ComparisonOperator.GT, list.get(0).getComparisonOperator());
    }

    @Test
    public void test08_nestedParamValuePath() {
        MgxqlStatement stmt = parse("update where name = :role.menu.name");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(3, list.get(0).getParamValueCommonPathItemList().size());
        Assert.assertEquals("role", list.get(0).getParamValueCommonPathItemList().get(0));
        Assert.assertEquals("menu", list.get(0).getParamValueCommonPathItemList().get(1));
        Assert.assertEquals("name", list.get(0).getParamValueCommonPathItemList().get(2));
    }

    @Test
    public void test09_notOperator() {
        MgxqlStatement stmt = parse("delete where status not in :statusList");
        List<ConditionInfo> list = convert(stmt);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(ComparisonOperator.IN, list.get(0).getComparisonOperator());
        Assert.assertEquals(ComparisonOperator.NOT, list.get(0).getComparisonNotOperator());
    }
}
