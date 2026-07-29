package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.BoundParam;
import com.mybatisgx.dsl.mgxql.model.BoundParamEntry;
import com.mybatisgx.dsl.mgxql.model.BracketDirectiveNode;
import com.mybatisgx.dsl.mgxql.model.ComparisonOperator;
import com.mybatisgx.dsl.mgxql.model.LogicOperator;
import com.mybatisgx.dsl.mgxql.model.ParamKind;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * MGXQL → mgxsql → MyBatis XML 三段管线测试。
 *
 * @author 薛承城
 * @date 2026/7/23
 */
public class MgxqlToMgxsqlPipelineTest {

    @Test
    public void test01_boundModelToSubsetToXml() {
        WhereExpression root = new WhereExpression(LogicOperator.NULL);
        WhereExpression body = new WhereExpression(LogicOperator.NULL);
        body.addNode(condition("user_name", "query", "name"));
        root.addNode(new BracketDirectiveNode(body));

        MgxqlWhereHandler renderer = new MgxqlWhereHandler();
        String subset = renderer.renderWhereClause(root, null);
        Assert.assertEquals("where[#[user_name = :query.name]]", subset);
        Assert.assertFalse("子集阶段不应出现 MyBatis 参数", subset.contains("#{"));
        Assert.assertFalse("子集阶段不应出现 MyBatis XML where", subset.contains("<where>"));

        String xml = new MgxsqlScanner().process("select * from User " + subset);
        Assert.assertTrue("scanner 应保留静态 SELECT", xml.contains("select * from User"));
        Assert.assertTrue("scanner 应生成 <where>", xml.contains("<where>"));
        Assert.assertTrue("#[...] 应生成 <if>", xml.contains("<if test="));
        Assert.assertTrue(":query.name 应转 MyBatis 参数", xml.contains("#{query.name}"));
    }

    private WhereConditionNode condition(String dbColumn, String firstPath, String secondPath) {
        WhereConditionNode node = new WhereConditionNode();
        node.setOperator(ComparisonOperator.EQ);
        node.setParamValuePath(Arrays.asList(firstPath, secondPath));
        BoundParam boundParam = new BoundParam();
        boundParam.setKind(ParamKind.SIMPLE);
        boundParam.setOperator(ComparisonOperator.EQ);
        BoundParamEntry entry = new BoundParamEntry();
        entry.setSqlExpression(new ConditionColumnExpression(dbColumn, null));
        entry.setParamPath(Arrays.asList(firstPath, secondPath));
        boundParam.addEntry(entry);
        node.setBoundParam(boundParam);
        return node;
    }
}
