package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.checker.MgxqlSyntaxCheckerChain;
import com.mybatisgx.dsl.mgxql.model.AggregateArgumentKind;
import com.mybatisgx.dsl.mgxql.model.SelectItem;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlLexer;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;

/**
 * MGXQL 解析回归测试：聚合参数 argumentKind 与 select* 全字段查询
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSelectItemParsingTest {

    @Test
    public void test01_countFieldKind() {
        SelectStatement stmt = parseSelect("select count(id) from User");
        SelectItem item = stmt.getSelectItems().get(0);
        Assert.assertEquals(SelectItemType.COUNT, item.getType());
        Assert.assertEquals(AggregateArgumentKind.FIELD, item.getArgumentKind());
        Assert.assertEquals("id", item.getFieldName());
    }

    @Test
    public void test02_countAsteriskKind() {
        SelectStatement stmt = parseSelect("select count(*) from User");
        SelectItem item = stmt.getSelectItems().get(0);
        Assert.assertEquals(SelectItemType.COUNT, item.getType());
        Assert.assertEquals(AggregateArgumentKind.ASTERISK, item.getArgumentKind());
        Assert.assertEquals("*", item.getFieldName());
    }

    @Test
    public void test03_countNumberKind() {
        SelectStatement stmt = parseSelect("select count(1) from User");
        SelectItem item = stmt.getSelectItems().get(0);
        Assert.assertEquals(SelectItemType.COUNT, item.getType());
        Assert.assertEquals(AggregateArgumentKind.NUMBER, item.getArgumentKind());
        Assert.assertEquals("1", item.getFieldName());
    }

    @Test
    public void test04_maxFieldKind() {
        SelectStatement stmt = parseSelect("select max(age) from User");
        SelectItem item = stmt.getSelectItems().get(0);
        Assert.assertEquals(SelectItemType.MAX, item.getType());
        Assert.assertEquals(AggregateArgumentKind.FIELD, item.getArgumentKind());
        Assert.assertEquals("age", item.getFieldName());
    }

    @Test
    public void test05_sumFieldKind() {
        SelectStatement stmt = parseSelect("select sum(salary) from User");
        SelectItem item = stmt.getSelectItems().get(0);
        Assert.assertEquals(SelectItemType.SUM, item.getType());
        Assert.assertEquals(AggregateArgumentKind.FIELD, item.getArgumentKind());
    }

    @Test
    public void test09_selectStarMultiEntityNoSyntaxError() {
        SelectStatement stmt = parseSelect("select * from User user left join Role role on user = role");
        // 多实体 JOIN 下裸 select * 不再被语法校验拦截
        new MgxqlSyntaxCheckerChain().check(stmt);
    }

    @Test
    public void test10_selectStarMultiEntityAggregateArgumentCheckerSkips() {
        // select * 是 COLUMN_ALL，非聚合，AggregateArgumentChecker 不应误报
        SelectStatement stmt = parseSelect("select * from User user left join Role role on user = role");
        new MgxqlSyntaxCheckerChain().check(stmt);
        Assert.assertNotNull(stmt.getSelectItems().get(0).getFieldRef());
    }

    private SelectStatement parseSelect(String dsl) {
        MgxqlLexer lexer = new MgxqlLexer(CharStreams.fromString(dsl));
        MgxqlParser parser = new MgxqlParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.sql_statement();

        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        MgxqlSyntaxHandler.ParserContext ctx = new MgxqlSyntaxHandler.ParserContext(stmt);
        List<MgxqlSyntaxHandler.SyntaxNodeHandler> handlers = Arrays.asList(
                new MgxqlSyntaxHandler.SelectStatementHandler(),
                new MgxqlSyntaxHandler.SelectItemHandler(),
                new MgxqlSyntaxHandler.SelectFromClauseHandler(),
                new MgxqlSyntaxHandler.WhereClauseHandler(),
                new MgxqlSyntaxHandler.OrderByHandler(),
                new MgxqlSyntaxHandler.LimitHandler(),
                new MgxqlSyntaxHandler.GroupByHandler(),
                new MgxqlSyntaxHandler.HavingHandler()
        );
        traverse(tree, ctx, handlers);
        return stmt;
    }

    private void traverse(ParseTree node, MgxqlSyntaxHandler.ParserContext ctx,
                          List<MgxqlSyntaxHandler.SyntaxNodeHandler> handlers) {
        for (MgxqlSyntaxHandler.SyntaxNodeHandler handler : handlers) {
            if (handler.support(node)) {
                handler.handle(node, ctx);
                break;
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            traverse(node.getChild(i), ctx, handlers);
        }
    }
}
