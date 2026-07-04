package com.mybatisgx.dsl.mgxql.model;

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * MGXQL语句模型，作为语法解析的中间表示，最终会转换成MyBatis XML
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlStatement {

    /**
     * mgxql来源
     */
    private MgxqlSourceType mgxqlSourceType;
    /**
     * SQL命令类型：SELECT/INSERT/DELETE/UPDATE
     */
    private SqlCommandType commandType;
    /**
     * WHERE子句
     */
    private WhereClause whereClause;
    /**
     * 语义表达式
     */
    private String dsl;

    public MgxqlSourceType getMgxqlSourceType() {
        return mgxqlSourceType;
    }

    public void setMgxqlSourceType(MgxqlSourceType mgxqlSourceType) {
        this.mgxqlSourceType = mgxqlSourceType;
    }

    public SqlCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(SqlCommandType commandType) {
        this.commandType = commandType;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(WhereClause whereClause) {
        this.whereClause = whereClause;
    }

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }
}
