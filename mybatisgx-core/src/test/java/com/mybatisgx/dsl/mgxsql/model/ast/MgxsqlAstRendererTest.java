package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mybatisgx.dsl.mgxsql.MgxsqlAstRenderer;
import com.mybatisgx.dsl.mgxsql.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * mgxsql AST 渲染器单元测试：手工构造 AST，断言渲染输出的 XML 与重构前扫描器逐字一致。
 *
 * @author 薛承城
 * @description mgxsql AST 渲染器测试
 * @date 2026/7/13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlAstRendererTest {

    private MgxsqlAstRenderer renderer;

    @Before
    public void setUp() {
        this.renderer = new MgxsqlAstRenderer();
    }

    private static PassthroughText t(String text) {
        return new PassthroughText(text, 0, 1, 1);
    }

    private static ParamExpr p(String name) {
        return new ParamExpr(name, 0, 1, 1);
    }

    private static List<MgxsqlNode> list(MgxsqlNode... nodes) {
        return new ArrayList<MgxsqlNode>(Arrays.asList(nodes));
    }

    @Test
    public void test01_whereWithNoGuardIf() {
        // select * from t_user where #[name = :name]
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(t("name = "));
        ifUnit.getBody().add(p("name"));
        WhereScope where = new WhereScope(false, 0, 1, 1);
        where.getChildren().add(ifUnit);

        List<MgxsqlNode> root = list(t("select * from t_user "), where);
        Assert.assertEquals(
                "select * from t_user <where><if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(name)\"> name = #{name}</if></where>",
                renderer.render(root));
    }

    @Test
    public void test02_customGuardWithLtGtEscape() {
        // #if(age>2)[salary > :min]
        IfUnit ifUnit = new IfUnit("age>2", 0, 1, 1);
        ifUnit.getBody().add(t("salary > "));
        ifUnit.getBody().add(p("min"));
        Assert.assertEquals(
                "<if test=\"age&gt;2\"> salary &gt; #{min}</if>",
                renderer.render(list(ifUnit)));
    }

    @Test
    public void test03_foreachInClause() {
        // id in :idList
        ForeachUnit foreach = new ForeachUnit("item", "idList", "#{item}", 0, 1, 1);
        Assert.assertEquals(
                "id in <foreach item=\"item\" collection=\"idList\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>",
                renderer.render(list(t("id "), foreach)));
    }

    @Test
    public void test04_chooseWhenOtherwise() {
        ChooseUnit choose = new ChooseUnit(0, 1, 1);
        WhenUnit when = new WhenUnit("type == 'vip'", 0, 1, 1);
        when.getBody().add(t("salary > "));
        when.getBody().add(p("min"));
        choose.getWhens().add(when);
        OtherwiseUnit otherwise = new OtherwiseUnit(0, 1, 1);
        otherwise.getBody().add(t("status = "));
        otherwise.getBody().add(p("status"));
        choose.setOtherwise(otherwise);

        Assert.assertEquals(
                "<choose><when test=\"type == 'vip'\"> salary &gt; #{min}</when><otherwise> status = #{status}</otherwise></choose>",
                renderer.render(list(choose)));
    }

    @Test
    public void test05_bindWithReferenceInConditionBody() {
        // #[name like %:name%]
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(t("name like "));
        ifUnit.getBody().add(new BindUnit("name", "_like_name", "'%' + name + '%'", true, 0, 1, 1));
        Assert.assertEquals(
                "<if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(name)\"> name like <bind name=\"_like_name\" value=\"'%' + name + '%'\"/>#{_like_name}</if>",
                renderer.render(list(ifUnit)));
    }

    @Test
    public void test06_noParamTestIsTrue() {
        // #[1=1] 无参数 → test="true"
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(t("1=1"));
        Assert.assertEquals(
                "<if test=\"true\"> 1=1</if>",
                renderer.render(list(ifUnit)));
    }

    @Test
    public void test07_nestedParamsDoNotBubble() {
        // #[id = :id #[and name = :name]] —— 外层 test 只含 id
        IfUnit inner = new IfUnit(null, 0, 1, 1);
        inner.getBody().add(t("and name = "));
        inner.getBody().add(p("name"));
        IfUnit outer = new IfUnit(null, 0, 1, 1);
        outer.getBody().add(t("id = "));
        outer.getBody().add(p("id"));
        outer.getBody().add(inner);
        Assert.assertEquals(
                "<if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(id)\"> id = #{id}<if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(name)\"> and name = #{name}</if></if>",
                renderer.render(list(outer)));
    }

    @Test
    public void test08_descentScopeScopeContext() {
        // <where> 三标签下沉，子节点 < > 原样
        DescentScope descent = new DescentScope("<where>", "</where>", 0, 1, 1);
        descent.getChildren().add(t("x "));
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(t("a = "));
        ifUnit.getBody().add(p("a"));
        descent.getChildren().add(ifUnit);
        Assert.assertEquals(
                "<where>x <if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(a)\"> a = #{a}</if></where>",
                renderer.render(list(descent)));
    }

    @Test
    public void test09_nestedPropertyPathTest() {
        // :user.name → @com.mybatisgx.utils.ObjectUtils@isNotEmpty(user) and @com.mybatisgx.utils.ObjectUtils@isNotEmpty(user.name)
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(t("x "));
        ifUnit.getBody().add(p("user.name"));
        Assert.assertEquals(
                "<if test=\"@com.mybatisgx.utils.ObjectUtils@isNotEmpty(user) and @com.mybatisgx.utils.ObjectUtils@isNotEmpty(user.name)\"> x #{user.name}</if>",
                renderer.render(list(ifUnit)));
    }

    @Test
    public void test10_guardAndOperatorEscape() {
        // #if(:a > 2 && :a < 18)[x] —— guard 去冒号 + <>&& 转义
        IfUnit ifUnit = new IfUnit(":a > 2 && :a < 18", 0, 1, 1);
        ifUnit.getBody().add(t("x"));
        Assert.assertEquals(
                "<if test=\"a &gt; 2 and a &lt; 18\"> x</if>",
                renderer.render(list(ifUnit)));
    }
}
