package com.mybatisgx.dsl.mgxsql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlScanner 测试（v2 语法）
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlScannerTest {

    private MgxsqlScanner scanner;

    @Before
    public void setUp() {
        this.scanner = new MgxsqlScanner();
    }

    // ==================== where 转 <where> ====================

    @Test
    public void test01_whereToWhereTag() {
        String input = "select * from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where> 标签", output.contains("<where>"));
        Assert.assertTrue("应包含 </where> 标签", output.contains("</where>"));
        Assert.assertTrue("应保留 id = #{id}", output.contains("id = #{id}"));
    }

    // ==================== set 转 <set> ====================

    @Test
    public void test02_setToSetTag() {
        String input = "update t_user set name = :name where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set> 标签", output.contains("<set>"));
        Assert.assertTrue("应包含 </set> 标签", output.contains("</set>"));
        Assert.assertTrue("应包含 <where> 标签", output.contains("<where>"));
    }

    // ==================== #(sql) 隐式条件 ====================

    @Test
    public void test03_implicitCondition() {
        String input = "select * from t_user where #(name = :name)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if> 标签", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test04_implicitConditionWithFixedCondition() {
        String input = "select * from t_user where #(name = :name) and age = :age";
        String output = this.scanner.process(input);
        Assert.assertTrue("#(name) 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("age = #{age} 不应在 <if> 内", output.contains("age = #{age}"));
    }

    @Test
    public void test05_multipleImplicitConditions() {
        String input = "select * from t_user where #(name = :name) #(or age > :age)";
        String output = this.scanner.process(input);
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertEquals("应有 2 个 <if> 标签", 2, ifCount);
    }

    // ==================== #(expr)(sql) 显式条件 ====================

    @Test
    public void test06_explicitCondition() {
        String input = "select * from t_user where id = :id #(:age > 2 && :age < 18)(or(name like :name and age = :age))";
        String output = this.scanner.process(input);
        Assert.assertTrue("表达式应写入 test（去冒号）", output.contains("age > 2 && age < 18"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
        Assert.assertTrue("条件体中 :age 应转为 #{age}", output.contains("#{age}"));
        Assert.assertTrue("or 应在条件体内", output.contains("or("));
    }

    @Test
    public void test07_explicitConditionWithoutColon() {
        String input = "select * from t_user where #(age > 2)(name = :name)";
        String output = this.scanner.process(input);
        Assert.assertTrue("test 应为 age > 2", output.contains("age > 2"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
    }

    // ==================== #(and/or sql) 连接词前缀 ====================

    @Test
    public void test08_andPrefix() {
        String input = "select * from t_user where id = :id #(and name = :name)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("and 应在条件体内", output.contains("and name = #{name}"));
    }

    @Test
    public void test09_orPrefix() {
        String input = "select * from t_user where id = :id #(or name = :name)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("or 应在条件体内", output.contains("or name = #{name}"));
    }

    // ==================== 首位 #() 不补 and ====================

    @Test
    public void test10_firstConditionNoAnd() {
        String input = "select * from t_user where #(name = :name) and age = :age";
        String output = this.scanner.process(input);
        Assert.assertTrue("首位条件不应有 and 前缀", output.contains("<if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(name)\"> name = #{name}</if>"));
    }

    // ==================== in :list 转 <foreach> ====================

    @Test
    public void test11_simpleInToForeach() {
        String input = "select * from t_user where id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
        Assert.assertTrue("应包含 </foreach>", output.contains("</foreach>"));
    }

    @Test
    public void test12_implicitInCondition() {
        String input = "select * from t_user where #(id in :idList)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("test 应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
    }

    // ==================== 复杂类型 IN ====================

    @Test
    public void test13_complexInToForeach() {
        String input = "select * from t_user where id in (item:idList)=>$item.id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test14_explicitComplexIn() {
        String input = "select * from t_user where #(idList != null)(id in (item:idList)=>$item.id)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if test=\"idList != null\">", output.contains("idList != null"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    // ==================== LIKE 模式 ====================

    @Test
    public void test15_likeBothSides() {
        String input = "select * from t_user where #(name like %:name%)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
        Assert.assertTrue("value 应包含 '%' + name + '%'", output.contains("'%' + name + '%'"));
    }

    @Test
    public void test16_likeRightSide() {
        String input = "select * from t_user where #(name like :name%)";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 name + '%'", output.contains("name + '%'"));
    }

    @Test
    public void test17_likeLeftSide() {
        String input = "select * from t_user where #(name like %:name)";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 '%' + name", output.contains("'%' + name"));
    }

    // ==================== :param → #{param} 参数绑定 ====================

    @Test
    public void test18_paramRefConversion() {
        String input = "select * from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("#{id}"));
    }

    @Test
    public void test19_nestedParamRef() {
        String input = "select * from t_user where user.name = :user.name";
        String output = this.scanner.process(input);
        Assert.assertTrue(":user.name 应转为 #{user.name}", output.contains("#{user.name}"));
    }

    @Test
    public void test20_backwardCompatParamRef() {
        String input = "select * from t_user where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{id} 应原样保留", output.contains("#{id}"));
    }

    @Test
    public void test21_mixedParamRefs() {
        String input = "select * from t_user where id = :id and name = #{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("#{name} 应原样保留", output.contains("name = #{name}"));
    }

    // ==================== XML 标签透传 ====================

    @Test
    public void test22_xmlTagPassthrough() {
        String input = "select * from t_user where id in :idList and <if test=\"id != null\">id = #{id}</if>";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("XML if 标签应原样保留", output.contains("<if test=\"id != null\">id = #{id}</if>"));
    }

    // ==================== 字符串字面量跳过 ====================

    @Test
    public void test23_stringLiteralSkip() {
        String input = "select * from t_user where name = 'where something' and #(code = :code)";
        String output = this.scanner.process(input);
        Assert.assertTrue("字符串内的 where 不触发 <where>", output.contains("'where something'"));
        Assert.assertTrue("#(code) 应生成 <if>", output.contains("<if"));
    }

    // ==================== UPDATE SET ====================

    @Test
    public void test24_updateSet() {
        String input = "update t_user set #(name = :name), #(age = :age) where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("name 应在 <if> 内", output.contains("name = #{name}"));
        Assert.assertTrue("age 应在 <if> 内", output.contains("age = #{age}"));
    }

    // ==================== DELETE ====================

    @Test
    public void test25_delete() {
        String input = "delete from t_user where #(name = :name) or id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("#(name) 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("in 应转 <foreach>", output.contains("<foreach"));
    }

    // ==================== # 后不跟 () 原样输出 ====================

    @Test
    public void test27_hashWithoutParen() {
        String input = "select * from t_user where #name = :name";
        String output = this.scanner.process(input);
        Assert.assertTrue("#name 不应触发条件解析", !output.contains("<if test="));
        Assert.assertTrue("# 应原样输出", output.contains("#"));
    }

    // ==================== 辅助方法 ====================

    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
