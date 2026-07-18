package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.List;

import com.mybatisgx.dsl.mgxsql.MgxsqlAstRenderer;
import com.mybatisgx.dsl.mgxsql.model.MgxsqlNode;
import com.mybatisgx.dsl.mgxsql.MgxsqlParser;
import com.mybatisgx.exception.MybatisgxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * mgxsql 解析器单元测试（Phase 2 子集：scope + :param + #{} 透传 + 字符串字面量）。
 * 通过 parse → render 的端到端输出，对齐 MgxsqlScanner 的既有行为（含无边界域的前导空格保留）。
 *
 * @author 薛承城
 * @description mgxsql 解析器测试
 * @date 2026/7/13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlParserTest {

    private MgxsqlParser parser;
    private MgxsqlAstRenderer renderer;

    @Before
    public void setUp() {
        this.parser = new MgxsqlParser();
        this.renderer = new MgxsqlAstRenderer();
    }

    private String pr(String input) {
        List<MgxsqlNode> ast = parser.parse(input);
        return renderer.render(ast);
    }

    @Test
    public void test01_whereWithParam() {
        // 无边界 where：where 后的空格被原样保留（对齐扫描器）
        Assert.assertEquals(
                "select * from t_user <where> id = #{id}</where>",
                pr("select * from t_user where id = :id"));
    }

    @Test
    public void test02_setThenWhere() {
        Assert.assertEquals(
                "update t_user <set> name = #{name} </set><where> id = #{id}</where>",
                pr("update t_user set name = :name where id = :id"));
    }

    @Test
    public void test03_hashBracePassthroughInScope() {
        // #{id} 在范围块原样透传（对齐扫描器）
        Assert.assertEquals(
                "select * from t <where> id = #{id}</where>",
                pr("select * from t where id = #{id}"));
    }

    @Test
    public void test04_boundedWhere() {
        // where[id = :id] 有边界：内容紧跟 [ 之后，无前导空格
        Assert.assertEquals(
                "select * from t <where>id = #{id}</where>",
                pr("select * from t where[id = :id]"));
    }

    @Test
    public void test05_stringLiteralSkipsColonParam() {
        // 'a:b' 字符串字面量内的 : 不触发参数绑定
        Assert.assertEquals(
                "select * from t <where> name = 'a:b' and id = #{id}</where>",
                pr("select * from t where name = 'a:b' and id = :id"));
    }

    @Test
    public void test06_whereClosesOnClauseKeyword() {
        // where 无边界遇 order by 关闭；where 内容前后空格原样保留，</where> 紧贴 order
        Assert.assertEquals(
                "select * from t <where> id = #{id} </where>order by id",
                pr("select * from t where id = :id order by id"));
    }

    @Test
    public void test07_blankInputPassthrough() {
        Assert.assertEquals("   ", pr("   "));
    }

    @Test(expected = MybatisgxException.class)
    public void test08_unclosedBracketReportsError() {
        pr("select * from t where[abc");
    }

    @Test
    public void test09_conditionNodeImplemented() {
        // #[ 条件构造已实现：where #[name = :name] → 自动 isNotEmpty 的 <if>
        Assert.assertEquals(
                "select * from t <where> <if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(name)\"> name = #{name}</if></where>",
                pr("select * from t where #[name = :name]"));
    }

    // ===== #if(expr) / #when(expr) 空圆括号 guard 收紧（mgxsql-if-when-empty-guard-reject） =====

    /**
     * 断言输入在解析期抛 {@link MybatisgxException}，且消息包含期望片段。
     */
    private void expectEmptyGuardError(String input, String expectedSnippet) {
        try {
            pr(input);
            Assert.fail("应抛 MybatisgxException: " + input);
        } catch (MybatisgxException e) {
            Assert.assertTrue("消息应含 [" + expectedSnippet + "]，实际: " + e.getMessage(),
                    e.getMessage().contains(expectedSnippet));
        }
    }

    @Test
    public void test10_ifEmptyGuardScopeLayerRejected() {
        // scope 层 #if()：圆括号空表达式报错
        expectEmptyGuardError("select * from t where #if()[age = :age]", "圆括号内表达式不能为空");
    }

    @Test
    public void test11_ifWhitespaceGuardScopeLayerRejected() {
        // scope 层 #if( ):纯空白与空括号同等报错
        expectEmptyGuardError("select * from t where #if( )[age = :age]", "圆括号内表达式不能为空");
    }

    @Test
    public void test12_ifEmptyGuardBodyLayerRejected() {
        // body 层 #if（嵌套触 parseBodyIf）：#[#if()...] 空表达式报错，两路对称
        expectEmptyGuardError("select * from t where #[#if()[a = :a]]", "圆括号内表达式不能为空");
    }

    @Test
    public void test13_ifWhitespaceGuardBodyLayerRejected() {
        // body 层 #if( ):纯空白同等报错
        expectEmptyGuardError("select * from t where #[#if( )[a = :a]]", "圆括号内表达式不能为空");
    }

    @Test
    public void test14_whenEmptyGuardRejected() {
        // #when():圆括号空表达式报错，消息指出 #when(expr)
        expectEmptyGuardError(
                "select * from t where #choose[#when()[a = :a] #otherwise[b = :b]]",
                "圆括号内表达式不能为空");
    }

    @Test
    public void test15_whenWhitespaceGuardRejected() {
        // #when( ):纯空白与空括号同等报错
        expectEmptyGuardError(
                "select * from t where #choose[#when( )[a = :a] #otherwise[b = :b]]",
                "圆括号内表达式不能为空");
    }

    @Test
    public void test16_ifWithGuardNotRegressed() {
        // 合法用例防回归：#if(:age > 2)[age = :age] 正常产出显式 guard，不退化为自动 guard
        // 显式 guard 走 hasCustomGuard 分支，test="age > 2"（> 在 XML 中实体化），
        // 不应出现自动 guard 的 isNotEmpty。用语义断言规避实体字符判读。
        String out = pr("select * from t where #if(:age > 2)[age = :age]");
        Assert.assertFalse("不应退化为自动 guard isNotEmpty，实际: " + out, out.contains("isNotEmpty"));
        Assert.assertTrue("应包含显式 guard test 属性，实际: " + out, out.contains("test=\"age"));
        Assert.assertTrue("guard 应含 age 与 2 比较结果，实际: " + out,
                out.contains("age") && out.contains("2"));
        Assert.assertTrue("应渲染 #{age}，实际: " + out, out.contains("#{age}"));
        Assert.assertTrue("应含 <if ...></if>，实际: " + out, out.contains("<if ") && out.contains("</if>"));
    }

    @Test
    public void test17_chooseWithWhenNotRegressed() {
        // 合法用例防回归：#when(:x > 0) 正常产出显式 guard（不应为空 guard 残留）
        String out = pr("select * from t where #choose[#when(:x > 0)[a = :a] #otherwise[b = :b]]");
        Assert.assertTrue("应渲染 <when 含显式 test，实际: " + out,
                out.contains("<when ") && out.contains("test=\"x"));
        Assert.assertTrue("when guard 应含 x 与 0，实际: " + out,
                out.contains("x") && out.contains("0"));
        Assert.assertTrue("应含 otherwise 分支，实际: " + out, out.contains("<otherwise"));
        Assert.assertTrue("应含 #{b}，实际: " + out, out.contains("#{b}"));
    }
}
