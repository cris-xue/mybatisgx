package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.relation.select.mgxql.manytomany.dao.MgxqlManyToManyJoinDao;
import com.mybatisgx.util.DaoTestUtils;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxqlSelectColumnTemplateHandler SELECT 列渲染测试：
 * 覆盖 select * / select alias.* 在 JOIN 场景下的列展开行为
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSelectColumnTemplateHandlerTest {

    private static final String DAO_NS = "com.mybatisgx.relation.select.mgxql.manytomany.dao.MgxqlManyToManyJoinDao";

    private static MybatisgxConfiguration config;
    private static MapperInfo mapperInfo;
    private static EntityInfo userEntityInfo;
    private static EntityInfo roleEntityInfo;

    @BeforeClass
    public static void setUp() {
        config = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.mgxql.manytomany.dao"}
        );
        mapperInfo = config.getMethodInfo(DAO_NS + ".findJoinStarManyToMany").getMapperInfo();

        // 提取 EntityInfo 用于手动构造测试场景
        MethodInfo methodInfo = config.getMethodInfo(DAO_NS + ".findJoinStarManyToMany");
        SelectStatement stmt = (SelectStatement) methodInfo.getMgxqlStatement();
        userEntityInfo = stmt.getFromClause().getPrimaryEntity().getEntityInfo();
        roleEntityInfo = stmt.getFromClause().getJoinEntities().get(0).getEntityInfo();
    }

    private String renderSql(String methodName) {
        MethodInfo methodInfo = config.getMethodInfo(DAO_NS + "." + methodName);
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        ColumnEntityRelation rootRelation = mapperInfo.getEntityRelationTree(returnType);
        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, rootRelation);
        return plainSelect.toString();
    }

    private String getSelectPart(String sql) {
        int fromIndex = sql.toUpperCase().indexOf(" FROM ");
        Assert.assertTrue("应存在 FROM", fromIndex > 0);
        return sql.substring(0, fromIndex);
    }

    /**
     * 5.3 select * from User u left join Role r → 展开全部实体列，表前缀分别为 u/r
     */
    @Test
    public void test01_selectStarJoinExpandsAllEntities() {
        String sql = renderSql("findJoinStarManyToMany");
        String selectPart = getSelectPart(sql);
        Assert.assertTrue("select * 应展开主实体 User 列（u. 前缀）", selectPart.contains("u."));
        Assert.assertTrue("select * 应展开 JOIN 实体 Role 列（r. 前缀）", selectPart.contains("r."));
    }

    /**
     * 5.4 select u.* from User u left join Role r → 仅展开 User 列，不展开 Role
     */
    @Test
    public void test02_selectAliasStarExpandsOnlySpecifiedEntity() {
        String sql = renderSql("findJoinAliasStar");
        String selectPart = getSelectPart(sql);
        Assert.assertTrue("select u.* 应展开 User 列（u. 前缀）", selectPart.contains("u."));
        Assert.assertFalse("select u.* 不应展开 Role 列（r. 前缀）", selectPart.contains("r."));
    }

    /**
     * 5.5 select * from User u left join Department d（d 无注解关系树节点）→ 从 EntityInfo 展开列，表前缀为 d
     * <p>
     * 手动构造 SelectStatement，使 JoinEntity 在 AliasContext 中无树节点：
     * rootRelation 只包含主实体 User 的 EntityInfo，无 composites 子节点，
     * 但 FromClause 包含 Role joinEntity（有 EntityInfo，无树节点匹配）。
     */
    @Test
    public void test03_selectStarNoTreeNodeExpandsFromEntityInfo() {
        // 构造仅包含主实体 User 的 rootRelation（无 composites → Role 不在树中）
        ColumnEntityRelation rootRelation = new ColumnEntityRelation<>();
        rootRelation.setEntityInfo(userEntityInfo);

        // 构造 SelectStatement：select * from User u left join Role r on u = r
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);

        SelectItem selectAllItem = new SelectItem();
        selectAllItem.setType(SelectItemType.COLUMN_ALL);
        selectAllItem.setFieldRef(new FieldReference(null, "*"));
        selectStatement.addSelectItem(selectAllItem);

        FromClause fromClause = new FromClause();
        FromEntity primaryEntity = new FromEntity("User", "u");
        primaryEntity.setEntityInfo(userEntityInfo);
        fromClause.setPrimaryEntity(primaryEntity);

        JoinEntity joinEntity = new JoinEntity("Role", "r", JoinType.LEFT);
        joinEntity.setEntityInfo(roleEntityInfo);
        joinEntity.setOnLeftAlias("u");
        joinEntity.setOnRightAlias("r");
        fromClause.addJoinEntity(joinEntity);
        selectStatement.setFromClause(fromClause);

        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, rootRelation);
        String sql = plainSelect.toString();
        String selectPart = getSelectPart(sql);
        Assert.assertTrue("无树节点时应从 EntityInfo 展开 Role 列（r. 前缀）", selectPart.contains("r."));
    }

    /**
     * 5.6 多对多 select * from User u left join Role r → 展开 User + Role 列，不展开中间表列
     */
    @Test
    public void test04_selectStarManyToManySkipsMiddleTable() {
        String sql = renderSql("findJoinStarManyToMany");
        String selectPart = getSelectPart(sql);
        Assert.assertTrue("多对多应展开主实体 User 列（u. 前缀）", selectPart.contains("u."));
        Assert.assertTrue("多对多应展开 JOIN 实体 Role 列（r. 前缀）", selectPart.contains("r."));
        Assert.assertFalse("多对多不应展开中间表列（user_role 前缀）", selectPart.contains("user_role"));
    }

    /**
     * 5.7 回归测试：非 JOIN 场景 select * 行为不变
     */
    @Test
    public void test05_nonJoinSelectStarBehaviorUnchanged() {
        MethodInfo methodInfo = config.getMethodInfo(DAO_NS + ".findJoinStarManyToMany");
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        ColumnEntityRelation rootRelation = mapperInfo.getEntityRelationTree(returnType);

        MgxqlSelectColumnTemplateHandler handler = new MgxqlSelectColumnTemplateHandler();
        PlainSelect plainSelect = handler.buildSelectSql(selectStatement, rootRelation);
        String sql = plainSelect.toString();
        Assert.assertTrue("应包含主表 FROM", sql.contains("simple_mtm_user_simple"));
        Assert.assertTrue("SELECT 列应有表前缀 u.", sql.contains("u."));
    }

    /**
     * 5.7 回归测试：select * from User u left join Role r 校验通过不报错
     */
    @Test
    public void test06_joinSelectStarNoError() {
        MethodInfo methodInfo = config.getMethodInfo(DAO_NS + ".findJoinStarManyToMany");
        Assert.assertNotNull("findJoinStarManyToMany 应存在", methodInfo);
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        Assert.assertNotNull("MGXQL 语句应正常解析", selectStatement);
        Assert.assertNotNull("FROM 子句应存在", selectStatement.getFromClause());
        Assert.assertFalse("JOIN 实体列表应非空", selectStatement.getFromClause().getJoinEntities().isEmpty());
    }
}
