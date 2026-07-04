package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL LIMIT子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class LimitClause {

    /**
     * 偏移量，从0开始
     */
    private int offset;

    /**
     * 每页大小
     */
    private int size;

    public LimitClause() {
    }

    public LimitClause(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
