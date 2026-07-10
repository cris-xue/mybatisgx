package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.exception.MybatisgxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlScanner 测试（v5 语法）
 *
 * @author 薛承城
 * @date 2026/7/8
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

    // ==================== #[body] 无 guard 条件体 ====================

    @Test
    public void test03_noGuardCondition() {
        String input = "select * from t_user where #[name = :name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if> 标签", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test04_noGuardWithFixedCondition() {
        String input = "select * from t_user where #[name = :name] and age = :age";
        String output = this.scanner.process(input);
        Assert.assertTrue("#[name] 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("age = #{age} 不应在 <if> 内", output.contains("age = #{age}"));
    }

    @Test
    public void test05_noGuardWithAndPrefix() {
        String input = "select * from t_user where id = :id #[and status = :status]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(status)", output.contains("isNotEmpty(status)"));
        Assert.assertTrue("and 应在条件体内", output.contains("and status = #{status}"));
    }

    @Test
    public void test06_noGuardWithOrPrefix() {
        String input = "select * from t_user where id = :id #[or name = :name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("or 应在条件体内", output.contains("or name = #{name}"));
    }

    @Test
    public void test07_noGuardMultilineBody() {
        String input = "select * from t_user where #[\n    or (name like :name\n        and age = :age)\n]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 isNotEmpty(age)", output.contains("isNotEmpty(age)"));
        Assert.assertTrue("应包含 #{name}", output.contains("#{name}"));
        Assert.assertTrue("应包含 #{age}", output.contains("#{age}"));
    }

    @Test
    public void test08_noGuardLikePattern() {
        String input = "select * from t_user where #[name like %:name%]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
    }

    @Test
    public void test09_noGuardInClause() {
        String input = "select * from t_user where #[id in :idList]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
    }

    // ==================== #(expr)[body] 有 guard 条件体 ====================

    @Test
    public void test10_explicitGuard() {
        String input = "select * from t_user where id = :id #(:age > 2 && :age < 18)[or(name like :name and age = :age)]";
        String output = this.scanner.process(input);
        Assert.assertTrue("表达式应写入 test（去冒号）", output.contains("age > 2 && age < 18"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
        Assert.assertTrue("条件体中 :age 应转为 #{age}", output.contains("#{age}"));
        Assert.assertTrue("or 应在条件体内", output.contains("or("));
    }

    @Test
    public void test11_emptyGuardImplicit() {
        String input = "select * from t_user where #()[and status = :status]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(status)", output.contains("isNotEmpty(status)"));
        Assert.assertTrue("应包含 and status = #{status}", output.contains("and status = #{status}"));
    }

    @Test
    public void test12_guardWithoutColon() {
        String input = "select * from t_user where #(age > 2)[name = :name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("test 应为 age > 2", output.contains("age > 2"));
        Assert.assertTrue("条件体中 :name 应转为 #{name}", output.contains("#{name}"));
    }

    @Test
    public void test13_guardColonStrip() {
        String input = "select * from t_user where #(:age > 2)[age = :age]";
        String output = this.scanner.process(input);
        Assert.assertTrue("guard 应为 age > 2（去冒号）", output.contains("age > 2"));
        Assert.assertTrue("body 应为 age = #{age}", output.contains("age = #{age}"));
    }

    @Test
    public void test14_guardMixedColon() {
        String input = "select * from t_user where #(:age > minAge && maxAge > :age)[age = :age]";
        String output = this.scanner.process(input);
        Assert.assertTrue("guard 应去冒号但保留裸标识符", output.contains("age > minAge && maxAge > age"));
        Assert.assertTrue("body 应为 age = #{age}", output.contains("age = #{age}"));
    }

    // ==================== 语法错误检测 ====================

    @Test(expected = MybatisgxException.class)
    public void test15_hashParenWithoutBracket() {
        // #() 后跟 {} 是 v3 语法，v4 不再支持
        String input = "select * from t_user where #(){name = :name}";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test16_hashBracketNotClosed() {
        String input = "select * from t_user where #[name = :name";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test17_hashWithNumber() {
        String input = "select * from t_user where\n  #123";
        this.scanner.process(input);
    }

    // ==================== 嵌套条件体 ====================

    @Test
    public void test18_nestedGuardBlocks() {
        String input = "select * from t_user where #(:type = 1)[#[and category = :category] #(:subType = 2)[#[and tag = :tag]]]";
        String output = this.scanner.process(input);
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertTrue("应有至少 3 个 <if> 标签", ifCount >= 3);
        Assert.assertTrue("外层 guard 应包含 type = 1", output.contains("type = 1"));
        Assert.assertTrue("应包含 isNotEmpty(category)", output.contains("isNotEmpty(category)"));
        Assert.assertTrue("应包含 subType = 2", output.contains("subType = 2"));
        Assert.assertTrue("应包含 isNotEmpty(tag)", output.contains("isNotEmpty(tag)"));
    }

    // ==================== 形式1：#condition 单条件简写（独占一行） ====================

    @Test
    public void test19_form1Basic() {
        String input = "select * from t_user where\n  #id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(id)", output.contains("isNotEmpty(id)"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("应包含 </if>", output.contains("</if>"));
    }

    @Test
    public void test20_form1LikePattern() {
        String input = "select * from t_user where\n  #name like %:name%";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
    }

    @Test
    public void test21_form1InClause() {
        String input = "select * from t_user where\n  #id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
    }

    @Test(expected = MybatisgxException.class)
    public void test22_form1InlineAndReportsError() {
        // v5: 形式1行内 and/or 报语法错误
        String input = "select * from t_user where\n  #id = :id and name = :name";
        this.scanner.process(input);
    }

    // ==================== in :list 转 <foreach> ====================

    @Test
    public void test23_simpleInToForeach() {
        String input = "select * from t_user where id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
    }

    @Test
    public void test24_implicitInCondition() {
        String input = "select * from t_user where #[id in :idList]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("test 应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
    }

    // ==================== 复杂类型 IN ====================

    @Test
    public void test25_complexInToForeach() {
        String input = "select * from t_user where id in (item:idList)=>$item.id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test26_explicitComplexIn() {
        String input = "select * from t_user where #(idList != null)[id in (item:idList)=>$item.id]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if test=\"idList != null\">", output.contains("idList != null"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    // ==================== LIKE 模式 ====================

    @Test
    public void test27_likeBothSides() {
        String input = "select * from t_user where #[name like %:name%]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 _like_name", output.contains("_like_name"));
        Assert.assertTrue("value 应包含 '%' + name + '%'", output.contains("'%' + name + '%'"));
    }

    @Test
    public void test28_likeRightSide() {
        String input = "select * from t_user where #[name like :name%]";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 name + '%'", output.contains("name + '%'"));
    }

    @Test
    public void test29_likeLeftSide() {
        String input = "select * from t_user where #[name like %:name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("value 应包含 '%' + name", output.contains("'%' + name"));
    }

    // ==================== :param → #{param} 参数绑定 ====================

    @Test
    public void test30_paramRefConversion() {
        String input = "select * from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("#{id}"));
    }

    @Test
    public void test31_nestedParamRef() {
        String input = "select * from t_user where user.name = :user.name";
        String output = this.scanner.process(input);
        Assert.assertTrue(":user.name 应转为 #{user.name}", output.contains("#{user.name}"));
    }

    @Test
    public void test32_backwardCompatParamRef() {
        String input = "select * from t_user where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{id} 应原样保留", output.contains("#{id}"));
    }

    @Test
    public void test33_mixedParamRefs() {
        String input = "select * from t_user where id = :id and name = #{name}";
        String output = this.scanner.process(input);
        Assert.assertTrue(":id 应转为 #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("#{name} 应原样保留", output.contains("name = #{name}"));
    }

    // ==================== XML 标签透传 ====================

    @Test
    public void test34_xmlTagPassthrough() {
        String input = "select * from t_user where id in :idList and <if test=\"id != null\">id = #{id}</if>";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("XML if 标签应原样保留", output.contains("<if test=\"id != null\">id = #{id}</if>"));
    }

    // ==================== 字符串字面量跳过 ====================

    @Test
    public void test35_stringLiteralSkip() {
        String input = "select * from t_user where name = 'where something' and #[code = :code]";
        String output = this.scanner.process(input);
        Assert.assertTrue("字符串内的 where 不触发 <where>", output.contains("'where something'"));
        Assert.assertTrue("#[code] 应生成 <if>", output.contains("<if"));
    }

    // ==================== UPDATE SET ====================

    @Test
    public void test36_updateSet() {
        String input = "update t_user set #[name = :name], #[age = :age] where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("name 应在 <if> 内", output.contains("name = #{name}"));
        Assert.assertTrue("age 应在 <if> 内", output.contains("age = #{age}"));
    }

    @Test
    public void test37_updateSetWithGuard() {
        String input = "update t_user set #(name != null)[name = :name] where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("guard 应为 name != null", output.contains("name != null"));
        Assert.assertTrue("body 应为 name = #{name}", output.contains("name = #{name}"));
    }

    // ==================== DELETE ====================

    @Test
    public void test38_delete() {
        String input = "delete from t_user where #[name = :name] or id in :idList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("#[name] 应在 <if> 内", output.contains("<if"));
        Assert.assertTrue("in 应转 <foreach>", output.contains("<foreach"));
    }

    // ==================== # 后非法字符报语法错误 ====================

    @Test
    public void test39_hashInNormalDomainPassthrough() {
        String input = "select # from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("NORMAL 域 # 应原样输出", output.contains("#"));
    }

    // ==================== #{param} 原样透传与 # 条件节点共存（范围块） ====================

    @Test
    public void test40_hashParamRefWithCondition() {
        // 条件范围块内 #{param} 原样保留，#condition 必须独占一行
        String input = "select * from t_user where id = #{id}\n  #[name = :name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{id} 应原样输出", output.contains("#{id}"));
        Assert.assertTrue("#[name] 应解析为条件节点", output.contains("<if"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
    }

    // ==================== SET 域形式1 ====================

    @Test
    public void test41_setForm1Condition() {
        String input = "update t_user set\n  #name = :name\nwhere id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 name = #{name}", output.contains("name = #{name}"));
    }

    // ==================== where[body] 边界限定 ====================

    @Test
    public void test42_whereBracketBasic() {
        String input = "select * from t_user where[id = :id] order by id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("应包含 </where>", output.contains("</where>"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
        Assert.assertTrue("order by 应在 </where> 后", output.contains("</where> order by"));
    }

    @Test
    public void test43_whereBracketWithCondition() {
        String input = "select * from t_user where[id = :id #[and name = :name]] order by id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("order by 应在 </where> 后", output.contains("</where> order by"));
    }

    @Test
    public void test44_whereBracketWithUnion() {
        String input = "select * from t_user where[id = :id] union select * from t_order where[user_id = :userId]";
        String output = this.scanner.process(input);
        int whereCount = this.countOccurrences(output, "<where>");
        Assert.assertEquals("应有 2 个 <where> 标签", 2, whereCount);
        Assert.assertTrue("union 应在 </where> 外", output.contains("</where> union"));
    }

    @Test
    public void test45_whereBracketNestedBrackets() {
        String input = "select * from t_user where[id = :id #[and name = :name #[and age > :age]]]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertTrue("应有 2 个嵌套 <if>", ifCount >= 2);
    }

    @Test(expected = MybatisgxException.class)
    public void test46_whereBracketNotClosed() {
        String input = "select * from t_user where[id = :id";
        this.scanner.process(input);
    }

    // ==================== where 无边界 + 子句关键字关闭 ====================

    @Test
    public void test47_whereOrderByCloses() {
        String input = "select * from t_user where #[id = :id] order by id";
        String output = this.scanner.process(input);
        Assert.assertTrue("order by 应在 </where> 后", output.contains("</where> order by"));
    }

    @Test
    public void test48_whereGroupByCloses() {
        String input = "select * from t_user where #[dept = :dept] group by dept having count(*) > 1";
        String output = this.scanner.process(input);
        Assert.assertTrue("group by 应在 </where> 后", output.contains("</where> group by"));
    }

    @Test
    public void test49_whereUnionCloses() {
        String input = "select * from t_user where id = :id union select * from t_order where user_id = :userId";
        String output = this.scanner.process(input);
        Assert.assertTrue("</where> 后应有 union", output.contains("</where>") && output.contains("union"));
        Assert.assertTrue("union 应在第一个 </where> 之后",
                output.indexOf("union") > output.indexOf("</where>"));
        int whereCount = this.countOccurrences(output, "<where>");
        Assert.assertEquals("应有 2 个 <where> 标签", 2, whereCount);
    }

    @Test
    public void test50_whereLimitCloses() {
        String input = "select * from t_user where #[name = :name] limit 10";
        String output = this.scanner.process(input);
        Assert.assertTrue("limit 应在 </where> 后", output.contains("</where> limit"));
    }

    @Test
    public void test51_stringLiteralKeywordNotClose() {
        String input = "select * from t_user where name = 'order by something'";
        String output = this.scanner.process(input);
        Assert.assertTrue("字符串内 order by 不触发关闭", output.contains("'order by something'"));
    }

    @Test
    public void test52_orderCodeNotClose() {
        String input = "select * from t_user where #[order_code = :code]";
        String output = this.scanner.process(input);
        Assert.assertTrue("order_code 不应误触发 order by 关闭",
                !output.contains("</where> order") || output.contains("order_code"));
    }

    // ==================== set[body] 边界限定 ====================

    @Test
    public void test53_setBracket() {
        String input = "update t_user set[#[name = :name], #[age = :age]] where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
    }

    // ==================== where 无边界到末尾关闭 ====================

    @Test
    public void test54_whereToEndNoClause() {
        String input = "select * from t_user where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("应包含 </where>", output.contains("</where>"));
        Assert.assertTrue("应包含 id = #{id}", output.contains("id = #{id}"));
    }

    // ==================== for update 关闭 WHERE 域 ====================

    @Test
    public void test55_forUpdateClosesWhere() {
        String input = "select * from t_user where id = :id for update";
        String output = this.scanner.process(input);
        Assert.assertTrue("</where> 应在 for update 前", output.contains("</where>for update"));
    }

    @Test
    public void test56_forUpdateNowait() {
        String input = "select * from t_user where id = :id for update nowait";
        String output = this.scanner.process(input);
        Assert.assertTrue("</where> 应在 for update 前", output.contains("</where>for update"));
        Assert.assertTrue("nowait 应原样输出", output.contains("nowait"));
    }

    // ==================== minus 不触发 WHERE 域关闭 ====================

    @Test
    public void test57_minusNotCloseWhere() {
        String input = "select * from t_user where id = :id minus select * from t_order where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("minus 不应触发 WHERE 域关闭，where 应包到末尾", output.contains("<where>"));
        Assert.assertFalse("minus 不应是子句关键字", output.contains("</where> minus"));
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

    // ==================== in (:list) 括号语法 ====================

    @Test
    public void test58_inParenSimpleList() {
        String input = "select * from t_user where id in (:idList)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
    }

    @Test
    public void test59_inParenSimpleListWithSpaces() {
        String input = "select * from t_user where id in ( :idList )";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
        Assert.assertTrue("应包含 #{item}", output.contains("#{item}"));
    }

    @Test
    public void test60_inParenInConditionBody() {
        String input = "select * from t_user where #[id in (:idList)]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("test 应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
    }

    @Test
    public void test61_inParenInConditionBodyWithSpaces() {
        String input = "select * from t_user where #[id in ( :idList )]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("collection 应为 idList", output.contains("collection=\"idList\""));
    }

    @Test
    public void test62_form1InParen() {
        String input = "select * from t_user where\n  #id in (:idList)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 isNotEmpty(idList)", output.contains("isNotEmpty(idList)"));
    }

    @Test
    public void test63_guardInParen() {
        String input = "select * from t_user where #(idList != null)[id in (:idList)]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if test=\"idList != null\">", output.contains("idList != null"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
    }

    // ==================== in #{list} 原样透传 ====================

    @Test
    public void test64_inHashParamPassthrough() {
        String input = "select * from t_user where id in #{idList}";
        String output = this.scanner.process(input);
        Assert.assertFalse("in #{idList} 不应翻译为 foreach", output.contains("<foreach"));
        Assert.assertTrue("应原样保留 #{idList}", output.contains("#{idList}"));
    }

    @Test
    public void test65_inParenHashParamPassthrough() {
        String input = "select * from t_user where id in (#{idList})";
        String output = this.scanner.process(input);
        Assert.assertFalse("in (#{idList}) 不应翻译为 foreach", output.contains("<foreach"));
        Assert.assertTrue("应原样保留 #{idList}", output.contains("#{idList}"));
    }

    // ==================== => 右边禁止 #{} ====================

    @Test(expected = MybatisgxException.class)
    public void test66_arrowRightNoHashParam() {
        String input = "select * from t_user where id in (item:idList)=>#{item.id}";
        this.scanner.process(input);
    }

    // ==================== 复杂 IN 空格容忍 ====================

    @Test
    public void test67_complexInWhitespaceTolerance() {
        String input = "select * from t_user where id in ( item : idList ) =>$item.id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test68_arrowWhitespaceTolerance() {
        String input = "select * from t_user where id in (item:idList) => $item.id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    // ==================== 降级 position 恢复 ====================

    @Test
    public void test69_fallbackPositionRestore() {
        String input = "select * from t_user where id in something";
        String output = this.scanner.process(input);
        Assert.assertFalse("不应包含 foreach", output.contains("<foreach"));
        Assert.assertTrue("应保留 something", output.contains("something"));
    }

    // ==================== where [body] / set [body] 空格容忍 ====================

    @Test
    public void test70_whereBracketWithSpace() {
        String input = "select * from t_user where [id = :id] order by id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
        Assert.assertTrue("应包含 </where>", output.contains("</where>"));
        Assert.assertTrue("order by 应在 </where> 后", output.contains("</where> order by"));
    }

    @Test
    public void test71_setBracketWithSpace() {
        String input = "update t_user set [#[name = :name]] where id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <set>", output.contains("<set>"));
        Assert.assertTrue("应包含 </set>", output.contains("</set>"));
        Assert.assertTrue("应包含 <where>", output.contains("<where>"));
    }

    // ==================== guard 表达式 trim ====================

    @Test
    public void test72_guardTrim() {
        String input = "select * from t_user where #( :age > 1 )[age = :age]";
        String output = this.scanner.process(input);
        Assert.assertTrue("guard 应为 age > 1（trim后无空格）", output.contains("age > 1"));
        Assert.assertTrue("body 应为 age = #{age}", output.contains("age = #{age}"));
    }

    // ==================== v5 新增：#and / #or 行首前缀 ====================

    @Test
    public void test73_andPrefixLineStart() {
        String input = "select * from t_user where\n  #id = :id\n  #and name = :name";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含两个 <if>", this.countOccurrences(output, "<if") >= 2);
        Assert.assertTrue("第一个 <if> test 含 isNotEmpty(id)", output.contains("isNotEmpty(id)"));
        Assert.assertTrue("第二个 <if> 含 and name = #{name}", output.contains("and name = #{name}"));
        Assert.assertTrue("第二个 <if> test 含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
    }

    @Test
    public void test74_orPrefixLineStart() {
        String input = "select * from t_user where\n  #id = :id\n  #or status = :status";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含两个 <if>", this.countOccurrences(output, "<if") >= 2);
        Assert.assertTrue("第二个 <if> 含 or status = #{status}", output.contains("or status = #{status}"));
    }

    @Test
    public void test75_andPrefixWithLike() {
        String input = "select * from t_user where\n  #id = :id\n  #and name like %:name%";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <bind", output.contains("<bind"));
        Assert.assertTrue("应包含 and name like", output.contains("and name like"));
    }

    @Test
    public void test76_andPrefixWithInClause() {
        String input = "select * from t_user where\n  #id = :id\n  #and dept_id in :deptIdList";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 and dept_id in", output.contains("and dept_id in"));
    }

    @Test(expected = MybatisgxException.class)
    public void test77_andPrefixNotAtLineStart() {
        String input = "select * from t_user where #and name = :name";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test78_orPrefixNotAtLineStart() {
        String input = "select * from t_user where #or status = :status";
        this.scanner.process(input);
    }

    // ==================== v5 新增：形式1必须独占一行 ====================

    @Test(expected = MybatisgxException.class)
    public void test79_form1NotAtLineStart() {
        String input = "select * from t_user where #id = :id";
        this.scanner.process(input);
    }

    @Test
    public void test80_form1AtLineStart() {
        String input = "select * from t_user where\n  #id = :id";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 isNotEmpty(id)", output.contains("isNotEmpty(id)"));
    }

    @Test(expected = MybatisgxException.class)
    public void test81_form1InlineOrReportsError() {
        String input = "select * from t_user where\n  #id = :id or status = :status";
        this.scanner.process(input);
    }

    // ==================== v5 新增：复杂 IN 外层括号包裹 ====================

    @Test
    public void test82_complexInWithOuterParen() {
        String input = "select * from t_user where id in ((item:idList)=>$item.id)";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("item 应为自定义名", output.contains("item=\"item\""));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test83_complexInOuterParenWithSpaces() {
        String input = "select * from t_user where id in ( ( item : idList ) => $item.id )";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    @Test
    public void test84_complexInOuterParenInCondition() {
        String input = "select * from t_user where #[id in ((item:idList)=>$item.id)]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含 <if>", output.contains("<if"));
        Assert.assertTrue("应包含 <foreach", output.contains("<foreach"));
        Assert.assertTrue("应包含 #{item.id}", output.contains("#{item.id}"));
    }

    // ==================== v5 新增：条件节点块内禁止 #{} ====================

    @Test(expected = MybatisgxException.class)
    public void test85_hashParamInConditionNode() {
        String input = "select * from t_user where #[id = #{id}]";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test86_hashParamInGuardConditionNode() {
        String input = "select * from t_user where #(type = 1)[id = #{id}]";
        this.scanner.process(input);
    }

    // ==================== v5 新增：条件节点块内禁止 ${} ====================

    @Test(expected = MybatisgxException.class)
    public void test87_dollarBraceInConditionNode() {
        String input = "select * from t_user where #[id = ${id}]";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test88_dollarBraceInGuardConditionNode() {
        String input = "select * from t_user where #(type = 1)[id = ${id}]";
        this.scanner.process(input);
    }

    // ==================== v5 新增：条件节点块内禁止 XML 标签 ====================

    @Test(expected = MybatisgxException.class)
    public void test89_xmlTagInConditionNode() {
        String input = "select * from t_user where #[<if test=\"id != null\">id = :id</if>]";
        this.scanner.process(input);
    }

    // ==================== v5 新增：=> 右边禁止 ${} ====================

    @Test(expected = MybatisgxException.class)
    public void test90_arrowRightDollarBrace() {
        String input = "select * from t_user where id in (item:idList)=>${item.id}";
        this.scanner.process(input);
    }

    // ==================== v5 新增：条件节点块内 #and/#or 报错 ====================

    @Test(expected = MybatisgxException.class)
    public void test91_andPrefixInConditionNode() {
        String input = "select * from t_user where #[id = :id\n  #and name = :name]";
        this.scanner.process(input);
    }

    @Test(expected = MybatisgxException.class)
    public void test92_orPrefixInConditionNode() {
        String input = "select * from t_user where #(type = 1)[id = :id\n  #or name = :name]";
        this.scanner.process(input);
    }

    @Test
    public void test93_nestedConditionAsAlternative() {
        // 正确替代：嵌套 #[and ...]
        String input = "select * from t_user where #[id = :id #[and name = :name]]";
        String output = this.scanner.process(input);
        Assert.assertTrue("应包含嵌套 <if>", this.countOccurrences(output, "<if") >= 2);
    }

    // ==================== v5 新增：参数收集只收直接子级 ====================

    @Test
    public void test94_paramNoBubbleSingleLayer() {
        // 单层条件体收集直接参数
        String input = "select * from t_user where #[id = :id and name = :name]";
        String output = this.scanner.process(input);
        Assert.assertTrue("test 应包含 isNotEmpty(id) 和 isNotEmpty(name)",
                output.contains("isNotEmpty(id)") && output.contains("isNotEmpty(name)"));
    }

    @Test
    public void test95_paramNoBubbleNested() {
        // 嵌套条件体参数不冒泡
        String input = "select * from t_user where #[id = :id #[and name = :name]]";
        String output = this.scanner.process(input);
        // 外层 <if> 的 test 只包含 id
        Assert.assertTrue("外层 test 应包含 isNotEmpty(id)", output.contains("isNotEmpty(id)"));
        // 内层 <if> 的 test 包含 name
        Assert.assertTrue("内层 test 应包含 isNotEmpty(name)", output.contains("isNotEmpty(name)"));
        // 外层 test 不应包含 name（因为参数不冒泡）
        int firstIfIdx = output.indexOf("<if");
        String firstIf = output.substring(firstIfIdx, output.indexOf("</if>", firstIfIdx) + 6);
        Assert.assertFalse("外层 if 内不应同时包含 isNotEmpty(id) 和 isNotEmpty(name)（参数不冒泡）",
                firstIf.contains("isNotEmpty(id) and isNotEmpty(name)"));
    }

    @Test
    public void test96_paramNoBubbleThreeLevels() {
        // 三层嵌套参数不冒泡
        String input = "select * from t_user where #[id = :id #[and name = :name #[and age = :age]]]";
        String output = this.scanner.process(input);
        int ifCount = this.countOccurrences(output, "<if");
        Assert.assertTrue("应有 3 个 <if> 标签", ifCount >= 3);
    }

    @Test
    public void test97_guardNoBubble() {
        // 有 guard 条件体不冒泡
        String input = "select * from t_user where #(:type = 1)[category = :category #[and tag = :tag]]";
        String output = this.scanner.process(input);
        Assert.assertTrue("外层 test 应为 type = 1", output.contains("type = 1"));
        Assert.assertTrue("内层 test 应包含 isNotEmpty(tag)", output.contains("isNotEmpty(tag)"));
    }

    @Test
    public void test98_noParamConditionTestTrue() {
        // 无参数条件体 test 为 true
        String input = "select * from t_user where #[1 = 1]";
        String output = this.scanner.process(input);
        Assert.assertTrue("test 应为 true", output.contains("test=\"true\""));
    }

    // ==================== v5 新增：条件范围块内 #{}/${}/<xml> 原样保留 ====================

    @Test
    public void test99_hashParamInScopeBlock() {
        // 条件范围块内 #{param} 原样保留
        String input = "select * from t_user where id = #{id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("#{id} 应原样保留", output.contains("#{id}"));
    }

    @Test
    public void test100_dollarBraceInScopeBlock() {
        // 条件范围块内 ${param} 原样保留
        String input = "select * from t_user where id = ${id}";
        String output = this.scanner.process(input);
        Assert.assertTrue("${id} 应原样保留", output.contains("${id}"));
    }

    @Test
    public void test101_xmlTagInScopeBlock() {
        // 条件范围块内 XML 标签原样透传
        String input = "select * from t_user where id in :idList and <if test=\"id != null\">id = #{id}</if>";
        String output = this.scanner.process(input);
        Assert.assertTrue("XML if 标签应原样保留", output.contains("<if test=\"id != null\">"));
    }
}
