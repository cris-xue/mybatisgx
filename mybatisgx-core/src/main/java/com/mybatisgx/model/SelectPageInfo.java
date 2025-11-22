package com.mybatisgx.model;

/**
 * 查询分页信息
 * @author 薛承城
 * @date 2025/11/20 12:22
 */
public class SelectPageInfo {

    /**
     * 分页位置
     */
    private Integer index = 0;
    /**
     * 每一页大小
     */
    private Integer size = 10;

    public SelectPageInfo(Integer index, Integer size) {
        this.index = index;
        this.size = size;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
