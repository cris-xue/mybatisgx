package com.lc.mybatisx.dao;

import java.util.List;

/**
 * 分页返回结果
 *
 * @author ccxuef
 * @date 2025/7/9 13:00
 */
public class Page<T> {

    private int total;

    private List<T> list;

    public Page() {
    }

    public Page(int total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
