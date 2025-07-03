package com.lc.mybatisx;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

/**
 * create by: 薛承城
 * description: CustomSqlSessionFactoryBean完全使用spring mybatis源码，仅仅在494行替换了自己的CustomXMLMapperBuilder
 * create time: 2019/5/9 17:54
 */
public class MybatisxSqlSessionFactoryBean extends SqlSessionFactoryBean {

    @Override
    public SqlSessionFactory getObject() throws Exception {
        SqlSessionFactory sqlSessionFactory = super.getObject();
        return sqlSessionFactory;
    }
}
