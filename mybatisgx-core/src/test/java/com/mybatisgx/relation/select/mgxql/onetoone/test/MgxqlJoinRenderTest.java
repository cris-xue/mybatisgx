package com.mybatisgx.relation.select.mgxql.onetoone.test;

import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.template.select.MgxqlSelectColumnTemplateHandler;
import com.mybatisgx.util.DaoTestUtils;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MGXQL join 渲染处理器测试：覆盖模型绑定（6.1）、四层 FROM/JOIN/ON 渲染（6.3）、
 * SELECT 投影（6.4）、聚合渲染（6.1）。
 *
 * @author 薛承城
 * @date 2026/6/27
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlJoinRenderTest {

    private static final String DAO_NAMESPACE = "com.mybatisgx.relation.select.mgxql.onetoone.dao.MgxqlJoinDao";
    private static MybatisgxConfiguration configuration;
    private static MapperInfo mapperInfo;

    @BeforeClass
    public static void setUp() {
        configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.mgxql.onetoone.dao"}
        );
        mapperInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findJoinList").getMapperInfo();
    }

    private String renderSql(String methodName) {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + "." + methodName);
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        ColumnEntityRelation rootRelation = mapperInfo.getEntityRelationTree(User.class);
        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, rootRelation);
        return plainSelect.toString();
    }

    @Test
    public void test01_fourLayerJoinRender() {
        String sql = renderSql("findJoinList");
        Assert.assertTrue("应包含主表 FROM simple_oto_user_simple", sql.contains("simple_oto_user_simple"));
        Assert.assertTrue("应 LEFT JOIN UserDetail", sql.contains("LEFT JOIN simple_oto_user_detail_simple"));
        Assert.assertTrue("应 LEFT JOIN UserDetailItem1", sql.contains("LEFT JOIN simple_oto_user_detail_item1_simple"));
        Assert.assertTrue("应 LEFT JOIN UserDetailItem2", sql.contains("LEFT JOIN simple_oto_user_detail_item2_simple"));
        Assert.assertTrue("ON 应推导 user_id 外键", sql.contains("user_id"));
        Assert.assertTrue("ON 应推导 user_detail_id 外键", sql.contains("user_detail_id"));
        Assert.assertTrue("ON 应推导 user_detail_item1_id 外键", sql.contains("user_detail_item1_id"));
    }

    @Test
    public void test02_projectionEmitsOnlySpecifiedColumn() {
        String sql = renderSql("findProjectionList");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertTrue("select u.code 应投影 code 列", selectPart.contains(".code"));
        long commaCount = selectPart.chars().filter(c -> c == ',').count();
        Assert.assertEquals("select u.code 应只投影一列，不补全展开", 0, commaCount);
    }

    @Test
    public void test03_aggregateRender() {
        String sql = renderSql("countAll");
        Assert.assertTrue("聚合应包含 COUNT", sql.contains("COUNT"));
        Assert.assertTrue("聚合 FROM 主表", sql.contains("simple_oto_user_simple"));
    }

    @Test
    public void test04_bindingsNonNull() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findJoinList");
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        Assert.assertNotNull("主实体应绑定 entityInfo",
                selectStatement.getFromClause().getPrimaryEntity().getEntityInfo());
        Assert.assertFalse("应存在 join 实体",
                selectStatement.getFromClause().getJoinEntities().isEmpty());
        Assert.assertNotNull("首个 join 实体应绑定 relationColumnInfo",
                selectStatement.getFromClause().getJoinEntities().get(0).getRelationColumnInfo());

        MethodInfo projection = configuration.getMethodInfo(DAO_NAMESPACE + ".findProjectionList");
        SelectStatement projectionStmt = (SelectStatement) projection.getMgxqlStatement();
        Assert.assertNotNull("COLUMN 投影应绑定 columnInfo",
                projectionStmt.getSelectItems().get(0).getColumnInfo());
    }
}
