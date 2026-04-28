package com.mybatisgx.model.handler.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.MybatisgxSyntaxProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MethodNameAstHandlerTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    private MybatisgxSyntaxProcessor buildProcessor() {
        return new MybatisgxSyntaxProcessor();
    }

    @Test
    public void test01() {
        // 测试方法名中只有条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        processor.execute(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "findByName");
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);
        ConditionInfo conditionInfo = conditionInfoList.get(0);
        Assert.assertEquals("name", conditionInfo.getColumnName());
    }

    @Test
    public void test02() {
        // 测试方法名中存在多条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        processor.execute(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "findByIdAndNameOrNameEqAndNameLike"
        );

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);

        ConditionInfo conditionInfo1 = conditionInfoList.get(0);
        Assert.assertEquals("id", conditionInfo1.getColumnName());
        Assert.assertEquals("Id", conditionInfo1.getOriginSegment());

        ConditionInfo conditionInfo2 = conditionInfoList.get(1);
        Assert.assertEquals("name", conditionInfo2.getColumnName());
        Assert.assertEquals("Name", conditionInfo2.getOriginSegment());

        ConditionInfo conditionInfo3 = conditionInfoList.get(2);
        Assert.assertEquals("NameEq", conditionInfo3.getOriginSegment());

        ConditionInfo conditionInfo4 = conditionInfoList.get(3);
        Assert.assertEquals("NameLike", conditionInfo4.getOriginSegment());
    }

    @Test
    public void test03() {
        // 测试方法名中的条件在实体中不存在
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        try {
            processor.execute(
                    entityInfo,
                    methodInfo,
                    null,
                    ConditionOriginType.METHOD_NAME,
                    "findByName1"
            );
        } catch (MybatisgxException e) {
            Assert.assertEquals("name1 方法条件字段或者实体条件字段在 com.mybatisgx.model.handler.test.User 实体类中不存在", e.getMessage());
        }
    }

    @Test
    public void test04() {
        // 测试方法名中存在错误语法
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        processor.execute(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "findCustomByName"
        );

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);

        ConditionInfo conditionInfo1 = conditionInfoList.get(0);
        Assert.assertEquals("name", conditionInfo1.getColumnName());
        Assert.assertEquals("Name", conditionInfo1.getOriginSegment());
    }

    @Test
    public void test05() {
        // 测试方法名中存在错误语法
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        try {
            processor.execute(
                    entityInfo,
                    methodInfo,
                    null,
                    ConditionOriginType.METHOD_NAME,
                    "findCustomByNameAndNameEqEq"
            );
        } catch (MybatisgxException e) {
            Assert.assertEquals("findCustomByNameAndNameEqEq 语法错误: extraneous input 'Eq' expecting <EOF>", e.getMessage());
        }
    }

    @Test
    public void test06() {
        // 测试方法名中存在错误语法
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        processor.execute(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "findCustomByNameAnd$NameEq$Eq"
        );

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);

        ConditionInfo conditionInfo1 = conditionInfoList.get(0);
        Assert.assertEquals("name", conditionInfo1.getColumnName());
        Assert.assertEquals("Name", conditionInfo1.getOriginSegment());

        ConditionInfo conditionInfo2 = conditionInfoList.get(1);
        Assert.assertEquals("nameEq", conditionInfo2.getColumnName());
        Assert.assertEquals("$NameEq$Eq", conditionInfo2.getOriginSegment());
    }
}
