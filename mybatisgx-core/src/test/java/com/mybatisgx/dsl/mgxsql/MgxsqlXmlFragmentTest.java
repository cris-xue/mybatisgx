package com.mybatisgx.dsl.mgxsql;

import org.junit.Assert;
import org.junit.Test;

/**
 * MgxsqlXmlFragment 单元测试，覆盖全部 9 个静态方法
 *
 * @author 薛承城
 * @date 2026/7/10
 */
public class MgxsqlXmlFragmentTest {

    @Test
    public void testIfTag() {
        String result = MgxsqlXmlFragment.ifTag("name != null", "AND name = #{name}");
        Assert.assertEquals("<if test=\"name != null\"> AND name = #{name}</if>", result);
    }

    @Test
    public void testForeachSimple() {
        String result = MgxsqlXmlFragment.foreachSimple("ids");
        Assert.assertEquals("<foreach item=\"item\" collection=\"ids\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>", result);
    }

    @Test
    public void testForeachComplex() {
        String result = MgxsqlXmlFragment.foreachComplex("item", "list", "#{item.id}");
        Assert.assertEquals("<foreach item=\"item\" collection=\"list\" open=\"(\" close=\")\" separator=\",\">#{item.id}</foreach>", result);
    }

    @Test
    public void testBindTag() {
        String result = MgxsqlXmlFragment.bindTag("_like_name", "'%' + name + '%'");
        Assert.assertEquals("<bind name=\"_like_name\" value=\"'%' + name + '%'\"/>", result);
    }

    @Test
    public void testParamRef() {
        String result = MgxsqlXmlFragment.paramRef("name");
        Assert.assertEquals("#{name}", result);
    }

    @Test
    public void testParamRefWithDot() {
        String result = MgxsqlXmlFragment.paramRef("user.name");
        Assert.assertEquals("#{user.name}", result);
    }

    @Test
    public void testOpenWhere() {
        Assert.assertEquals("<where>", MgxsqlXmlFragment.openWhere());
    }

    @Test
    public void testCloseWhere() {
        Assert.assertEquals("</where>", MgxsqlXmlFragment.closeWhere());
    }

    @Test
    public void testOpenSet() {
        Assert.assertEquals("<set>", MgxsqlXmlFragment.openSet());
    }

    @Test
    public void testCloseSet() {
        Assert.assertEquals("</set>", MgxsqlXmlFragment.closeSet());
    }
}
