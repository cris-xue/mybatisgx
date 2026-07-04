package com.mybatisgx.dsl.method;

import com.mybatisgx.dsl.method.model.ConditionTerm;
import com.mybatisgx.dsl.mgxql.model.ComparisonOperator;
import com.mybatisgx.dsl.mgxql.model.LogicOperator;
import org.junit.Assert;
import org.junit.Test;

public class ConditionTermTest {

    private ConditionTerm buildConditionTerm(LogicOperator logicOperator, String fieldName, ComparisonOperator comparisonOperator, String value) {
        ConditionTerm conditionTerm = new ConditionTerm();
        conditionTerm.setLogicOperator(logicOperator);
        conditionTerm.setFieldName(fieldName);
        conditionTerm.setComparisonOperator(comparisonOperator);
        conditionTerm.setValue(value);
        return conditionTerm;
    }

    @Test
    public void test01_IsNotNull_OmitsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.AND, "name", ComparisonOperator.IS_NOT_NULL, ":name");
        String result = term.toConditionTerm();
        Assert.assertEquals("and name is not null", result);
        Assert.assertFalse(result.contains(":"));
    }

    @Test
    public void test02_IsNull_OmitsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.AND, "status", ComparisonOperator.IS_NULL, ":status");
        String result = term.toConditionTerm();
        Assert.assertEquals("and status is null", result);
        Assert.assertFalse(result.contains(":"));
    }

    @Test
    public void test03_NotNull_OmitsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.OR, "email", ComparisonOperator.NOT_NULL, ":email");
        String result = term.toConditionTerm();
        Assert.assertEquals("or email is not null", result);
        Assert.assertFalse(result.contains(":"));
    }

    @Test
    public void test04_Eq_KeepsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.AND, "name", ComparisonOperator.EQ, ":name");
        String result = term.toConditionTerm();
        Assert.assertEquals("and name = :name", result);
    }

    @Test
    public void test05_Like_KeepsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.AND, "name", ComparisonOperator.LIKE, ":name");
        String result = term.toConditionTerm();
        Assert.assertEquals("and name like :name", result);
    }

    @Test
    public void test06_In_KeepsValuePlaceholder() {
        ConditionTerm term = buildConditionTerm(LogicOperator.AND, "id", ComparisonOperator.IN, ":id");
        String result = term.toConditionTerm();
        Assert.assertEquals("and id in :id", result);
    }
}
