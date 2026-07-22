package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.BracketDirectiveNode;
import com.mybatisgx.dsl.mgxql.model.ChooseNode;
import com.mybatisgx.dsl.mgxql.model.CollectionInfo;
import com.mybatisgx.dsl.mgxql.model.ComparisonOperator;
import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.IfDirectiveNode;
import com.mybatisgx.dsl.mgxql.model.LogicOperator;
import com.mybatisgx.dsl.mgxql.model.WhenNode;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import com.mybatisgx.model.ColumnInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * {@link MgxqlSubsetRenderer} 单测（task 4.1-4.8）。
 * <p>
 * 构造 mgxql WhereElement 平面变体树（普通条件 + Bracket/If/Choose 变体），断言产出的 mgxsql 子集文本；
 * 并用 {@link MgxsqlScanner} 双向验证产出文本语法合法（能被 mgxsql 反解析）。
 * <p>
 * 列名走回退路径（fieldRef.columnInfo.dbColumnName，不依赖 BoundParam 绑定产物）。
 *
 * @author 薛承城
 * @date 2026/7/21
 */
public class MgxqlSubsetRendererTest {

    private final MgxqlSubsetRenderer renderer = new MgxqlSubsetRenderer();
    private final MgxsqlScanner scanner = new MgxsqlScanner();

    // ==================== 普通条件 ====================

