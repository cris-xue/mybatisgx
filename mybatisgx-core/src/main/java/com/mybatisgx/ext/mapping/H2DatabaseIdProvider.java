package com.mybatisgx.ext.mapping;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/25 17:37
 */
public class H2DatabaseIdProvider extends VendorDatabaseIdProvider {

    private Properties properties;

    @Override
    public void setProperties(Properties p) {
        this.properties = p;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) {
        String databaseId = super.getDatabaseId(dataSource);
        if ("H2".equals(databaseId)) {
            if (dataSource instanceof PooledDataSource) {
                PooledDataSource pooledDataSource = (PooledDataSource) dataSource;
                String url = pooledDataSource.getUrl();
                String[] urlItems = StringUtils.split(url, ";");
                for (String urlItem : urlItems) {
                    if (urlItem.startsWith("MODE=")) {
                        String dbType = StringUtils.split(urlItem, "=")[1];
                        return this.properties.getProperty(dbType);
                    }
                }
            }
        }
        return databaseId;
    }
}
