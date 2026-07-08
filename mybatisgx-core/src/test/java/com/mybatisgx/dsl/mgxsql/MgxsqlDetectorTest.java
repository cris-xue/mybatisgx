package com.mybatisgx.dsl.mgxsql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlDetector 测试（v2 语法）
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlDetectorTest {

    @Test
    public void test01_detectConditionNode() {
        Assert.assertTrue("应检测到 #( 条件节点", MgxsqlDetector.containsMgxsqlSyntax("select * from t_user where #(name = :name)"));
    }

    @Test
    public void test02_detectExprCondition() {
        Assert.assertTrue("应检测到 #(expr) 显式条件", MgxsqlDetector.containsMgxsqlSyntax("where #(age > 2)(name = :name)"));
    }

    @Test
    public void test03_detectInParam() {
        Assert.assertTrue("应检测到 in :", MgxsqlDetector.containsMgxsqlSyntax("where id in :idList"));
    }

    @Test
    public void test04_detectLikePattern() {
        Assert.assertTrue("应检测到 %:", MgxsqlDetector.containsMgxsqlSyntax("where name like %:name%"));
    }

    @Test
    public void test05_detectParamRef() {
        Assert.assertTrue("应检测到 :param", MgxsqlDetector.containsMgxsqlSyntax("where id = :id"));
    }

    @Test
    public void test06_noMgxsqlSyntax() {
        Assert.assertFalse("标准 MyBatis XML 不应检测到 mgxsql", MgxsqlDetector.containsMgxsqlSyntax("<if test=\"name != null\">and name = #{name}</if>"));
    }

    @Test
    public void test07_paramInString() {
        Assert.assertFalse("字符串内的 : 不应触发", MgxsqlDetector.containsMgxsqlSyntax("where name = ':value'"));
    }

    @Test
    public void test08_doubleColonNotTrigger() {
        Assert.assertFalse(":: 不应触发", MgxsqlDetector.containsMgxsqlSyntax("where id = 1::integer"));
    }

    @Test
    public void test09_blankInput() {
        Assert.assertFalse("空白输入不应触发", MgxsqlDetector.containsMgxsqlSyntax(""));
        Assert.assertFalse("null 不应触发", MgxsqlDetector.containsMgxsqlSyntax(null));
    }

    @Test
    public void test10_oldQuestionMarkNotTrigger() {
        Assert.assertFalse("? 语法已废弃，不应触发", MgxsqlDetector.containsMgxsqlSyntax("where ?name = #{name}"));
    }
}
