package com.mybatisgx.model;

public enum ComparisonOperator {

    LT("Lt", "<"),
    LT_EQ("Lteq", "<="),
    GT("Gt", ">"),
    GT_EQ("Gteq", ">="),
    IN("In", "in"),
    IS("Is", "="),
    EQ("Eq", "="),
    NOT("Not", "<>"),
    LIKE("Like", "like"),
    BETWEEN("Between", "between");

    private String key;
    private String value;

    ComparisonOperator(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static ComparisonOperator getComparisonOperator(String key) {
        for (ComparisonOperator comparisonOperator : ComparisonOperator.values()) {
            if (comparisonOperator.key.equals(key)) {
                return comparisonOperator;
            }
        }
        return ComparisonOperator.EQ;
    }

    public String getValue() {
        return value;
    }
}
