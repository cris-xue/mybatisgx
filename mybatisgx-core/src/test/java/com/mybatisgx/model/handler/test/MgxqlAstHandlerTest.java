package com.mybatisgx.model.handler.test;

import com.mybatisgx.dsl.mgxql.MgxqlSyntaxProcessor;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.ComparisonOperator;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.LogicOperator;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.test.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MgxqlAstHandlerTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    private MgxqlSyntaxProcessor buildProcessor() {
        return new MgxqlSyntaxProcessor();
    }

    private MgxqlStatement parse(String expression) {
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MgxqlSyntaxProcessor processor = this.buildProcessor();
        return processor.executeAndCheck(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, expression);
    }

    private MgxqlStatement parseWithoutCheck(String expression) {
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MgxqlSyntaxProcessor processor = this.buildProcessor();
        return processor.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, expression);
    }

    // ==================== SELECT * FROM 基础测试 ====================

    @Test
    public void test001() {
        // 测试方法名中只有条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MgxqlSyntaxProcessor processor = this.buildProcessor();
        MgxqlStatement mgxqlStatement = processor.executeAndCheck(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "select * from User user");
        FromClause fromClause = mgxqlStatement.getFromClause();
        Assert.assertTrue(fromClause != null);
        Assert.assertTrue(fromClause.getPrimaryEntity() != null);
        Assert.assertTrue(fromClause.getPrimaryEntity().getEntityName().equals("User"));
        Assert.assertTrue(fromClause.getPrimaryEntity().getAlias().equals("user"));

        WhereClause whereClause = mgxqlStatement.getWhereClause();
        Assert.assertTrue(whereClause == null);
    }

    // ==================== WHERE 条件测试 ====================

    @Test
    public void test002() {
        // 测试方法名中只有条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        MgxqlSyntaxProcessor processor = this.buildProcessor();
        MgxqlStatement mgxqlStatement = processor.executeAndCheck(
                entityInfo,
                methodInfo,
                null,
                ConditionOriginType.METHOD_NAME,
                "select * from User where name = :name and (age < :age or status = :status)");
        WhereClause whereClause = mgxqlStatement.getWhereClause();
        Assert.assertTrue(whereClause != null);
        ConditionNode conditionNode = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", conditionNode.getFieldName());
        Assert.assertEquals("name", StringUtils.join(conditionNode.getParamValuePath(), "."));
    }

    @Test
    public void test003_singleWhereCondition() {
        // 测试单个where条件
        MgxqlStatement stmt = parse("select * from User where name = :name");
        WhereClause whereClause = stmt.getWhereClause();
        Assert.assertNotNull(whereClause);
        List<ConditionNode> nodes = whereClause.getRootExpression().getNodes();
        Assert.assertEquals(1, nodes.size());
        ConditionNode node = nodes.get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.EQ, node.getOperator());
        Assert.assertEquals("name", node.getParamValuePath().get(0));
    }

    @Test
    public void test004_multipleAndConditions() {
        // 测试多个AND条件
        MgxqlStatement stmt = parse("select * from User where name = :name and age > :age and status = :status");
        WhereClause whereClause = stmt.getWhereClause();
        Assert.assertNotNull(whereClause);
        List<ConditionNode> nodes = whereClause.getRootExpression().getNodes();
        Assert.assertEquals(3, nodes.size());

        Assert.assertEquals("name", nodes.get(0).getFieldName());
        Assert.assertEquals(ComparisonOperator.EQ, nodes.get(0).getOperator());

        Assert.assertEquals("age", nodes.get(1).getFieldName());
        Assert.assertEquals(ComparisonOperator.GT, nodes.get(1).getOperator());
        Assert.assertEquals(LogicOperator.AND, nodes.get(1).getLogicOperator());

        Assert.assertEquals("status", nodes.get(2).getFieldName());
        Assert.assertEquals(ComparisonOperator.EQ, nodes.get(2).getOperator());
        Assert.assertEquals(LogicOperator.AND, nodes.get(2).getLogicOperator());
    }

    @Test
    public void test005_multipleOrConditions() {
        // 测试多个OR条件
        MgxqlStatement stmt = parse("select * from User where name = :name or age > :age");
        WhereClause whereClause = stmt.getWhereClause();
        Assert.assertNotNull(whereClause);
        // OR表达式在根层
        ConditionExpression rootExpr = whereClause.getRootExpression();
        Assert.assertEquals(LogicOperator.OR, rootExpr.getLogicOperator());
    }

    @Test
    public void test006_nestedBracketConditions() {
        // 测试嵌套括号条件
        MgxqlStatement stmt = parse("select * from User where name = :name and (age < :age or status = :status)");
        WhereClause whereClause = stmt.getWhereClause();
        Assert.assertNotNull(whereClause);
        List<ConditionNode> nodes = whereClause.getRootExpression().getNodes();
        // 第一个是name条件，第二个是括号嵌套表达式
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals("name", nodes.get(0).getFieldName());
        // 第二个节点是嵌套括号
        ConditionNode bracketNode = nodes.get(1);
        Assert.assertTrue(bracketNode.isNested());
        Assert.assertNotNull(bracketNode.getSubExpression());
        // 括号内是OR表达式
        ConditionExpression subExpr = bracketNode.getSubExpression();
        Assert.assertEquals(LogicOperator.OR, subExpr.getLogicOperator());
        Assert.assertEquals(2, subExpr.getNodes().size());
        Assert.assertEquals("age", subExpr.getNodes().get(0).getFieldName());
        Assert.assertEquals("status", subExpr.getNodes().get(1).getFieldName());
    }

    // ==================== 比较运算符测试 ====================

    @Test
    public void test010_comparisonOperators() {
        // 测试各种比较运算符
        MgxqlStatement stmt = parse("select * from User where age > :age and age < :maxAge and age >= :minAge and age <= :limitAge and name != :excludeName");
        WhereClause whereClause = stmt.getWhereClause();
        List<ConditionNode> nodes = whereClause.getRootExpression().getNodes();
        Assert.assertEquals(5, nodes.size());

        Assert.assertEquals(ComparisonOperator.GT, nodes.get(0).getOperator());
        Assert.assertEquals(ComparisonOperator.LT, nodes.get(1).getOperator());
        Assert.assertEquals(ComparisonOperator.GT_EQ, nodes.get(2).getOperator());
        Assert.assertEquals(ComparisonOperator.LT_EQ, nodes.get(3).getOperator());
        Assert.assertEquals(ComparisonOperator.NOT, nodes.get(4).getOperator());
    }

    @Test
    public void test011_likeOperator() {
        // 测试like运算符
        MgxqlStatement stmt = parse("select * from User where name like :name");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.LIKE, node.getOperator());
    }

    @Test
    public void test012_inOperator() {
        // 测试in运算符
        MgxqlStatement stmt = parse("select * from User where status in :statusList");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("status", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.IN, node.getOperator());
    }

    @Test
    public void test013_betweenOperator() {
        // 测试between运算符
        MgxqlStatement stmt = parse("select * from User where age between :ageRange");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("age", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.BETWEEN, node.getOperator());
    }

    @Test
    public void test014_isNullOperator() {
        // 测试is null运算符
        MgxqlStatement stmt = parse("select * from User where phone is null");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("phone", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.IS_NULL, node.getOperator());
    }

    @Test
    public void test015_isNotNullOperator() {
        // 测试is not null运算符
        MgxqlStatement stmt = parse("select * from User where email is not null");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("email", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.IS_NOT_NULL, node.getOperator());
    }

    @Test
    public void test016_leftLikeOperator() {
        // 测试left like运算符
        MgxqlStatement stmt = parse("select * from User where name left like :name");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.STARTING_WITH, node.getOperator());
    }

    @Test
    public void test017_rightLikeOperator() {
        // 测试right like运算符
        MgxqlStatement stmt = parse("select * from User where name right like :name");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.ENDING_WITH, node.getOperator());
    }

    @Test
    public void test018_notInOperator() {
        // 测试not in运算符
        MgxqlStatement stmt = parse("select * from User where status not in :excludeStatusList");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("status", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.IN, node.getOperator());
        Assert.assertNotNull(node.getNotOperator());
        Assert.assertEquals(ComparisonOperator.NOT, node.getNotOperator());
    }

    @Test
    public void test019_notLikeOperator() {
        // 测试not like运算符
        MgxqlStatement stmt = parse("select * from User where name not like :excludeName");
        WhereClause whereClause = stmt.getWhereClause();
        ConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.LIKE, node.getOperator());
        Assert.assertNotNull(node.getNotOperator());
        Assert.assertEquals(ComparisonOperator.NOT, node.getNotOperator());
    }

    // ==================== 参数值路径测试 ====================

    @Test
    public void test020_simpleParamValuePath() {
        // 测试简单参数值路径
        MgxqlStatement stmt = parse("select * from User where name = :name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals(1, node.getParamValuePath().size());
        Assert.assertEquals("name", node.getParamValuePath().get(0));
    }

    @Test
    public void test021_nestedParamValuePath() {
        // 测试嵌套参数值路径 (role.menu.name)
        MgxqlStatement stmt = parse("select * from User where name = :role.menu.name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals(3, node.getParamValuePath().size());
        Assert.assertEquals("role", node.getParamValuePath().get(0));
        Assert.assertEquals("menu", node.getParamValuePath().get(1));
        Assert.assertEquals("name", node.getParamValuePath().get(2));
    }

    @Test
    public void test022_aliasedFieldCondition() {
        // 测试带别名的字段条件
        MgxqlStatement stmt = parse("select * from User user where user.name = :name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("user", node.getFieldAlias());
        Assert.assertEquals("name", node.getFieldName());
    }

    // ==================== SELECT 查询项测试 ====================

    @Test
    public void test030_selectAll() {
        // 测试select *
        MgxqlStatement stmt = parse("select * from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.COLUMN_ALL, items.get(0).getType());
    }

    @Test
    public void test031_selectSpecificColumns() {
        // 测试select指定列
        MgxqlStatement stmt = parse("select id, name, age from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(3, items.size());
        Assert.assertEquals(SelectItemType.COLUMN, items.get(0).getType());
        Assert.assertEquals("id", items.get(0).getFieldName());
        Assert.assertEquals(SelectItemType.COLUMN, items.get(1).getType());
        Assert.assertEquals("name", items.get(1).getFieldName());
        Assert.assertEquals(SelectItemType.COLUMN, items.get(2).getType());
        Assert.assertEquals("age", items.get(2).getFieldName());
    }

    @Test
    public void test032_selectWithAlias() {
        // 测试select带别名
        MgxqlStatement stmt = parse("select user.id, user.name from User user");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(2, items.size());
        Assert.assertEquals(SelectItemType.COLUMN, items.get(0).getType());
        Assert.assertEquals("user", items.get(0).getEntityAlias());
        Assert.assertEquals("id", items.get(0).getFieldName());
        Assert.assertEquals("user", items.get(1).getEntityAlias());
        Assert.assertEquals("name", items.get(1).getFieldName());
    }

    @Test
    public void test033_selectCountField() {
        // 测试count(id)
        MgxqlStatement stmt = parse("select count(id) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.COUNT, items.get(0).getType());
        Assert.assertEquals("id", items.get(0).getAggregateArg());
    }

    @Test
    public void test034_selectSum() {
        // 测试sum(age)
        MgxqlStatement stmt = parse("select sum(age) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.SUM, items.get(0).getType());
        Assert.assertEquals("age", items.get(0).getAggregateArg());
    }

    @Test
    public void test035_selectCountName() {
        // 测试count(name)
        MgxqlStatement stmt = parse("select count(name) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.COUNT, items.get(0).getType());
        Assert.assertEquals("name", items.get(0).getAggregateArg());
    }

    @Test
    public void test036_selectMax() {
        // 测试max(id)
        MgxqlStatement stmt = parse("select max(id) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.MAX, items.get(0).getType());
        Assert.assertEquals("id", items.get(0).getAggregateArg());
    }

    @Test
    public void test037_selectMin() {
        // 测试min(id)
        MgxqlStatement stmt = parse("select min(id) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.MIN, items.get(0).getType());
        Assert.assertEquals("id", items.get(0).getAggregateArg());
    }

    @Test
    public void test038_selectAvg() {
        // 测试avg(age)
        MgxqlStatement stmt = parse("select avg(age) from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.AVG, items.get(0).getType());
        Assert.assertEquals("age", items.get(0).getAggregateArg());
    }

    // ==================== FROM + JOIN 测试 ====================

    @Test
    public void test040_fromWithoutAlias() {
        // 测试from不带别名
        MgxqlStatement stmt = parse("select * from User");
        FromClause fromClause = stmt.getFromClause();
        Assert.assertNotNull(fromClause);
        Assert.assertEquals("User", fromClause.getPrimaryEntity().getEntityName());
        Assert.assertNull(fromClause.getPrimaryEntity().getAlias());
    }

    @Test
    public void test041_fromWithAlias() {
        // 测试from带别名
        MgxqlStatement stmt = parse("select * from User user");
        FromClause fromClause = stmt.getFromClause();
        Assert.assertNotNull(fromClause);
        Assert.assertEquals("User", fromClause.getPrimaryEntity().getEntityName());
        Assert.assertEquals("user", fromClause.getPrimaryEntity().getAlias());
    }

    @Test
    public void test042_leftJoin() {
        // 测试left join
        MgxqlStatement stmt = parseWithoutCheck("select * from User user left join Role role on user = role where user.name = :name");
        FromClause fromClause = stmt.getFromClause();
        Assert.assertNotNull(fromClause);
        Assert.assertEquals("User", fromClause.getPrimaryEntity().getEntityName());
        Assert.assertEquals("user", fromClause.getPrimaryEntity().getAlias());
        // 验证join实体
        List<JoinEntity> joinEntities = fromClause.getJoinEntities();
        Assert.assertEquals(1, joinEntities.size());
        JoinEntity joinEntity = joinEntities.get(0);
        Assert.assertEquals("Role", joinEntity.getEntityName());
        Assert.assertEquals("role", joinEntity.getAlias());
        Assert.assertEquals(JoinType.LEFT, joinEntity.getJoinType());
    }

    @Test
    public void test043_multipleLeftJoins() {
        // 测试多个left join
        MgxqlStatement stmt = parseWithoutCheck("select * from User user left join Role role on user = role left join Menu menu on role = menu");
        FromClause fromClause = stmt.getFromClause();
        Assert.assertEquals(2, fromClause.getJoinEntities().size());
        Assert.assertEquals("Role", fromClause.getJoinEntities().get(0).getEntityName());
        Assert.assertEquals("role", fromClause.getJoinEntities().get(0).getAlias());
        Assert.assertEquals("Menu", fromClause.getJoinEntities().get(1).getEntityName());
        Assert.assertEquals("menu", fromClause.getJoinEntities().get(1).getAlias());
    }

    // ==================== ORDER BY 测试 ====================

    @Test
    public void test050_orderByAsc() {
        // 测试order by默认升序
        MgxqlStatement stmt = parse("select * from User order by name");
        OrderByClause orderByClause = stmt.getOrderByClause();
        Assert.assertNotNull(orderByClause);
        List<OrderByItem> items = orderByClause.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("name", items.get(0).getField().getFieldName());
        Assert.assertEquals("asc", items.get(0).getDirection());
    }

    @Test
    public void test051_orderByDesc() {
        // 测试order by降序
        MgxqlStatement stmt = parse("select * from User order by age desc");
        OrderByClause orderByClause = stmt.getOrderByClause();
        Assert.assertNotNull(orderByClause);
        List<OrderByItem> items = orderByClause.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("age", items.get(0).getField().getFieldName());
        Assert.assertEquals("desc", items.get(0).getDirection());
    }

    @Test
    public void test052_orderByMultipleFields() {
        // 测试order by多字段
        MgxqlStatement stmt = parse("select * from User order by name asc, age desc");
        OrderByClause orderByClause = stmt.getOrderByClause();
        Assert.assertNotNull(orderByClause);
        List<OrderByItem> items = orderByClause.getItems();
        Assert.assertEquals(2, items.size());
        Assert.assertEquals("name", items.get(0).getField().getFieldName());
        Assert.assertEquals("asc", items.get(0).getDirection());
        Assert.assertEquals("age", items.get(1).getField().getFieldName());
        Assert.assertEquals("desc", items.get(1).getDirection());
    }

    @Test
    public void test053_orderByWithAlias() {
        // 测试order by带别名
        MgxqlStatement stmt = parse("select * from User user order by user.name desc");
        OrderByClause orderByClause = stmt.getOrderByClause();
        OrderByItem item = orderByClause.getItems().get(0);
        Assert.assertEquals("user", item.getField().getEntityAlias());
        Assert.assertEquals("name", item.getField().getFieldName());
        Assert.assertEquals("desc", item.getDirection());
    }

    // ==================== GROUP BY 测试 ====================

    @Test
    public void test060_groupBySingleField() {
        // 测试group by单字段
        MgxqlStatement stmt = parse("select * from User group by status");
        GroupByClause groupByClause = stmt.getGroupByClause();
        Assert.assertNotNull(groupByClause);
        Assert.assertEquals(1, groupByClause.getFields().size());
        Assert.assertEquals("status", groupByClause.getFields().get(0).getFieldName());
    }

    @Test
    public void test061_groupByMultipleFields() {
        // 测试group by多字段
        MgxqlStatement stmt = parse("select * from User group by status, age");
        GroupByClause groupByClause = stmt.getGroupByClause();
        Assert.assertNotNull(groupByClause);
        Assert.assertEquals(2, groupByClause.getFields().size());
        Assert.assertEquals("status", groupByClause.getFields().get(0).getFieldName());
        Assert.assertEquals("age", groupByClause.getFields().get(1).getFieldName());
    }

    @Test
    public void test062_groupByWithAlias() {
        // 测试group by带别名
        MgxqlStatement stmt = parse("select * from User user group by user.status");
        GroupByClause groupByClause = stmt.getGroupByClause();
        Assert.assertNotNull(groupByClause);
        FieldReference field = groupByClause.getFields().get(0);
        Assert.assertEquals("user", field.getEntityAlias());
        Assert.assertEquals("status", field.getFieldName());
    }

    // ==================== HAVING 测试 ====================

    @Test
    public void test070_havingWithMax() {
        // 测试having max(age) > :age
        MgxqlStatement stmt = parse("select * from User group by status having max(age) > :age");
        HavingClause havingClause = stmt.getHavingClause();
        Assert.assertNotNull(havingClause);
        Assert.assertEquals(1, havingClause.getConditions().size());
        HavingCondition condition = havingClause.getConditions().get(0);
        Assert.assertEquals(SelectItemType.MAX, condition.getAggregateFunction().getType());
        Assert.assertEquals("age", condition.getAggregateFunction().getAggregateArg());
        Assert.assertEquals(ComparisonOperator.GT, condition.getOperator());
        Assert.assertEquals("age", condition.getParamValuePath().get(0));
    }

    @Test
    public void test071_havingWithCount() {
        // 测试having count(id) > :`count`（count为关键字，需用反引号转义）
        MgxqlStatement stmt = parse("select * from User group by status having count(id) > :`count`");
        HavingClause havingClause = stmt.getHavingClause();
        Assert.assertNotNull(havingClause);
        HavingCondition condition = havingClause.getConditions().get(0);
        Assert.assertEquals(SelectItemType.COUNT, condition.getAggregateFunction().getType());
        Assert.assertEquals("id", condition.getAggregateFunction().getAggregateArg());
        Assert.assertEquals("count", condition.getParamValuePath().get(0));
    }

    @Test
    public void test072_havingWithMin() {
        // 测试having min(id) < :minId
        MgxqlStatement stmt = parse("select * from User group by status having min(id) < :minId");
        HavingClause havingClause = stmt.getHavingClause();
        HavingCondition condition = havingClause.getConditions().get(0);
        Assert.assertEquals(SelectItemType.MIN, condition.getAggregateFunction().getType());
        Assert.assertEquals("id", condition.getAggregateFunction().getAggregateArg());
        Assert.assertEquals(ComparisonOperator.LT, condition.getOperator());
    }

    // ==================== LIMIT 测试 ====================

    @Test
    public void test080_limitWithOffsetAndSize() {
        // 测试limit offset, size
        MgxqlStatement stmt = parse("select * from User limit 0, 10");
        LimitClause limitClause = stmt.getLimitClause();
        Assert.assertNotNull(limitClause);
        Assert.assertEquals(0, limitClause.getOffset());
        Assert.assertEquals(10, limitClause.getSize());
    }

    @Test
    public void test081_limitWithNonZeroOffset() {
        // 测试limit非零偏移
        MgxqlStatement stmt = parse("select * from User limit 20, 10");
        LimitClause limitClause = stmt.getLimitClause();
        Assert.assertNotNull(limitClause);
        Assert.assertEquals(20, limitClause.getOffset());
        Assert.assertEquals(10, limitClause.getSize());
    }

    // ==================== SQL命令类型测试 ====================

    @Test
    public void test090_selectCommandType() {
        // 测试SELECT命令类型
        MgxqlStatement stmt = parse("select * from User");
        Assert.assertEquals(SqlCommandType.SELECT, stmt.getCommandType());
    }

    // ==================== 组合查询测试 ====================

    @Test
    public void test100_fullQueryWithAllClauses() {
        // 测试包含所有子句的完整查询
        MgxqlStatement stmt = parseWithoutCheck(
                "select user.name, user.age from User user left join Role role on user = role " +
                "where user.name = :name and user.age > :age " +
                "group by user.status " +
                "having max(user.age) > :maxAge " +
                "order by user.age desc " +
                "limit 0, 10");
        // 验证select items
        Assert.assertEquals(2, stmt.getSelectItems().size());
        Assert.assertEquals("name", stmt.getSelectItems().get(0).getFieldName());
        Assert.assertEquals("age", stmt.getSelectItems().get(1).getFieldName());
        // 验证from
        Assert.assertEquals("User", stmt.getFromClause().getPrimaryEntity().getEntityName());
        Assert.assertEquals(1, stmt.getFromClause().getJoinEntities().size());
        // 验证where
        Assert.assertNotNull(stmt.getWhereClause());
        Assert.assertEquals(2, stmt.getWhereClause().getRootExpression().getNodes().size());
        // 验证group by
        Assert.assertNotNull(stmt.getGroupByClause());
        Assert.assertEquals("status", stmt.getGroupByClause().getFields().get(0).getFieldName());
        // 验证having
        Assert.assertNotNull(stmt.getHavingClause());
        Assert.assertEquals(1, stmt.getHavingClause().getConditions().size());
        // 验证order by
        Assert.assertNotNull(stmt.getOrderByClause());
        Assert.assertEquals("age", stmt.getOrderByClause().getItems().get(0).getField().getFieldName());
        Assert.assertEquals("desc", stmt.getOrderByClause().getItems().get(0).getDirection());
        // 验证limit
        Assert.assertNotNull(stmt.getLimitClause());
        Assert.assertEquals(0, stmt.getLimitClause().getOffset());
        Assert.assertEquals(10, stmt.getLimitClause().getSize());
    }

    @Test
    public void test101_selectWithWhereAndOrderBy() {
        // 测试where + order by组合
        MgxqlStatement stmt = parse("select * from User where name = :name order by age desc");
        Assert.assertNotNull(stmt.getWhereClause());
        Assert.assertNotNull(stmt.getOrderByClause());
        Assert.assertNull(stmt.getGroupByClause());
        Assert.assertNull(stmt.getLimitClause());
    }

    @Test
    public void test102_selectWithWhereAndLimit() {
        // 测试where + limit组合
        MgxqlStatement stmt = parse("select * from User where status = :status limit 0, 20");
        Assert.assertNotNull(stmt.getWhereClause());
        Assert.assertNull(stmt.getOrderByClause());
        Assert.assertNotNull(stmt.getLimitClause());
        Assert.assertEquals(0, stmt.getLimitClause().getOffset());
        Assert.assertEquals(20, stmt.getLimitClause().getSize());
    }

    @Test
    public void test103_aggregateWithGroupByAndHaving() {
        // 测试聚合函数 + group by + having组合
        MgxqlStatement stmt = parse("select count(id) from User group by status having count(id) > :minCount");
        Assert.assertEquals(1, stmt.getSelectItems().size());
        Assert.assertEquals(SelectItemType.COUNT, stmt.getSelectItems().get(0).getType());
        Assert.assertNotNull(stmt.getGroupByClause());
        Assert.assertNotNull(stmt.getHavingClause());
    }

    @Test
    public void test104_joinWithAliasedWhereConditions() {
        // 测试join + 带别名的where条件
        MgxqlStatement stmt = parseWithoutCheck(
                "select user.id, user.name, role.name from User user left join Role role on user = role " +
                "where user.name = :user.name and role.status = :role.status");
        WhereClause whereClause = stmt.getWhereClause();
        List<ConditionNode> nodes = whereClause.getRootExpression().getNodes();
        Assert.assertEquals(2, nodes.size());
        // 第一个条件: user.name = :user.name
        Assert.assertEquals("user", nodes.get(0).getFieldAlias());
        Assert.assertEquals("name", nodes.get(0).getFieldName());
        Assert.assertEquals("user", nodes.get(0).getParamValuePath().get(0));
        Assert.assertEquals("name", nodes.get(0).getParamValuePath().get(1));
        // 第二个条件: role.status = :role.status
        Assert.assertEquals("role", nodes.get(1).getFieldAlias());
        Assert.assertEquals("status", nodes.get(1).getFieldName());
        Assert.assertEquals("role", nodes.get(1).getParamValuePath().get(0));
        Assert.assertEquals("status", nodes.get(1).getParamValuePath().get(1));
    }

    // ==================== WHERE 数字字面量测试 ====================

    @Test
    public void test105_whereNumberLiteral() {
        // 测试WHERE数字字面量
        MgxqlStatement stmt = parse("select * from User where age > 18");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("age", node.getFieldName());
        Assert.assertEquals(ComparisonOperator.GT, node.getOperator());
        Assert.assertEquals(Integer.valueOf(18), node.getConditionValue());
        Assert.assertNull(node.getParamValuePath());
    }

    // ==================== HAVING 数字字面量测试 ====================

    @Test
    public void test106_havingNumberLiteral() {
        // 测试HAVING数字字面量
        MgxqlStatement stmt = parse("select count(id) from User group by status having count(id) > 10");
        HavingCondition condition = stmt.getHavingClause().getConditions().get(0);
        Assert.assertEquals(SelectItemType.COUNT, condition.getAggregateFunction().getType());
        Assert.assertEquals(ComparisonOperator.GT, condition.getOperator());
        Assert.assertEquals(Integer.valueOf(10), condition.getHavingValue());
        Assert.assertNull(condition.getParamValuePath());
    }

    // ==================== ? 前缀可选条件测试 ====================

    @Test
    public void test107_optionalCondition() {
        // 测试?前缀可选条件
        MgxqlStatement stmt = parse("select * from User where ?name = :name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertTrue(node.isOptional());
    }

    @Test
    public void test108_nonOptionalCondition() {
        // 测试非可选条件（无?前缀）
        MgxqlStatement stmt = parse("select * from User where name = :name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertFalse(node.isOptional());
    }

    // ==================== 反引号关键字转义测试 ====================

    @Test
    public void test110_paramNameIsKeyword() {
        // 测试参数名为关键字（having），使用反引号转义
        MgxqlStatement stmt = parseWithoutCheck("select * from User where status = :`having`");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("status", node.getFieldName());
        Assert.assertEquals(1, node.getParamValuePath().size());
        Assert.assertEquals("having", node.getParamValuePath().get(0));
    }

    @Test
    public void test111_paramPathContainsKeyword() {
        // 测试参数路径含关键字（having），使用反引号转义
        MgxqlStatement stmt = parseWithoutCheck("select * from User where name = :`having`.name");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
        Assert.assertEquals(2, node.getParamValuePath().size());
        Assert.assertEquals("having", node.getParamValuePath().get(0));
        Assert.assertEquals("name", node.getParamValuePath().get(1));
    }

    @Test
    public void test112_quotedFieldName() {
        // 测试反引号字段名（desc为关键字）
        MgxqlStatement stmt = parseWithoutCheck("select `desc` from User");
        List<SelectItem> items = stmt.getSelectItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(SelectItemType.COLUMN, items.get(0).getType());
        Assert.assertEquals("desc", items.get(0).getFieldName());
    }

    @Test
    public void test113_whereQuotedField() {
        // 测试WHERE中使用反引号字段（having为关键字）
        MgxqlStatement stmt = parseWithoutCheck("select * from User where `having` = :val");
        ConditionNode node = stmt.getWhereClause().getRootExpression().getNodes().get(0);
        Assert.assertEquals("having", node.getFieldName());
        Assert.assertEquals("val", node.getParamValuePath().get(0));
    }

    @Test
    public void test114_groupByQuotedField() {
        // 测试GROUP BY中使用反引号字段（desc为关键字）
        MgxqlStatement stmt = parseWithoutCheck("select `desc` from User group by `desc`");
        GroupByClause groupByClause = stmt.getGroupByClause();
        Assert.assertNotNull(groupByClause);
        Assert.assertEquals(1, groupByClause.getFields().size());
        Assert.assertEquals("desc", groupByClause.getFields().get(0).getFieldName());
    }

    @Test
    public void test115_orderByQuotedField() {
        // 测试ORDER BY中使用反引号字段（desc为关键字）
        MgxqlStatement stmt = parseWithoutCheck("select * from User order by `desc`");
        OrderByClause orderByClause = stmt.getOrderByClause();
        Assert.assertNotNull(orderByClause);
        List<OrderByItem> items = orderByClause.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("desc", items.get(0).getField().getFieldName());
    }
}
