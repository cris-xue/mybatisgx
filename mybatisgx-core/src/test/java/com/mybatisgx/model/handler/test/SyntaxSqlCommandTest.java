package com.mybatisgx.model.handler.test;

import com.mybatisgx.model.handler.MybatisgxSyntaxProcessor;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxSqlCommandTest {

    private MybatisgxSyntaxProcessor buildProcessor() {
        return new MybatisgxSyntaxProcessor();
    }

    @Test
    public void test01() {
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        SqlCommandType sqlCommandType = processor.getSqlCommandType("findByName");
        Assert.assertEquals(SqlCommandType.SELECT, sqlCommandType);
    }

    @Test
    public void test02() {
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        SqlCommandType sqlCommandType = processor.getSqlCommandType("countByName");
        Assert.assertEquals(SqlCommandType.SELECT, sqlCommandType);
    }

    @Test
    public void test03() {
        MybatisgxSyntaxProcessor processor = this.buildProcessor();
        SqlCommandType sqlCommandType = processor.getSqlCommandType("deleteByName");
        Assert.assertEquals(SqlCommandType.DELETE, sqlCommandType);
    }
}
