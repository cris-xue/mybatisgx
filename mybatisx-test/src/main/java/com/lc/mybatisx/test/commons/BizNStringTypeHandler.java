package com.lc.mybatisx.test.commons;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.NStringTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BizNStringTypeHandler extends NStringTypeHandler {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        super.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return super.getNullableResult(rs, columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getNullableResult(rs, columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getNullableResult(cs, columnIndex);
    }
}
