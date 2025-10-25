package com.lc.mybatisx.model;

public enum ComparisonOp {

    LT("Lt", "<", "等值查询"),
    LT_EQ("Lteq", "<=", "等值查询"),
    GT("Gt", ">", "等值查询"),
    GT_EQ("Gteq", ">=", "等值查询"),
    IN("In", "in", "等值查询"),
    IS("Is", "=", "等值查询"),
    EQ("Eq", "=", "等值查询"),
    NOT("Not", "<>", "等值查询"),
    LIKE("Like", "like", "等值查询"),
    BETWEEN("Between", "between", "等值查询");

    private String key;
    private String comparisonOp;
    private String comparisonOpDesc;

    ComparisonOp(String key, String comparisonOp, String comparisonOpDesc) {
        this.key = key;
        this.comparisonOp = comparisonOp;
        this.comparisonOpDesc = comparisonOpDesc;
    }

    public static ComparisonOp getComparisonOp(String key) {
        for (ComparisonOp comparisonOp : ComparisonOp.values()) {
            if (comparisonOp.key.equals(key)) {
                return comparisonOp;
            }
        }
        return null;
    }

    public String getComparisonOp() {
        return comparisonOp;
    }
}
