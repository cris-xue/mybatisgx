package com.mybatisgx.relation.select.mgxql.manytomany.dto;

import java.util.List;

/**
 * 字段不匹配投影 DTO（包含 unknownList 字段，在路径链中不存在）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserUnknownFieldProjection {

    private String code;

    private List<MenuProjection> unknownList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MenuProjection> getUnknownList() {
        return unknownList;
    }

    public void setUnknownList(List<MenuProjection> unknownList) {
        this.unknownList = unknownList;
    }
}
