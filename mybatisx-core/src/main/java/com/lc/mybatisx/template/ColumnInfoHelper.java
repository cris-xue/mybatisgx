package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.RelationColumnInfo;

public class ColumnInfoHelper {

    public static Boolean isColumnInfo(ColumnInfo columnInfo) {
        return !(columnInfo instanceof RelationColumnInfo);
    }
}
