package com.lc.mybatisx.handler;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.wrapper.SqlWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 17:17
 */
public abstract class AbstractMapperHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractMapperHandler.class);

    protected Class<?> getDaoInterface(String namespace) {
        Class<?> daoInterface = null;
        try {
            daoInterface = Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return daoInterface;
    }

    protected Type[] getDaoInterfaceParams(Class<?> daoInterface, Class<?> CURDInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
            ParameterizedTypeImpl daoSuperInterfaceClass = (ParameterizedTypeImpl) daoSuperInterfaceType;
            Class<?> daoSuperInterface = daoSuperInterfaceClass.getRawType();
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (daoSuperInterface == CURDInterface || daoSuperInterface == SimpleDao.class) {
                return daoInterfaceParams;
            }
        }

        log.info("{} un extend {} or {}", daoInterface.getName(), CURDInterface.getName(), SimpleDao.class.getName());
        return null;
    }

    protected SqlWrapper buildSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        String methodName = method.getName();
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        String tableName = entityClass.getAnnotation(Table.class).name();
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];

        SqlWrapper sqlWrapper = instanceSqlWrapper();
        sqlWrapper.setNamespace(namespace);
        sqlWrapper.setMethodName(methodName);
        sqlWrapper.setParameterType(entityClass.getName());
        sqlWrapper.setTableName(tableName);
        sqlWrapper.setResultType(entityClass.getName());

        return sqlWrapper;
    }

    abstract protected SqlWrapper instanceSqlWrapper();

}
