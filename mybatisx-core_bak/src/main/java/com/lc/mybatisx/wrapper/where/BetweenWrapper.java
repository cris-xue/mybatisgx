package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.WhereWrapper;

/**
 * @author ：薛承城
 * @description：Between需要增加注解
 * @date ：2020/11/9 12:36
 */
public class BetweenWrapper extends WhereWrapper {

    /**
     * java字段
     */
    private String start;

    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