    @Test
    public void test01_singleEqCondition() {
        // name = :name  →  列名 user_name（Java 字段 name 映射 DB 列 user_name），参数 :name
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        expr.addNode(condition("name", "user_name", ComparisonOperator.EQ, "name"));
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("user_name = :name", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test02_numberRightValue() {
        // age > 18（数字字面量右值）
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = condition("age", "age", ComparisonOperator.GT, null);
        node.setConditionValue(18);
        expr.addNode(node);
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("age > 18", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test03_isNullCondition() {
        // email is null
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        expr.addNode(condition("email", "email", ComparisonOperator.IS_NULL, null));
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("email is null", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test04_likePatterns() {
        // 前模糊 name like %:name (ENDING_WITH)
        WhereExpression e1 = new WhereExpression(LogicOperator.NULL);
        e1.addNode(condition("name", "user_name", ComparisonOperator.ENDING_WITH, "name"));
        Assert.assertEquals("user_name like %:name", renderer.render(e1, null));

        // 后模糊 name like :name% (STARTING_WITH)
        WhereExpression e2 = new WhereExpression(LogicOperator.NULL);
        e2.addNode(condition("name", "user_name", ComparisonOperator.STARTING_WITH, "name"));
        Assert.assertEquals("user_name like :name%", renderer.render(e2, null));

        // 全模糊 name like %:name% (LIKE)
        WhereExpression e3 = new WhereExpression(LogicOperator.NULL);
        e3.addNode(condition("name", "user_name", ComparisonOperator.LIKE, "name"));
        Assert.assertEquals("user_name like %:name%", renderer.render(e3, null));

        assertMgxsqlValid("user_name like %:name%");
    }

    @Test
    public void test05_simpleIn() {
        // id in (:ids)
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        expr.addNode(condition("id", "id", ComparisonOperator.IN, "ids"));
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("id in (:ids)", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test05b_complexIn() {
        // 复杂 IN（单字段，design D6 task 5.4）：id in (item:objs)=>$item.id
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode node = condition("id", "id", ComparisonOperator.IN, "objs");
        CollectionInfo collectionInfo = new CollectionInfo();
        collectionInfo.setItemName("item");
        collectionInfo.setCollectionName("objs");
        collectionInfo.setValueExpr("item.id");
        node.setCollectionInfo(collectionInfo);
        expr.addNode(node);
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("id in (item:objs)=>$item.id", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test06_multiConditionAndOr() {
        // 多条件：name = :name  age > :age(AND)  status = :status(OR)
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode n1 = condition("name", "user_name", ComparisonOperator.EQ, "name");
        WhereConditionNode n2 = condition("age", "age", ComparisonOperator.GT, "age");
        n2.setLogicOperator(LogicOperator.AND);
        WhereConditionNode n3 = condition("status", "status", ComparisonOperator.EQ, "status");
        n3.setLogicOperator(LogicOperator.OR);
        expr.addNode(n1);
        expr.addNode(n2);
        expr.addNode(n3);
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("user_name = :name and age > :age or status = :status", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    // ==================== 动态门变体 ====================

    @Test
    public void test07_bracketDirectiveAutoGuard() {
        // #[name = :name]（BracketDirectiveNode，首元素 NULL）
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereExpression body = new WhereExpression(LogicOperator.NULL);
        body.addNode(condition("name", "user_name", ComparisonOperator.EQ, "name"));
        expr.addNode(new BracketDirectiveNode(body));
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("#[user_name = :name]", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test08_bracketDirectiveAndOrPrefix() {
        // 首元素 #[a=:a]，第二块 AND → #[and b=:b]，第三块 OR → #[or c=:c]
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereExpression body1 = new WhereExpression(LogicOperator.NULL);
        body1.addNode(condition("a", "a_col", ComparisonOperator.EQ, "a"));
        BracketDirectiveNode b1 = new BracketDirectiveNode(body1);
        expr.addNode(b1);

        WhereExpression body2 = new WhereExpression(LogicOperator.NULL);
        body2.addNode(condition("b", "b_col", ComparisonOperator.EQ, "b"));
        BracketDirectiveNode b2 = new BracketDirectiveNode(body2);
        b2.setLogicOperator(LogicOperator.AND);
        expr.addNode(b2);

        WhereExpression body3 = new WhereExpression(LogicOperator.NULL);
        body3.addNode(condition("c", "c_col", ComparisonOperator.EQ, "c"));
        BracketDirectiveNode b3 = new BracketDirectiveNode(body3);
        b3.setLogicOperator(LogicOperator.OR);
        expr.addNode(b3);

        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("#[a_col = :a]#[and b_col = :b]#[or c_col = :c]", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test09_ifDirectiveGuard() {
        // #if(status == 1)[user_name = :name]（IfDirectiveNode，guard 原样）
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereExpression body = new WhereExpression(LogicOperator.NULL);
        body.addNode(condition("name", "user_name", ComparisonOperator.EQ, "name"));
        expr.addNode(new IfDirectiveNode("status == 1", body));
        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("#if(status == 1)[user_name = :name]", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test10_chooseDirective() {
        // #choose[#when(x == 1)[a_col = :a] #otherwise[b_col = :b]]
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhenNode when = new WhenNode();
        when.setGuard("x == 1");
        WhereExpression whenBody = new WhereExpression(LogicOperator.NULL);
        whenBody.addNode(condition("a", "a_col", ComparisonOperator.EQ, "a"));
        when.setBody(whenBody);

        WhereExpression otherwiseBody = new WhereExpression(LogicOperator.NULL);
        otherwiseBody.addNode(condition("b", "b_col", ComparisonOperator.EQ, "b"));

        ChooseNode choose = new ChooseNode();
        choose.addWhen(when);
        choose.setOtherwise(otherwiseBody);
        expr.addNode(choose);

        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("#choose[#when(x == 1)[a_col = :a] #otherwise[b_col = :b]]", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test11_nestedBracketGroupInBody() {
        // #[name = :name and (age > :age or status = :status)]（body 内括号分组走 subExpression）
        WhereExpression expr = new WhereExpression(LogicOperator.NULL);
        WhereExpression body = new WhereExpression(LogicOperator.NULL);
        body.addNode(condition("name", "user_name", ComparisonOperator.EQ, "name"));
        // 括号分组：subExpression，AND 连接（与 name 条件之间）
        WhereConditionNode group = new WhereConditionNode();
        group.setLeftBracket("(");
        group.setRightBracket(")");
        group.setLogicOperator(LogicOperator.AND);
        WhereExpression subExpr = new WhereExpression(LogicOperator.NULL);
        subExpr.addNode(condition("age", "age", ComparisonOperator.GT, "age"));
        WhereConditionNode statusNode = condition("status", "status", ComparisonOperator.EQ, "status");
        statusNode.setLogicOperator(LogicOperator.OR);
        subExpr.addNode(statusNode);
        group.setSubExpression(subExpr);
        body.addNode(group);
        expr.addNode(new BracketDirectiveNode(body));

        String mgxsql = renderer.render(expr, null);
        Assert.assertEquals("#[user_name = :name and (age > :age or status = :status)]", mgxsql);
        assertMgxsqlValid(mgxsql);
    }

    @Test
    public void test12_emptyExpression() {
        Assert.assertEquals("", renderer.render(null, null));
        Assert.assertEquals("", renderer.render(new WhereExpression(LogicOperator.NULL), null));
    }

    // ==================== 辅助 ====================

    /**
     * 构造普通条件节点（回退路径：fieldRef + columnInfo.dbColumnName，不设 BoundParam）。
     *
     * @param javaField  Java 字段名
     * @param dbColumn   数据库列名
     * @param operator   比较算子
     * @param paramName  右值参数名（null 表示无参数，如 is null）
     */
    private WhereConditionNode condition(String javaField, String dbColumn, ComparisonOperator operator, String paramName) {
        WhereConditionNode node = new WhereConditionNode();
        FieldReference fieldRef = new FieldReference();
        fieldRef.setFieldName(javaField);
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setDbColumnName(dbColumn);
        fieldRef.setColumnInfo(columnInfo);
        node.setFieldRef(fieldRef);
        node.setOperator(operator);
        if (paramName != null) {
            node.setParamValuePath(Collections.singletonList(paramName));
        }
        return node;
    }

    /**
     * 双向验证：renderer 产出的 mgxsql 文本经 MgxsqlScanner 不报错（语法合法）。
     * 包裹 where[...] 外壳使其成为完整 mgxsql。
     */
    private void assertMgxsqlValid(String body) {
        String wrapped = "select * from User\nwhere[\n" + body + "\n]";
        try {
            String xml = scanner.process(wrapped);
            Assert.assertNotNull("mgxsql 应产出非 null XML", xml);
        } catch (Exception e) {
            Assert.fail("renderer 产出的 mgxsql 文本无法被 MgxsqlScanner 解析: " + body + "，错误: " + e.getMessage());
        }
    }
}
