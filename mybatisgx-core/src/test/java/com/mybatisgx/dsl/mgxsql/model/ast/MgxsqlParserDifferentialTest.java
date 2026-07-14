package com.mybatisgx.dsl.mgxsql.model.ast;

import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * mgxsql 解析器差分测试：对一组代表性输入，断言 {@code render(parse(input))} 与重构前
 * {@link MgxsqlScanner#process(String)} 的输出<b>逐字相等</b>。逐字相等是比 contains 断言更强的验证——
 * 若相等，则 MgxsqlScannerTest 的所有 contains 断言对 parser+renderer 也成立。
 *
 * @author 薛承城
 * @description mgxsql 解析器差分测试（对齐扫描器）
 * @date 2026/7/13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlParserDifferentialTest {

    private MgxsqlScanner scanner;
    private MgxsqlParser parser;
    private MgxsqlAstRenderer renderer;

    @Before
    public void setUp() {
        this.scanner = new MgxsqlScanner();
        this.parser = new MgxsqlParser();
        this.renderer = new MgxsqlAstRenderer();
    }

    private void diff(String input) {
        String expected = scanner.process(input);
        String actual = renderer.render(parser.parse(input));
        Assert.assertEquals("差分失败 input=" + input, expected, actual);
    }

    @Test
    public void test01_paramAndWhere() {
        diff("select * from t_user where id = :id");
    }

    @Test
    public void test02_noGuardCondition() {
        diff("select * from t_user where #[name = :name]");
    }

    @Test
    public void test03_noGuardWithFixed() {
        diff("select * from t_user where #[name = :name] and age = :age");
    }

    @Test
    public void test04_noGuardAndPrefix() {
        diff("select * from t_user where id = :id #[and status = :status]");
    }

    @Test
    public void test05_noGuardOrPrefix() {
        diff("select * from t_user where id = :id #[or name = :name]");
    }

    @Test
    public void test06_explicitGuard() {
        diff("select * from t_user where id = :id #(:age > 2 && :age < 18)[or(name like :name and age = :age)]");
    }

    @Test
    public void test07_emptyGuardImplicit() {
        diff("select * from t_user where #()[and status = :status]");
    }

    @Test
    public void test08_likeBothInBody() {
        diff("select * from t_user where #[name like %:name%]");
    }

    @Test
    public void test09_inSimpleInBody() {
        diff("select * from t_user where #[id in :idList]");
    }

    @Test
    public void test10_inParenInBody() {
        diff("select * from t_user where #[id in (:idList)]");
    }

    @Test
    public void test11_inComplexInBody() {
        diff("select * from t_user where #[id in (item:idList)=>$item.id]");
    }

    @Test
    public void test12_inComplexWrappedInBody() {
        diff("select * from t_user where #[id in ((item:idList)=>$item.id)]");
    }

    @Test
    public void test13_inSimpleScope() {
        diff("select * from t_user where id in :idList");
    }

    @Test
    public void test14_setThenWhere() {
        diff("update t_user set name = :name where id = :id");
    }

    @Test
    public void test15_setConditions() {
        diff("update t_user set #[name = :name], #[age = :age] where id = :id");
    }

    @Test
    public void test16_nestedNoBubble() {
        diff("select * from t_user where #[id = :id #[and name = :name]]");
    }

    @Test
    public void test17_choose() {
        diff("select * from t_user where #choose[#when(type == 'vip')[salary > :min] #otherwise[status = :status]]");
    }

    @Test
    public void test18_likeRightInBody() {
        diff("select * from t_user where #[name like :name%]");
    }

    @Test
    public void test19_likeLeftInBody() {
        diff("select * from t_user where #[name like %:name]");
    }

    @Test
    public void test20_deleteOrIn() {
        diff("delete from t_user where #[name = :name] or id in :idList");
    }

    @Test
    public void test21_whereClosesOnClause() {
        diff("select * from t_user where id = :id order by id");
    }

    @Test
    public void test22_boundedWhere() {
        diff("select * from t_user where[id = :id] order by id");
    }

    @Test
    public void test23_hashParamPassthroughInScope() {
        diff("select * from t_user where id = #{id} and name = ${name}");
    }

    @Test
    public void test24_nestedThreeLevels() {
        diff("select * from t_user where #(:type = 1)[#[and category = :category] #(:subType = 2)[#[and tag = :tag]]]");
    }

    @Test
    public void test25_descentWhere() {
        diff("select * from user <where> #[name = :name] </where>");
    }

    @Test
    public void test26_descentTrim() {
        diff("update user <trim prefix=\"SET\" suffixOverrides=\",\"> #[name = :name,] </trim> where id = :id");
    }

    @Test
    public void test27_descentWithPassthroughIf() {
        diff("select * from user <where> <if test=\"status != null\">and status = #{status}</if> </where>");
    }
}
