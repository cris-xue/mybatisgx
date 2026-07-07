package com.mybatisgx.template.select;

/**
 * 方法名分页信息
 * @author 薛承城
 * @date 2025/11/20 12:22
 */
public class MethodRowLimitInfo {

    /**
     * 分页位置，从0开始。
     * <p>
     * 语义取决于 {@link #rowOffset} 标志：
     * <ul>
     *   <li>rowOffset=false（默认）：offset 表示页码，渲染时需乘以 size 得到行偏移</li>
     *   <li>rowOffset=true：offset 表示行偏移，渲染时直接使用，不再乘以 size</li>
     * </ul>
     */
    private final Integer offset;
    /**
     * 每一页大小
     */
    private final Integer size;

    /**
     * 是否为行偏移语义。true 表示 offset 已经是行偏移量（如 MGXQL limit 5,5 的 offset=5），
     * false 表示 offset 是页码（如方法名分页的 offset=1 表示第2页）
     */
    private final boolean rowOffset;

    public MethodRowLimitInfo(Integer offset, Integer size) {
        this(offset, size, false);
    }

    public MethodRowLimitInfo(Integer offset, Integer size, boolean rowOffset) {
        this.offset = offset == null ? 0 : offset;
        this.size = size == null ? 10 : size;
        this.rowOffset = rowOffset;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getSize() {
        return size;
    }

    public boolean isRowOffset() {
        return rowOffset;
    }
}
