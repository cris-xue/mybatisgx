package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.model.MethodInfo;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 薛承城
 * @date 2026/6/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlWhereTemplateHandlerTest {

    private MgxqlWhereTemplateHandler handler;

    @Before
    public void setUp() {
        handler = new MgxqlWhereTemplateHandler();
    }

    @Test
    public void test01_dynamicMakesNonOptionalConditionWrapped() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(true);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("name"), false);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("@Dynamic should wrap non-optional condition in <if>", xml.contains("<if"));
    }

    @Test
    public void test02_noDynamicNonOptionalNotWrapped() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("name"), false);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse("Without @Dynamic, non-optional condition should not be wrapped", xml.contains("<if"));
    }

    @Test
    public void test03_dynamicSkipsNullOperator() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(true);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildNullCondition("deleted_at", ComparisonOperator.IS_NULL);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse("IS NULL condition should not be wrapped in <if> even with @Dynamic", xml.contains("<if"));
        Assert.assertTrue(xml.contains("is null"));
    }

    @Test
    public void test04_dynamicSkipsIsNotNullOperator() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(true);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildNullCondition("status", ComparisonOperator.IS_NOT_NULL);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse("IS NOT NULL condition should not be wrapped in <if> even with @Dynamic", xml.contains("<if"));
        Assert.assertTrue(xml.contains("is not null"));
    }

    @Test
    public void test05_likeBindKeyHasPrefix() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildLikeCondition("user_name", Arrays.asList("queryEntity", "userName"), ComparisonOperator.LIKE, false);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("LIKE bind name should start with _like_", xml.contains("_like_queryEntity_userName"));
        Assert.assertTrue(xml.contains("like #{_like_queryEntity_userName}"));
    }

    @Test
    public void test06_startingWithBindKeyHasPrefix() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildLikeCondition("name", Arrays.asList("nameLike"), ComparisonOperator.STARTING_WITH, false);
        expression.addNode(node);

        Element result = handler.execute(methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("STARTING_WITH bind name should start with _like_", xml.contains("_like_nameLike"));
    }

    private WhereConditionNode buildSimpleCondition(String fieldName, String opStr, List<String> paramPath, boolean optional) {
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldName(fieldName);
        node.setOptional(optional);

        ComparisonOperator operator = ComparisonOperator.EQ;
        if ("=".equals(opStr)) {
            operator = ComparisonOperator.EQ;
        }

        BoundParam boundParam = new BoundParam();
        boundParam.setOperator(operator);
        boundParam.setKind(ParamKind.SIMPLE);

        BoundParamEntry entry = new BoundParamEntry();
        entry.setSqlExpression(new ConditionColumnExpression(fieldName, null));
        entry.setParamPath(new ArrayList<>(paramPath));
        boundParam.addEntry(entry);

        node.setBoundParam(boundParam);
        return node;
    }

    private WhereConditionNode buildNullCondition(String fieldName, ComparisonOperator operator) {
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldName(fieldName);
        node.setOptional(false);

        BoundParam boundParam = new BoundParam(ParamKind.NULL_TYPE);
        boundParam.setOperator(operator);

        BoundParamEntry entry = new BoundParamEntry();
        entry.setSqlExpression(new ConditionColumnExpression(fieldName, null));
        boundParam.addEntry(entry);

        node.setBoundParam(boundParam);
        return node;
    }

    private WhereConditionNode buildLikeCondition(String fieldName, List<String> paramPath, ComparisonOperator operator, boolean optional) {
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldName(fieldName);
        node.setOptional(optional);

        BoundParam boundParam = new BoundParam();
        boundParam.setOperator(operator);
        boundParam.setKind(ParamKind.SIMPLE);

        BoundParamEntry entry = new BoundParamEntry();
        entry.setSqlExpression(new ConditionColumnExpression(fieldName, null));
        entry.setParamPath(new ArrayList<>(paramPath));
        boundParam.addEntry(entry);

        node.setBoundParam(boundParam);
        return node;
    }
}
