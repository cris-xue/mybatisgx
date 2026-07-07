package com.mybatisgx.dsl.mgxql.model;

public enum ComparisonOperator {

    LT("Lt", "<"),
    LT_EQ("Lteq", "<="),
    GT("Gt", ">"),
    GT_EQ("Gteq", ">="),
    IN("In", "in"),
    EQ("Eq", "="),
    EQUAL("Equal", "="),
    LIKE("Like", "like"),
    STARTING_WITH("StartingWith", "like"),
    ENDING_WITH("EndingWith", "like"),
    BETWEEN("Between", "between"),
    NOT("Not", "not"),
    NOT_EQ("NotEq", "<>"),

    IS_NULL("IsNull", "is null"),
    IS_NOT_NULL("IsNotNull", "is not null"),
    NOT_NULL("NotNull", "is not null");

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

    public Boolean isNullComparisonOperator() {
        return this == IS_NULL || this == IS_NOT_NULL || this == NOT_NULL;
    }
}
