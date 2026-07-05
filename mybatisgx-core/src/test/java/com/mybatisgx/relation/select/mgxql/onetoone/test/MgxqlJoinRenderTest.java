package com.mybatisgx.relation.select.mgxql.onetoone.test;

import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.*;
import com.mybatisgx.template.select.AliasContext;
import com.mybatisgx.template.select.FromAliasContext;
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
        mapperInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findJoinList1").getMapperInfo();
    }

    private String renderSql(String methodName) {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + "." + methodName);
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();

        AliasContext aliasContext = AliasContext.build(selectStatement, selectStatement.getMgxqlEntityRelationTree());
        FromAliasContext fromAliasContext = FromAliasContext.build(selectStatement.getFromClause());

        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, fromAliasContext, aliasContext);
        return plainSelect.toString();
    }

    @Test
    public void test01_fourLayerJoinRender() {
        String sql = renderSql("findJoinList1");
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
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findJoinList1");
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

    @Test
    public void test05_returnTypeNotFromPrimary() {
        String sql = renderSql("findJoinList4");
        Assert.assertTrue("FROM 应为 User 表 simple_oto_user_simple", sql.contains("FROM simple_oto_user_simple"));
        Assert.assertTrue("FROM 应使用用户别名 simple_oto_user_simple_1_1", sql.contains("simple_oto_user_simple AS simple_oto_user_simple_1_1"));
        Assert.assertTrue("LEFT JOIN 应为 UserDetail 表 simple_oto_user_detail_simple",
                sql.contains("LEFT JOIN simple_oto_user_detail_simple"));
        Assert.assertTrue("JOIN 应使用用户别名 simple_oto_user_detail_simple_2_1", sql.contains("simple_oto_user_detail_simple AS simple_oto_user_detail_simple_2_1"));
        Assert.assertFalse("FROM 和 JOIN 别名不应相同",
                sql.matches("(?s).*FROM simple_oto_user_simple AS ud.*LEFT JOIN simple_oto_user_detail_simple AS ud.*"));
        String onPart = sql.substring(sql.indexOf("ON"));
        Assert.assertTrue("ON 条件应包含别名 simple_oto_user_simple_1_1.", onPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertTrue("ON 条件应包含别名 simple_oto_user_detail_simple_2_1.", onPart.contains("simple_oto_user_detail_simple_2_1."));
    }

    @Test
    public void test06_columnAllTableAliasMatchesFrom() {
        String sql = renderSql("findJoinList1");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertTrue("SELECT 列应使用用户别名 u. 作为表前缀", selectPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertFalse("SELECT 列不应使用树别名 simple_oto_user_simple 作为表前缀", selectPart.matches("(?s).*simple_oto_user_simple\\..*"));
        Assert.assertTrue("FROM 应使用用户别名 simple_oto_user_simple_1_1", sql.contains("simple_oto_user_simple AS simple_oto_user_simple_1_1"));
        Assert.assertTrue("LEFT JOIN 应使用用户别名 simple_oto_user_detail_simple_2_1", sql.contains("simple_oto_user_detail_simple AS simple_oto_user_detail_simple_2_1"));
    }

    @Test
    public void test07_columnProjectionTableAlias() {
        String sql = renderSql("findProjectionList");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertTrue("SELECT 列应使用用户别名 simple_oto_user_simple_1_1.code", selectPart.contains("simple_oto_user_simple_1_1.code"));
        Assert.assertFalse("SELECT 列不应使用树别名作为表前缀",
                selectPart.matches("(?s).*simple_oto_user_simple\\.code.*"));
    }

    @Test
    public void test08_selectStarJoinExpandsAllEntities() {
        String sql = renderSql("findJoinStarTwoEntity");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertTrue("select * 应展开主实体 User 列（simple_oto_user_simple_1_1. 前缀）", selectPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertTrue("select * 应展开 JOIN 实体 UserDetail 列（simple_oto_user_detail_simple_2_1. 前缀）", selectPart.contains("simple_oto_user_detail_simple_2_1."));
    }

    @Test
    public void test09_selectAliasStarExpandsOnlySpecifiedEntity() {
        String sql = renderSql("findJoinList2");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertTrue("select u.* 应展开 User 列", selectPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertFalse("select u.* 不应展开 UserDetail 列", selectPart.contains("simple_oto_user_detail_simple_2_1."));
    }

    /**
     * 投影 DTO + select * 四层 JOIN：应展开所有实体列，不输出裸 *
     */
    @Test
    public void test10_projectionSelectStarExpandsAllEntities() {
        String sql = renderSql("findSkipLevelProjectionSelectStar");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertFalse("select * 不应输出裸 *", selectPart.trim().equals("SELECT *"));
        Assert.assertTrue("select * 应展开主实体 User 列（simple_oto_user_simple_1_1. 前缀）", selectPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertTrue("select * 应展开 JOIN 实体 UserDetail 列（simple_oto_user_detail_simple_2_1. 前缀）", selectPart.contains("simple_oto_user_detail_simple_2_1."));
        Assert.assertTrue("select * 应展开 JOIN 实体 UserDetailItem1 列（simple_oto_user_detail_item1_simple_3_2. 前缀）", selectPart.contains("simple_oto_user_detail_item1_simple_3_2."));
        Assert.assertTrue("select * 应展开 JOIN 实体 UserDetailItem2 列（simple_oto_user_detail_item2_simple_4_2. 前缀）", selectPart.contains("simple_oto_user_detail_item2_simple_4_2."));
    }

    /**
     * 投影 DTO + select u.* 四层 JOIN：应仅展开 User 列，不输出裸 *
     */
    @Test
    public void test11_projectionSelectAliasStarExpandsOnlyUser() {
        String sql = renderSql("findSkipLevelProjectionSelectAliasStar");
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        String selectPart = sql.substring(0, fromIndex);
        Assert.assertFalse("select u.* 不应输出裸 *", selectPart.trim().equals("SELECT *"));
        Assert.assertTrue("select u.* 应展开 User 列（simple_oto_user_simple_1_1. 前缀）", selectPart.contains("simple_oto_user_simple_1_1."));
        Assert.assertFalse("select u.* 不应展开 UserDetail 列（simple_oto_user_detail_simple_2_1. 前缀）", selectPart.contains("simple_oto_user_detail_simple_2_1."));
    }

    /**
     * 投影 DTO 场景下 SELECT 列别名与 ResultMap column 属性对齐：
     * SELECT 中每个 AS 后的别名后缀（dbColumnNameAlias 部分）与投影树 EntityInfo 的 ColumnInfo.dbColumnNameAlias 一致。
     */
    @Test
    public void test12_projectionColumnAliasAlignsWithResultMap() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findSkipLevelProjectionSelectAliasStar");
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        ColumnEntityRelation rootRelation = mapperInfo.getEntityRelationTree(returnType);

        AliasContext aliasContext = AliasContext.build(selectStatement, selectStatement.getMgxqlEntityRelationTree());
        FromAliasContext fromAliasContext = FromAliasContext.build(selectStatement.getFromClause());

        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, fromAliasContext, aliasContext);
        String sql = plainSelect.toString();

        // 从投影树根节点获取 EntityInfo，验证 SELECT 中包含其列的 dbColumnNameAlias
        EntityInfo rootEntityInfo = rootRelation.getEntityInfo();
        Assert.assertNotNull("投影树根节点 entityInfo 不应为 null", rootEntityInfo);

        for (ColumnInfo columnInfo : rootEntityInfo.getTableColumnInfoList()) {
            String dbColumnNameAlias = columnInfo.getDbColumnNameAlias();
            if (dbColumnNameAlias != null) {
                Assert.assertTrue("SELECT 应包含列别名后缀 " + dbColumnNameAlias,
                        sql.contains(dbColumnNameAlias));
            }
        }
    }
}
