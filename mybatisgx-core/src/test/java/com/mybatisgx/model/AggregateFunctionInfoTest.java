package com.mybatisgx.model;

import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import org.junit.Assert;
import org.junit.Test;

public class AggregateFunctionInfoTest {

    @Test
    public void test01_countWithFieldRef() {
        AggregateFunctionInfo info = new AggregateFunctionInfo();
        info.setType(SelectItemType.COUNT);
        info.setFieldRef(new GroupByFieldInfo(null, "id"));
        Assert.assertEquals(SelectItemType.COUNT, info.getType());
        Assert.assertEquals("id", info.getFieldRef().getFieldName());
    }

    @Test
    public void test02_countStarWithNullFieldRef() {
        AggregateFunctionInfo info = new AggregateFunctionInfo(SelectItemType.COUNT, null);
        Assert.assertEquals(SelectItemType.COUNT, info.getType());
        Assert.assertNull(info.getFieldRef());
    }

    @Test
    public void test03_maxWithAlias() {
        AggregateFunctionInfo info = new AggregateFunctionInfo(SelectItemType.MAX, new GroupByFieldInfo("user", "age"));
        Assert.assertEquals(SelectItemType.MAX, info.getType());
        Assert.assertEquals("user", info.getFieldRef().getEntityAlias());
        Assert.assertEquals("age", info.getFieldRef().getFieldName());
    }

    @Test
    public void test04_havingInfoWithLiteralValue() {
        HavingInfo having = new HavingInfo();
        having.setAggregateFunction(new AggregateFunctionInfo(SelectItemType.COUNT, new GroupByFieldInfo(null, "id")));
        having.setOperator(com.mybatisgx.model.ComparisonOperator.GT);
        having.setLiteralValue(10);
        Assert.assertEquals(SelectItemType.COUNT, having.getAggregateFunction().getType());
        Assert.assertEquals(com.mybatisgx.model.ComparisonOperator.GT, having.getOperator());
        Assert.assertEquals(Integer.valueOf(10), having.getLiteralValue());
        Assert.assertNull(having.getParamValuePath());
    }

    @Test
    public void test05_havingInfoWithParamPath() {
        HavingInfo having = new HavingInfo();
        having.setAggregateFunction(new AggregateFunctionInfo(SelectItemType.MAX, new GroupByFieldInfo(null, "age")));
        having.setOperator(com.mybatisgx.model.ComparisonOperator.GT);
        java.util.List<String> paramPath = new java.util.ArrayList<>();
        paramPath.add("maxAge");
        having.setParamValuePath(paramPath);
        Assert.assertEquals(com.mybatisgx.model.ComparisonOperator.GT, having.getOperator());
        Assert.assertEquals("maxAge", having.getParamValuePath().get(0));
        Assert.assertNull(having.getLiteralValue());
    }
}
