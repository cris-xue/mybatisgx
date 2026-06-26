package com.mybatisgx.boundary;

import com.mybatisgx.annotation.QueryColumn;
import com.mybatisgx.annotation.QueryColumnComparisonOperator;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.handler.ColumnInfoHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 验证 ColumnInfoHandler 将 @QueryColumn 注解解析到 ColumnInfo 上。
 * @author 薛承城
 * @date 2026/6/26
 */
public class ColumnInfoQueryColumnTest {

    private ColumnInfoHandler handler;

    @Before
    public void setUp() {
        handler = new ColumnInfoHandler();
    }

    @Test
    public void test01_ignoredFieldCarriesQueryColumn() {
        List<ColumnInfo> list = handler.getColumnInfoList(QueryColumnTestEntity.class, new HashMap<>());
        ColumnInfo startDate = findByName(list, "startDate");

        assertNotNull(startDate.getQueryColumn());
        assertTrue(startDate.getQueryColumn().ignore());
    }

    @Test
    public void test02_unannotatedFieldHasNullQueryColumn() {
        List<ColumnInfo> list = handler.getColumnInfoList(QueryColumnTestEntity.class, new HashMap<>());
        ColumnInfo name = findByName(list, "name");

        assertNull(name.getQueryColumn());
    }

    @Test
    public void test03_operatorOnlyFieldNotIgnored() {
        List<ColumnInfo> list = handler.getColumnInfoList(QueryColumnTestEntity.class, new HashMap<>());
        ColumnInfo status = findByName(list, "status");

        assertNotNull(status.getQueryColumn());
        assertEquals(false, status.getQueryColumn().ignore());
        assertEquals(2, status.getQueryColumn().operator().length);
    }

    private ColumnInfo findByName(List<ColumnInfo> list, String javaColumnName) {
        for (ColumnInfo columnInfo : list) {
            if (javaColumnName.equals(columnInfo.getJavaColumnName())) {
                return columnInfo;
            }
        }
        throw new AssertionError("未找到字段: " + javaColumnName);
    }

    static class QueryColumnTestEntity {
        private String name;

        @QueryColumn(ignore = true)
        private String startDate;

        @QueryColumn(operator = {QueryColumnComparisonOperator.EQ, QueryColumnComparisonOperator.LIKE})
        private String status;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
