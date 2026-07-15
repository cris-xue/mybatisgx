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
}
