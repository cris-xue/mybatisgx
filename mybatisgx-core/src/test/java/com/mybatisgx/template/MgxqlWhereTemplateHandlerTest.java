package com.mybatisgx.template;

import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
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

        Element result = handler.execute(null, methodInfo, expression);
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

        Element result = handler.execute(null, methodInfo, expression);
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

        Element result = handler.execute(null, methodInfo, expression);
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

        Element result = handler.execute(null, methodInfo, expression);
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

        Element result = handler.execute(null, methodInfo, expression);
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

        Element result = handler.execute(null, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("STARTING_WITH bind name should start with _like_", xml.contains("_like_nameLike"));
    }

    @Test
    public void test07_updateAppendsOptimisticLockCondition() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.UPDATE);
        MethodParamInfo entityParamInfo = new MethodParamInfo();
        methodInfo.setEntityParamInfo(entityParamInfo);

        EntityInfo entityInfo = buildEntityInfoWithVersion("version", "version");

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("user", "name"), false);
        expression.addNode(node);

        Element result = handler.execute(entityInfo, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("UPDATE should append version condition", xml.contains("and version = #{version}"));
    }

    @Test
    public void test08_selectDoesNotAppendOptimisticLockCondition() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);

        EntityInfo entityInfo = buildEntityInfoWithVersion("version", "version");

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("queryEntity", "name"), false);
        expression.addNode(node);

        Element result = handler.execute(entityInfo, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse("SELECT should not append version condition", xml.contains("version"));
    }

    @Test
    public void test09_appendsLogicDeleteCondition() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);

        EntityInfo entityInfo = buildEntityInfoWithLogicDelete("is_deleted", "isDeleted", "N");

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("queryEntity", "name"), false);
        expression.addNode(node);

        Element result = handler.execute(entityInfo, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("Should append logic delete condition", xml.contains("and is_deleted = 'N'"));
    }

    @Test
    public void test10_updateAppendsBothOptimisticLockAndLogicDelete() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.UPDATE);
        MethodParamInfo entityParamInfo = new MethodParamInfo();
        methodInfo.setEntityParamInfo(entityParamInfo);

        EntityInfo entityInfo = buildEntityInfoWithVersionAndLogicDelete("version", "version", "is_deleted", "isDeleted", "N");

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("user", "name"), false);
        expression.addNode(node);

        Element result = handler.execute(entityInfo, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("Should append version condition", xml.contains("and version = #{version}"));
        Assert.assertTrue("Should append logic delete condition", xml.contains("and is_deleted = 'N'"));
        int versionIndex = xml.indexOf("and version");
        int logicDeleteIndex = xml.indexOf("and is_deleted");
        Assert.assertTrue("Version should come before logic delete", versionIndex < logicDeleteIndex);
    }

    @Test
    public void test11_nullEntityInfoDoesNotThrow() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDynamic(false);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.UPDATE);

        WhereExpression expression = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = buildSimpleCondition("name", "=", Arrays.asList("name"), false);
        expression.addNode(node);

        Element result = handler.execute(null, methodInfo, expression);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse("Null EntityInfo should not append version", xml.contains("version"));
        Assert.assertFalse("Null EntityInfo should not append logic delete", xml.contains("is_deleted"));
    }

    private EntityInfo buildEntityInfoWithVersion(String javaColumnName, String dbColumnName) {
        EntityInfo entityInfo = new EntityInfo();
        ColumnInfo versionColumn = new ColumnInfo();
        versionColumn.setJavaColumnName(javaColumnName);
        versionColumn.setDbColumnName(dbColumnName);
        versionColumn.setVersion(createMockVersion());
        entityInfo.setVersionColumnInfo(versionColumn);
        return entityInfo;
    }

    private EntityInfo buildEntityInfoWithLogicDelete(String dbColumnName, String javaColumnName, String showValue) {
        EntityInfo entityInfo = new EntityInfo();
        ColumnInfo logicDeleteColumn = new ColumnInfo();
        logicDeleteColumn.setJavaColumnName(javaColumnName);
        logicDeleteColumn.setDbColumnName(dbColumnName);
        logicDeleteColumn.setLogicDelete(createMockLogicDelete(showValue));
        entityInfo.setLogicDeleteColumnInfo(logicDeleteColumn);
        return entityInfo;
    }

    private EntityInfo buildEntityInfoWithVersionAndLogicDelete(
            String versionJavaName, String versionDbName,
            String logicDeleteDbName, String logicDeleteJavaName, String showValue) {
        EntityInfo entityInfo = new EntityInfo();

        ColumnInfo versionColumn = new ColumnInfo();
        versionColumn.setJavaColumnName(versionJavaName);
        versionColumn.setDbColumnName(versionDbName);
        versionColumn.setVersion(createMockVersion());
        entityInfo.setVersionColumnInfo(versionColumn);

        ColumnInfo logicDeleteColumn = new ColumnInfo();
        logicDeleteColumn.setJavaColumnName(logicDeleteJavaName);
        logicDeleteColumn.setDbColumnName(logicDeleteDbName);
        logicDeleteColumn.setLogicDelete(createMockLogicDelete(showValue));
        entityInfo.setLogicDeleteColumnInfo(logicDeleteColumn);

        return entityInfo;
    }

    private com.mybatisgx.annotation.Version createMockVersion() {
        return (com.mybatisgx.annotation.Version) java.lang.reflect.Proxy.newProxyInstance(
                com.mybatisgx.annotation.Version.class.getClassLoader(),
                new Class[]{com.mybatisgx.annotation.Version.class},
                (proxy, method, args) -> {
                    if ("initValue".equals(method.getName())) return 0;
                    if ("increment".equals(method.getName())) return 1;
                    if ("annotationType".equals(method.getName())) return com.mybatisgx.annotation.Version.class;
                    if ("toString".equals(method.getName())) return "@Version";
                    if ("hashCode".equals(method.getName())) return 0;
                    return null;
                });
    }

    private LogicDelete createMockLogicDelete(String showValue) {
        return (LogicDelete) java.lang.reflect.Proxy.newProxyInstance(
                LogicDelete.class.getClassLoader(),
                new Class[]{LogicDelete.class},
                (proxy, method, args) -> {
                    if ("show".equals(method.getName())) return showValue;
                    if ("hide".equals(method.getName())) return "0";
                    if ("annotationType".equals(method.getName())) return LogicDelete.class;
                    if ("toString".equals(method.getName())) return "@LogicDelete";
                    if ("hashCode".equals(method.getName())) return 0;
                    return null;
                });
    }

    private WhereConditionNode buildSimpleCondition(String fieldName, String opStr, List<String> paramPath, boolean optional) {
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, fieldName));
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
        node.setFieldRef(new FieldReference(null, fieldName));
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
        node.setFieldRef(new FieldReference(null, fieldName));
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
