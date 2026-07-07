package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.S2Condition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlScanner 测试
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
        String input = "select * from t_user where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where> 标签", output.contains("<where>"));
        Assert.assertTrue("应包含 </where> 标签", output.contains("</where>"));
        Assert.assertTrue("应保留 id = #{id}", output.contains("id = #{id}"));
    }

    // ==================== set 转 <set> ====================

    @Test
    public void test02_setToSetTag() {
        String input = "update t_user set name = #{name} where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set> 标签", output.contains("<set>"));
        Assert.assertTrue("应包含 </set> 标签", output.contains("</set>"));
        Assert.assertTrue("应包含 <where> 标签", output.contains("<where>"));
    }

    // ==================== ?条件 转 <if> ====================

    @Test
    public void test03_optionalSimpleCondition() {
        String input = "select * from t_user where ?name = #{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if> 标签", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test04_optionalWithFixedCondition() {
        String input = "select * from t_user where ?name = #{name} and age = #{age}";
        String output = this.scanner.process(input);
        Assert.assertTrue("?name 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("age = #{age} 不应在 <if> 内", output.contains("age = #{age}"));
    }

    @Test
    public void test05_multipleOptionalConditions() {
        String input = "select * from t_user where ?name = #{name} or ?age > #{age}";
        String output = this.scanner.process(input);
        // 应有两个 <if> 标签
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertEquals("应有 2 个 <if> 标签", 2, ifCount);
    }

    // ==================== ?(expr)(condition) 转 <if test="expr"> ====================

    @Test
    public void test06_exprCondition() {
        String input = "select * from t_user where id = #{id} or ?(age > 2 && age < 18)(name like #{name} and age = #{age})";
        String output = this.scanner.process(input);
        Assert.assertTrue("表达式应原样写入 test", output.contains("age > 2 && age < 18"));
        Assert.assertTrue("条件体应在括号内", output.contains("(name like #{name} and age = #{age})"));
        Assert.assertTrue("or 应在 <if> 内", output.contains(" or ("));
    }

    // ==================== in #{list} 转 <foreach> ====================

    @Test
    public void test07_simpleInToForeach() {
        String input = "select * from t_user where id in #{idList}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
        Assert.assertTrue("应包含 </foreach>", output.contains("</foreach>"));
    }

    @Test
    public void test08_optionalIn() {
        String input = "select * from t_user where ?id in #{idList}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
    }

    // ==================== 复杂类型 IN ====================

    @Test
    public void test09_complexInToForeach() {
        String input = "select * from t_user where id in (item:idList)=>#{item.id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test10_optionalComplexIn() {
        String input = "select * from t_user where ?(idList != null)(id in (item:idList)=>#{item.id})";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if test=\"idList != null\">", output.contains("idList != null"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
    }

    // ==================== LIKE 模式 ====================

    @Test
    public void test11_likeBothSides() {
        String input = "select * from t_user where ?name like %#{name}%";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
        Assert.assertTrue("value 应包含 '%' + name + '%'", output.contains("'%' + name + '%'"));
    }

    @Test
    public void test12_likeRightSide() {
        String input = "select * from t_user where ?name like #{name}%";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 name + '%'", output.contains("name + '%'"));
    }

    @Test
    public void test13_likeLeftSide() {
        String input = "select * from t_user where ?name like %#{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 '%' + name", output.contains("'%' + name"));
    }

    // ==================== XML 标签透传 ====================

    @Test
    public void test14_xmlTagPassthrough() {
        String input = "select * from t_user where id in #{idList} and <if test=\"id != null\">id = #{id}</if>";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("XML if 标签应原样保留", output.contains("<if test=\"id != null\">id = #{id}</if>"));
    }

    // ==================== 字符串字面量跳过 ====================

    @Test
    public void test15_stringLiteralSkip() {
        String input = "select * from t_user where name = 'where something' and ?code = #{code}";
        String output = this.scanner.process(input);
        // 字符串内的 where 不应触发 <where>
        Assert.assertTrue("字符串内的 where 不触发 <where>", output.contains("'where something'"));
        Assert.assertTrue("?code 应生成 <if>", output.contains("<if"));
    }

    // ==================== UPDATE SET ====================

    @Test
    public void test16_updateSet() {
        String input = "update t_user set ?name = #{name}, ?age = #{age} where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("name 应在 <if> 内", output.contains("name = #{name}"));
        Assert.assertTrue("age 应在 <if> 内", output.contains("age = #{age}"));
    }

    // ==================== DELETE ====================

    @Test
    public void test17_delete() {
        String input = "delete from t_user where ?name = #{name} or id in #{idList}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("?name 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("in 应转 <foreach>", output.contains("<foreach"));
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
