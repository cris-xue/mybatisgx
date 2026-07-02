package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * 字段名不匹配投影 DTO（unknownRelation 字段在路径链中不存在）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserUnknownFieldProjection {

    private String code;

    private UserDetailProjection unknownRelation;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetailProjection getUnknownRelation() {
        return unknownRelation;
    }

    public void setUnknownRelation(UserDetailProjection unknownRelation) {
        this.unknownRelation = unknownRelation;
    }
}
