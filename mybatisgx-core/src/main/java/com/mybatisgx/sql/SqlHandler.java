package com.mybatisgx.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * sql处理器
 * @author 薛承城
 * @date 2025/11/13 18:10
 */
public interface SqlHandler {

    BoundSql process(MappedStatement mappedStatement, Object parameter);
}
