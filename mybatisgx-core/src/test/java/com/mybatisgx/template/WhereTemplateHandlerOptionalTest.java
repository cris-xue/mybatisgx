package com.mybatisgx.template;

import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.test.entity.User;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WhereTemplateHandlerOptionalTest {

    private EntityInfo entityInfo;
    private WhereTemplateHandler handler;

    @Before
    public void setUp() {
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        entityInfo = entityInfoHandler.execute(User.class);
        handler = new WhereTemplateHandler();
    }

    @Test
    public void test01_optionalConditionGeneratesIfTag() {
        MethodInfo methodInfo = buildDeleteMethodInfo("deleteByName", "name", true);
        Element result = handler.execute(entityInfo, methodInfo);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue(xml.contains("<if"));
        Assert.assertTrue(xml.contains("test="));
    }

    @Test
    public void test02_nonOptionalConditionNoIfTag() {
        MethodInfo methodInfo = buildDeleteMethodInfo("deleteByName", "name", false);
        Element result = handler.execute(entityInfo, methodInfo);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertFalse(xml.contains("<if"));
    }

    private MethodInfo buildDeleteMethodInfo(String methodName, String columnName, boolean optional) {
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityInfo(entityInfo);
        mapperInfo.setEntityClass(User.class);
        mapperInfo.setIdClass(Long.class);

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMapperInfo(mapperInfo);
        methodInfo.setMethodName(methodName);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.DELETE);

        ColumnInfo columnInfo = entityInfo.getColumnInfo(columnName);

        ConditionInfo conditionInfo = new ConditionInfo(0);
        conditionInfo.setColumnName(columnName);
        conditionInfo.setComparisonOperator(ComparisonOperator.EQ);
        conditionInfo.setColumnInfo(columnInfo);
        conditionInfo.setOptional(optional);
        List<String> paramValueCommonPathItemList = new ArrayList<>();
        paramValueCommonPathItemList.add(columnName);
        conditionInfo.setParamValueCommonPathItemList(paramValueCommonPathItemList);

        MethodParamInfo methodParamInfo = new MethodParamInfo();
        methodParamInfo.setIndex(0);
        methodParamInfo.setType(String.class);
        methodParamInfo.setTypeCategory(TypeCategory.SIMPLE);
        methodParamInfo.setArgName(columnName);
        List<String> argValueCommonPathItemList = new ArrayList<>();
        argValueCommonPathItemList.add(columnName);
        methodParamInfo.setArgValueCommonPathItemList(argValueCommonPathItemList);
        methodParamInfo.setWrapper(true);
        conditionInfo.setMethodParamInfo(methodParamInfo);

        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        conditionInfoList.add(conditionInfo);
        methodInfo.setConditionInfoList(conditionInfoList);

        return methodInfo;
    }
}
