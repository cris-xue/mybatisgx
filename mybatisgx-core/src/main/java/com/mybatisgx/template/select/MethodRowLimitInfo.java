package com.mybatisgx.template.select;

/**
 * 方法名分页信息
 * @author 薛承城
 * @date 2025/11/20 12:22
 */
public class MethodRowLimitInfo {

    /**
     * 分页位置，从0开始
     */
    private final Integer offset;
    /**
     * 每一页大小
     */
    private final Integer size;

    public MethodRowLimitInfo(Integer offset, Integer size) {
        this.offset = offset == null ? 0 : offset;
        this.size = size == null ? 10 : size;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getSize() {
        return size;
    }
}