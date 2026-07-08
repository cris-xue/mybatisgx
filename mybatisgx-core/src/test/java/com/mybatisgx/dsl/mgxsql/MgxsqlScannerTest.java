package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.exception.MybatisgxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlScanner 测试（v3 语法）
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

    // ==================== 形式2：#(){condition} 隐式条件（原v2 #(condition) 迁移） ====================

    @Test
    public void test03_implicitCondition() {
        String input = "select * from t_user where #(){name = :name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if> 标签", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test04_implicitConditionWithFixedCondition() {
        String input = "select * from t_user where #(){name = :name} and age = :age";
        String output = this.scanner.process(input);
        Assert.assertTrue("#(){name} 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("age = #{age} 不应在 <if> 内", output.contains("age = #{age}"));
    }

    @Test
    public void test05_multipleImplicitConditions() {
        String input = "select * from t_user where #(){name = :name} #(){or age > :age}";
        String output = this.scanner.process(input);
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertEquals("应有 2 个 <if> 标签", 2, ifCount);
    }

    // ==================== 形式2：#(expr){body} 显式条件（原v2 #(expr)(sql) 迁移） ====================

    @Test
    public void test06_explicitCondition() {
        String input = "select * from t_user where id = :id #(:age > 2 && :age < 18){or(name like :name and age = :age)}";
        String output = this.scanner.process(input);
        Assert.assertTrue("表达式应写入 test（去冒号）", output.contains("age > 2 && age < 18"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
        Assert.assertTrue("条件体中 :age 应转为 #{age}", output.contains("#{age}"));
        Assert.assertTrue("or 应在条件体内", output.contains("or("));
    }

    @Test
    public void test07_explicitConditionWithoutColon() {
        String input = "select * from t_user where #(age > 2){name = :name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("test 应为 age > 2", output.contains("age > 2"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
    }

    // ==================== 形式2：#(){and/or condition} 连接词前缀（原v2 #(and/or condition) 迁移） ====================

    @Test
    public void test08_andPrefix() {
        String input = "select * from t_user where id = :id #(){and name = :name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("and 应在条件体内", output.contains("and name = #{name}"));
    }

    @Test
    public void test09_orPrefix() {
        String input = "select * from t_user where id = :id #(){or name = :name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("or 应在条件体内", output.contains("or name = #{name}"));
    }

    // ==================== 形式1：#condition 单条件简写 ====================

    @Test
    public void test10_form1Basic() {
        String input = "select * from t_user where #id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(id)", output.contains("isNotEmpty(id)"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test11_form1LikePattern() {
        String input = "select * from t_user where #name like %:name%";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
    }

    @Test
    public void test12_form1InClause() {
        String input = "select * from t_user where #id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
    }

    @Test
    public void test13_form1StopsAtAnd() {
        String input = "select * from t_user where #id = :id and name = :name";
        String output = this.scanner.process(input);
        // 形式1在遇到独立的 and 关键字时截断，只管 id = :id
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("and name = #{name} 应在 <if> 外", output.contains("and name = #{name}"));
    }

    // ==================== 形式2：#(){condition} 空guard ====================

    @Test
    public void test14_emptyGuardWithAnd() {
        String input = "select * from t_user where #(){and status = :status}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(status)", output.contains("isNotEmpty(status)"));
        Assert.assertTrue("应包含 and status = #{status}", output.contains("and status = #{status}"));
    }

    // ==================== 形式2：#(expr){body} 显式guard ====================

    @Test
    public void test15_explicitGuardWithColonStrip() {
        String input = "select * from t_user where #(:age > 2){age = :age}";
        String output = this.scanner.process(input);
        Assert.assertTrue("guard 应为 age > 2（去冒号）", output.contains("age > 2"));
        Assert.assertTrue("body 应为 age = #{age}", output.contains("age = #{age}"));
    }

    // ==================== 形式2：#() 后无 {} 报语法错误 ====================

    @Test(expected = MybatisgxException.class)
    public void test16_hashParenWithoutBrace() {
        String input = "select * from t_user where #(name = :name)";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test17_hashParenEndOfInput() {
        String input = "select * from t_user where #()";
        this.scanner.process(input);
    }

    // ==================== 形式2：多行 body ====================

    @Test
    public void test18_multilineBody() {
        String input = "select * from t_user where #(){\n    or (name like :name\n        and age = :age)\n}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 isNotEmpty(age)", output.contains("isNotEmpty(age)"));
        Assert.assertTrue("应包含 #{name}", output.contains("#{name}"));
        Assert.assertTrue("应包含 #{age}", output.contains("#{age}"));
    }

    // ==================== 守卫块嵌套 ====================

    @Test
    public void test19_nestedGuardBlocks() {
        String input = "select * from t_user where #(:type = 1){#(){and category = :category} #(:subType = 2){#(){and tag = :tag}}}";
        String output = this.scanner.process(input);
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertTrue("应有至少 3 个 <if> 标签", ifCount >= 3);
        Assert.assertTrue("外层 guard 应包含 type = 1", output.contains("type = 1"));
        Assert.assertTrue("应包含 isNotEmpty(category)", output.contains("isNotEmpty(category)"));
        Assert.assertTrue("应包含 subType = 2", output.contains("subType = 2"));
        Assert.assertTrue("应包含 isNotEmpty(tag)", output.contains("isNotEmpty(tag)"));
    }

    // ==================== in :list 转 <foreach> ====================

    @Test
    public void test20_simpleInToForeach() {
        String input = "select * from t_user where id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
        Assert.assertTrue("应包含 </foreach>", output.contains("</foreach>"));
    }

    @Test
    public void test21_implicitInCondition() {
        String input = "select * from t_user where #(){id in :idList}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("test 应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
    }

    // ==================== 复杂类型 IN ====================

    @Test
    public void test22_complexInToForeach() {
        String input = "select * from t_user where id in (item:idList)=>$item.id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test23_explicitComplexIn() {
        String input = "select * from t_user where #(idList != null){id in (item:idList)=>$item.id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if test=\"idList != null\">", output.contains("idList != null"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    // ==================== LIKE 模式 ====================

    @Test
    public void test24_likeBothSides() {
        String input = "select * from t_user where #(){name like %:name%}";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
        Assert.assertTrue("value 应包含 '%' + name + '%'", output.contains("'%' + name + '%'"));
    }

    @Test
    public void test25_likeRightSide() {
        String input = "select * from t_user where #(){name like :name%}";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 name + '%'", output.contains("name + '%'"));
    }

    @Test
    public void test26_likeLeftSide() {
        String input = "select * from t_user where #(){name like %:name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 '%' + name", output.contains("'%' + name"));
    }

    // ==================== :param → #{param} 参数绑定 ====================

    @Test
    public void test27_paramRefConversion() {
        String input = "select * from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("#{id}"));
    }

    @Test
    public void test28_nestedParamRef() {
        String input = "select * from t_user where user.name = :user.name";
        String output = this.scanner.process(input);
        Assert.assertTrue(":user.name 应转为 #{user.name}", output.contains("#{user.name}"));
    }

    @Test
    public void test29_backwardCompatParamRef() {
        String input = "select * from t_user where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{id} 应原样保留", output.contains("#{id}"));
    }

    @Test
    public void test30_mixedParamRefs() {
        String input = "select * from t_user where id = :id and name = #{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("#{name} 应原样保留", output.contains("name = #{name}"));
    }

    // ==================== XML 标签透传 ====================

    @Test
    public void test31_xmlTagPassthrough() {
        String input = "select * from t_user where id in :idList and <if test=\"id != null\">id = #{id}</if>";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("XML if 标签应原样保留", output.contains("<if test=\"id != null\">id = #{id}</if>"));
    }

    // ==================== 字符串字面量跳过 ====================

    @Test
    public void test32_stringLiteralSkip() {
        String input = "select * from t_user where name = 'where something' and #(){code = :code}";
        String output = this.scanner.process(input);
        Assert.assertTrue("字符串内的 where 不触发 <where>", output.contains("'where something'"));
        Assert.assertTrue("#(){code} 应生成 <if>", output.contains("<if"));
    }

    // ==================== UPDATE SET ====================

    @Test
    public void test33_updateSet() {
        String input = "update t_user set #(){name = :name}, #(){age = :age} where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("name 应在 <if> 内", output.contains("name = #{name}"));
        Assert.assertTrue("age 应在 <if> 内", output.contains("age = #{age}"));
    }

    // ==================== DELETE ====================

    @Test
    public void test34_delete() {
        String input = "delete from t_user where #(){name = :name} or id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("#(){name} 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("in 应转 <foreach>", output.contains("<foreach"));
    }

    // ==================== # 后非法字符报语法错误 ====================

    @Test(expected = MybatisgxException.class)
    public void test35_hashWithNumber() {
        String input = "select * from t_user where #123";
        this.scanner.process(input);
    }

    @Test
    public void test36_hashInNormalDomainPassthrough() {
        String input = "select # from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("NORMAL 域 # 应原样输出", output.contains("#"));
    }

    // ==================== #{param} 原样透传与 # 条件节点共存 ====================

    @Test
    public void test37_hashParamRefWithCondition() {
        String input = "select * from t_user where #id = :id and name = #{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{name} 应原样输出", output.contains("#{name}"));
        Assert.assertTrue("#id = :id 应解析为形式1条件", output.contains("<if"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("and name = #{name} 应在 <if> 外", output.contains("and name = #{name}"));
    }

    // ==================== SET 域 # 后跟标识符（形式1） ====================

    @Test
    public void test38_setForm1Condition() {
        String input = "update t_user set #name = :name where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
    }

    // ==================== 形式2 guard 参数去冒号 ====================

    @Test
    public void test39_guardColonStrip() {
        String input = "select * from t_user where #(:age > 2){age = :age}";
        String output = this.scanner.process(input);
        Assert.assertTrue("guard 应为 age > 2（去冒号）", output.contains("age > 2"));
        Assert.assertTrue("body 应为 age = #{age}（转花括号）", output.contains("age = #{age}"));
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
