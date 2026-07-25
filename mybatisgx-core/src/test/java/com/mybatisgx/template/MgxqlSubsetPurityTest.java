package com.mybatisgx.template;

import com.mybatisgx.exception.MybatisgxException;
import org.junit.Test;

/**
 * MGXQL 子集文本纯净性断言测试。
 *
 * @author 薛承城
 * @date 2026/7/23
 */
public class MgxqlSubsetPurityTest {

    @Test
    public void test01_allowsWhitelistedSubset() {
        MgxqlSubsetPurity.assertPure("select * from User where[#[name = :name] #if(:age != null)[and age = :age] #choose[#when(:type == 'vip')[level = :level] #otherwise[status = :status]]] order by id desc");
    }

    @Test(expected = MybatisgxException.class)
    public void test02_rejectsBlacklistDirective() {
        MgxqlSubsetPurity.assertPure("where[#bind[name=x] name = :name]");
    }

    @Test(expected = MybatisgxException.class)
    public void test03_rejectsRawMybatisParameterInNodeBlock() {
        MgxqlSubsetPurity.assertPure("where[#[name = #{name}]]");
    }

    @Test(expected = MybatisgxException.class)
    public void test04_rejectsNestedDynamicGate() {
        MgxqlSubsetPurity.assertPure("where[#if(:x != null)[#[name = :name]]]");
    }
}
