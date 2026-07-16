package com.mybatisgx.dsl.mgxsql.model.ast;

import com.mybatisgx.dsl.mgxsql.model.*;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * mgxsql AST 节点模型单元测试：手工构造典型语句的 AST，断言父子结构与三层（Scope/Unit/Expression）分类。
 *
 * @author 薛承城
 * @description mgxsql AST 模型测试
 * @date 2026/7/13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlAstModelTest {

    // ==================== Scope + Unit + Expression 三层组合 ====================

    @Test
    public void test01_whereScopeWithIfAndForeach() {
        // 对应：where #[name = :name] and id in :idList
        IfUnit ifUnit = new IfUnit(null, 0, 1, 1);
        ifUnit.getBody().add(new PassthroughText("name = ", 0, 1, 1));
        ifUnit.getBody().add(new ParamExpr("name", 0, 1, 1));

        ForeachUnit foreach = new ForeachUnit("item", "idList", "#{item}", 0, 1, 1);

        WhereScope where = new WhereScope(false, 0, 1, 1);
        where.getChildren().add(ifUnit);
        where.getChildren().add(new PassthroughText(" and id in ", 0, 1, 1));
        where.getChildren().add(foreach);

        // 层级分类：WhereScope(Scope) -> IfUnit/ForeachUnit(Unit) -> ParamExpr(Expression)
        Assert.assertEquals("WhereScope 含 3 个子节点", 3, where.getChildren().size());
        Assert.assertTrue("子[0] 是 IfUnit（Unit 层）", where.getChildren().get(0) instanceof IfUnit);
        Assert.assertTrue("子[2] 是 ForeachUnit（Unit 层）", where.getChildren().get(2) instanceof ForeachUnit);
        Assert.assertFalse("无 guard 的 IfUnit 走自动 isNotEmpty", ifUnit.hasCustomGuard());
        Assert.assertEquals("IfUnit body 含 2 个 Expression 片段", 2, ifUnit.getBody().size());
        Assert.assertTrue("IfUnit body[1] 是 ParamExpr（Expression 层）", ifUnit.getBody().get(1) instanceof ParamExpr);
        Assert.assertEquals("ForeachUnit collection", "idList", foreach.getCollectionName());
        Assert.assertEquals("ForeachUnit valueExpr", "#{item}", foreach.getValueExpr());
    }

    // ==================== 嵌套条件 + choose + 自定义 guard ====================

    @Test
    public void test02_nestedIfChooseAndCustomGuard() {
        // #if(type == 1)[#[and category = :category]]
        IfUnit inner = new IfUnit(null, 0, 1, 1);
        inner.getBody().add(new PassthroughText("and category = ", 0, 1, 1));
        inner.getBody().add(new ParamExpr("category", 0, 1, 1));

        IfUnit outer = new IfUnit("type == 1", 0, 1, 1);
        outer.getBody().add(inner);

        Assert.assertTrue("外层有自定义 guard", outer.hasCustomGuard());
        Assert.assertEquals("外层 guard 原文", "type == 1", outer.getGuardExpression());
        Assert.assertFalse("内层无自定义 guard", inner.hasCustomGuard());
        Assert.assertSame("嵌套父子关系：外层 body[0] 是内层", inner, outer.getBody().get(0));

        // #choose[#when(type=='vip')[salary > :min] #otherwise[status = :status]]
        WhenUnit when = new WhenUnit("type == 'vip'", 0, 1, 1);
        when.getBody().add(new PassthroughText("salary > ", 0, 1, 1));
        when.getBody().add(new ParamExpr("min", 0, 1, 1));

        ChooseUnit choose = new ChooseUnit(0, 1, 1);
        choose.getWhens().add(when);
        OtherwiseUnit otherwise = new OtherwiseUnit(0, 1, 1);
        otherwise.getBody().add(new PassthroughText("status = ", 0, 1, 1));
        otherwise.getBody().add(new ParamExpr("status", 0, 1, 1));
        choose.setOtherwise(otherwise);

        Assert.assertEquals("choose 含 1 个 when", 1, choose.getWhens().size());
        Assert.assertNotNull("choose 含 otherwise", choose.getOtherwise());
        Assert.assertEquals("when guard 原文", "type == 'vip'", choose.getWhens().get(0).getGuardExpression());
    }

    // ==================== 三标签下沉 + bind + 原生参数表达式 ====================

    @Test
    public void test03_descentScopeBindAndNativeExpressions() {
        DescentScope descent = new DescentScope("<trim prefix=\"SET\" suffixOverrides=\",\">", "</trim>", 0, 1, 1);
        descent.getChildren().add(new BindUnit("name", "_like_name", "'%' + name + '%'", true, 0, 1, 1));
        descent.getChildren().add(new HashParamExpr("id", 0, 1, 1));
        descent.getChildren().add(new DollarParamExpr("table", 0, 1, 1));
        descent.getChildren().add(new LocalVarExpr("item.id", 0, 1, 1));

        Assert.assertEquals("DescentScope openTag 透传", "<trim prefix=\"SET\" suffixOverrides=\",\">", descent.getOpenTag());
        Assert.assertEquals("DescentScope closeTag 透传", "</trim>", descent.getCloseTag());
        Assert.assertTrue("DescentScope 属于 Scope 层", descent instanceof MgxsqlNode);

        BindUnit bind = (BindUnit) descent.getChildren().get(0);
        Assert.assertTrue("bind 需追加 #{bindName} 引用", bind.isEmitReference());
        Assert.assertEquals("bind paramName", "name", bind.getParamName());
        Assert.assertEquals("bind bindName", "_like_name", bind.getBindName());

        Assert.assertTrue("HashParamExpr 属于 Expression 层", descent.getChildren().get(1) instanceof HashParamExpr);
        Assert.assertTrue("DollarParamExpr 属于 Expression 层", descent.getChildren().get(2) instanceof DollarParamExpr);
        Assert.assertTrue("LocalVarExpr 属于 Expression 层", descent.getChildren().get(3) instanceof LocalVarExpr);
        Assert.assertEquals("LocalVarExpr varName", "item.id", ((LocalVarExpr) descent.getChildren().get(3)).getVarName());
    }

    // ==================== 位置信息 + bounded 标记 ====================

    @Test
    public void test04_positionAndBoundedFlag() {
        ParamExpr p = new ParamExpr("x", 42, 7, 3);
        Assert.assertEquals("startPosition", 42, p.getStartPosition());
        Assert.assertEquals("line", 7, p.getLine());
        Assert.assertEquals("column", 3, p.getColumn());

        WhereScope whereBounded = new WhereScope(true, 0, 1, 1);
        SetScope setBounded = new SetScope(true, 0, 1, 1);
        SetScope setUnbounded = new SetScope(false, 0, 1, 1);
        Assert.assertTrue("where[...] bounded=true", whereBounded.isBounded());
        Assert.assertTrue("set[...] bounded=true", setBounded.isBounded());
        Assert.assertFalse("set 无边界 bounded=false", setUnbounded.isBounded());
    }

    // ==================== 空 guard 退化为自动 isNotEmpty ====================

    @Test
    public void test05_blankGuardTreatedAsAuto() {
        // #if()[and status = :status] —— 空 guard 等价于 #[body]
        IfUnit blankGuard = new IfUnit("   ", 0, 1, 1);
        Assert.assertFalse("空白 guard 视为无自定义 guard", blankGuard.hasCustomGuard());
    }
}
