package com.lc.mybatisx.model;

import com.lc.mybatisx.parse.SqlField;
import com.lc.mybatisx.parse.SqlWhere;

import java.util.List;

public class QuerySql {

    private String action;

    private String tableName;

    private SqlField sqlField;

    private List<SqlWhere> sqlWheres;

    private List<String> groupBy;

    private List<String> orderBy;

}
