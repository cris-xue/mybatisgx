package com.mybatisgx.template;

import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.test.entity.User;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 薛承城
 * @date 2026/6/16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WhereTemplateHandlerLikeBindKeyTest {

    private EntityInfo entityInfo;
    private WhereTemplateHandler handler;

    @Before
    public void setUp() {
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        entityInfo = entityInfoHandler.execute(User.class);
        handler = new WhereTemplateHandler();
    }

    @Test
    public void test01_likeBindKeyHasPrefix() {
        MethodInfo methodInfo = buildLikeMethodInfo("findByNameLike", "name");
        Element result = handler.execute(entityInfo, methodInfo);
        Assert.assertNotNull(result);
        String xml = result.asXML();
        Assert.assertTrue("LIKE bind name should contain _like_ prefix", xml.contains("_like_"));
        Assert.assertTrue("LIKE condition should reference _like_ bind name", xml.contains("like #{_like_"));
    }

    private MethodInfo buildLikeMethodInfo(String methodName, String columnName) {
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityInfo(entityInfo);
        mapperInfo.setEntityClass(User.class);
        mapperInfo.setIdClass(Long.class);

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMapperInfo(mapperInfo);
        methodInfo.setMethodName(methodName);
        methodInfo.setSqlCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        methodInfo.setDynamic(true);

        ColumnInfo columnInfo = entityInfo.getColumnInfo(columnName);

        ConditionInfo conditionInfo = new ConditionInfo(0);
        conditionInfo.setColumnName(columnName);
        conditionInfo.setComparisonOperator(ComparisonOperator.LIKE);
        conditionInfo.setColumnInfo(columnInfo);
        conditionInfo.setOptional(true);
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
