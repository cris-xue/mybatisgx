package com.mybatisgx.boundary;

import com.mybatisgx.annotation.*;
import com.mybatisgx.model.RelationColumnInfo;
import com.mybatisgx.model.RelationType;
import com.mybatisgx.model.handler.ColumnInfoHandler;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *  覆盖点：
 * 1. mappedBy + 非法 JoinColumn / JoinTable
 * 2. MANY_TO_MANY 缺失 JoinTable
 * 3. MANY_TO_MANY + JoinColumn 非法
 * 4. 非 MANY_TO_MANY + 使用 JoinTable 非法
 * 5. 非 MANY_TO_MANY 缺失 JoinColumn(s)
 * 6. JoinColumn + JoinColumns 同时存在
 * 7. JoinColumns 为空
 * 8. Fetch 缺失
 * 9. 正常合法路径
 * @author 薛承城
 * @date 2025/12/24 19:25
 */
public class ColumnInfoHandlerSetRelationColumnInfoTest {

    private ColumnInfoHandler handler;
    private Method method;

    @Before
    public void setUp() throws Exception {
        handler = new ColumnInfoHandler();
        method = ColumnInfoHandler.class
                .getDeclaredMethod("setRelationColumnInfo", Field.class, RelationColumnInfo.class);
        method.setAccessible(true);
    }

    // ===================== 异常分支 =====================

    @Test
    public void testMappedByWithJoinColumn_shouldThrow() throws Exception {
        Field field = EntityMappedByInvalid.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, "user");

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段已声明为关系反向方（mappedBy 不为空），不允许定义 JoinTable / JoinColumns / JoinColumn", e.getCause().getMessage());
        }
    }

    @Test
    public void testManyToManyWithoutJoinTable_shouldThrow() throws Exception {
        Field field = EntityManyToManyNoJoinTable.class.getDeclaredField("roles");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.MANY_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("roles字段为多对多关系的维护方，必须使用 JoinTable 注解定义中间表", e.getCause().getMessage());
        }
    }

    @Test
    public void testManyToManyWithJoinColumn_shouldThrow() throws Exception {
        Field field = EntityManyToManyWithJoinColumn.class.getDeclaredField("roles");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.MANY_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("roles字段为多对多关系的维护方，必须使用 JoinTable 注解定义中间表", e.getCause().getMessage());
        }
    }

    @Test
    public void testNonManyToManyWithJoinTable_shouldThrow() throws Exception {
        Field field = EntityOneToManyWithJoinTable.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段不是多对多关系，不能使用 JoinTable 注解", e.getCause().getMessage());
        }
    }

    @Test
    public void testNonManyToManyWithoutJoinColumn_shouldThrow() throws Exception {
        Field field = EntityOneToManyNoJoinColumn.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段为关系维护方，必须使用 JoinColumn 或 JoinColumns 定义外键", e.getCause().getMessage());
        }
    }

    @Test
    public void testJoinColumnAndJoinColumnsTogether_shouldThrow() throws Exception {
        Field field = EntityJoinColumnAndJoinColumns.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段不能同时使用 JoinColumn 和 JoinColumns，请选择其中一种", e.getCause().getMessage());
        }
    }

    @Test
    public void testJoinColumnsEmpty_shouldThrow() throws Exception {
        Field field = EntityJoinColumnsEmpty.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段的 JoinColumns 至少需要定义一个 JoinColumn", e.getCause().getMessage());
        }
    }

    @Test
    public void testFetchMissing_shouldThrow() throws Exception {
        Field field = EntityFetchMissing.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        try {
            invoke(field, info);
        } catch (InvocationTargetException e) {
            assertEquals("orders字段必须显式声明 Fetch 注解，用于指定关联加载时的抓取方式（如批量、单条等）", e.getCause().getMessage());
        }
    }

    // ===================== 正常分支 =====================

    @Test
    public void testValidOneToMany_shouldPass() throws Exception {
        Field field = EntityValid.class.getDeclaredField("orders");
        RelationColumnInfo info = buildRelationInfo(field, RelationType.ONE_TO_MANY, null);

        invoke(field, info);

        assertNotNull(info.getFetch());
        assertNotNull(info.getInverseForeignKeyInfoList());
        assertEquals(1, info.getInverseForeignKeyInfoList().size());
    }

    // ===================== 工具方法 =====================

    private void invoke(Field field, RelationColumnInfo info) throws Exception {
        method.invoke(handler, field, info);
    }

    private RelationColumnInfo buildRelationInfo(Field field, RelationType type, String mappedBy) {
        RelationColumnInfo info = new RelationColumnInfo();
        info.setJavaColumnName(field.getName());
        info.setRelationType(type);
        info.setMappedBy(mappedBy);
        return info;
    }

    // ===================== 测试实体 =====================

    static class EntityMappedByInvalid {
        @OneToMany(mappedBy = "user")
        @JoinColumn(name = "user_id")
        @Fetch
        private Object orders;
    }

    static class EntityManyToManyNoJoinTable {
        @ManyToMany
        @Fetch
        private Object roles;
    }

    static class EntityManyToManyWithJoinColumn {
        @ManyToMany
        @JoinColumn(name = "role_id")
        @Fetch
        private Object roles;
    }

    static class EntityOneToManyWithJoinTable {
        @OneToMany
        @JoinTable(
                name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "order_id")
        )
        @Fetch
        private Object orders;
    }

    static class EntityOneToManyNoJoinColumn {
        @OneToMany
        @Fetch
        private Object orders;
    }

    static class EntityJoinColumnAndJoinColumns {
        @OneToMany
        @JoinColumn(name = "user_id")
        @JoinColumns(@JoinColumn(name = "user_id"))
        @Fetch
        private Object orders;
    }

    static class EntityJoinColumnsEmpty {
        @OneToMany
        @JoinColumns({})
        @Fetch
        private Object orders;
    }

    static class EntityFetchMissing {
        @OneToMany
        @JoinColumn(name = "user_id")
        private Object orders;
    }

    static class EntityValid {
        @OneToMany
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        @Fetch
        private Object orders;
    }
}
