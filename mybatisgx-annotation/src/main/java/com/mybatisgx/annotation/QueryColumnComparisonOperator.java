package com.mybatisgx.annotation;

/**
 * 查询字段支持的比较运算符
 * @author 薛承城
 * @date 2026/1/2 20:57
 */
public enum QueryColumnComparisonOperator {

    LT("Lt"),
    LT_EQ("Lteq"),
    GT("Gt"),
    GT_EQ("Gteq"),
    IN("In"),
    EQ("Eq"),
    EQUAL("Equal"),
    LIKE("Like"),
    STARTING_WITH("StartingWith"),
    ENDING_WITH("EndingWith"),
    BETWEEN("Between"),

    NOT_IN("NotIn"),
    NOT_EQ("NotEq"),
    NOT_EQUAL("NotEqual"),
    NOT_LIKE("NotLike"),
    NOT_STARTING_WITH("NotStartingWith"),
    NOT_ENDING_WITH("NotEndingWith"),
    NOT_BETWEEN("NotBetween");

    private String value;

    QueryColumnComparisonOperator(String value) {
        this.value = value;
    }

    public static QueryColumnComparisonOperator getComparisonOperator(String value) {
        for (QueryColumnComparisonOperator comparisonOperator : QueryColumnComparisonOperator.values()) {
            if (comparisonOperator.value.equals(value)) {
                return comparisonOperator;
            }
        }
        return QueryColumnComparisonOperator.EQ;
    }

    public String getValue() {
        return value;
    }
}
