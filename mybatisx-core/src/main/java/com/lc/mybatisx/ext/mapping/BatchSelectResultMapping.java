package com.lc.mybatisx.ext.mapping;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultMappingAdapter;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.List;
import java.util.Set;

public class BatchSelectResultMapping extends ResultMappingAdapter {

    private ResultMapping resultMapping;

    public BatchSelectResultMapping(ResultMapping resultMapping) {
        this.resultMapping = resultMapping;
    }

    public ResultMapping getResultMapping() {
        return resultMapping;
    }

    @Override
    public String getProperty() {
        return this.resultMapping.getProperty();
    }

    @Override
    public String getColumn() {
        return this.resultMapping.getColumn();
    }

    @Override
    public Class<?> getJavaType() {
        return this.resultMapping.getJavaType();
    }

    @Override
    public JdbcType getJdbcType() {
        return this.resultMapping.getJdbcType();
    }

    @Override
    public TypeHandler<?> getTypeHandler() {
        return this.resultMapping.getTypeHandler();
    }

    @Override
    public String getNestedResultMapId() {
        return this.resultMapping.getNestedResultMapId();
    }

    @Override
    public String getNestedQueryId() {
        return this.resultMapping.getNestedQueryId();
    }

    @Override
    public Set<String> getNotNullColumns() {
        return this.resultMapping.getNotNullColumns();
    }

    @Override
    public String getColumnPrefix() {
        return this.resultMapping.getColumnPrefix();
    }

    @Override
    public List<ResultFlag> getFlags() {
        return this.resultMapping.getFlags();
    }

    @Override
    public List<ResultMapping> getComposites() {
        return this.resultMapping.getComposites();
    }

    @Override
    public boolean isCompositeResult() {
        return this.resultMapping.isCompositeResult();
    }

    @Override
    public String getResultSet() {
        return this.resultMapping.getResultSet();
    }

    @Override
    public String getForeignColumn() {
        return this.resultMapping.getForeignColumn();
    }

    @Override
    public boolean isLazy() {
        return this.resultMapping.isLazy();
    }

    @Override
    public boolean isSimple() {
        return this.resultMapping.isSimple();
    }
}
