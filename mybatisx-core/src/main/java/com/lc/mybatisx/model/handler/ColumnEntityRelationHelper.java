package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.ColumnEntityRelation;

public class ColumnEntityRelationHelper {

    public static void copy(ColumnEntityRelation source, ColumnEntityRelation target) {
        target.setColumnInfo(source.getColumnInfo());
        target.setMiddleEntityInfo(source.getMiddleEntityInfo());
        target.setEntityInfo(source.getEntityInfo());
    }
}
