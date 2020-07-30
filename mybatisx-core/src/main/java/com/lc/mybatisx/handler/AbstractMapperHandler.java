package com.lc.mybatisx.handler;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.utils.GenericUtils;
import com.lc.mybatisx.wrapper.SqlWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 17:17
 */
public abstract class AbstractMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapperHandler.class);

    protected Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

        logger.info("{} un extend {} or {}", daoInterface.getName(), CURDInterface.getName(), SimpleDao.class.getName());
        return null;
    }

    protected SqlWrapper buildSqlWrapper(String namespace, Method method, Type[] daoInterfaceParams) {
        String methodName = method.getName();
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        String tableName = this.getTableName(entityClass);

        SqlWrapper sqlWrapper = instanceSqlWrapper();
        sqlWrapper.setNamespace(namespace);
        sqlWrapper.setMethodName(methodName);
        String parameterType = this.getParameterType(method, idClass, entityClass);
        sqlWrapper.setParameterType(parameterType);
        sqlWrapper.setTableName(tableName);
        String resultType = this.getResultType(method, entityClass);
        sqlWrapper.setResultType(resultType);

        return sqlWrapper;
    }

    /**
     * 获取表名
     *
     * @param entityClass
     * @return
     */
    private String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        return table != null ? table.name() : entityClass.getSimpleName();
    }

    /**
     * @param method
     * @param idClass
     * @param entityClass
     * @return
     */
    private String getParameterType(Method method, Class<?> idClass, Class<?> entityClass) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 1) {
            Parameter parameter = parameters[0];
            Type type = parameter.getParameterizedType();
            Type paramType = GenericUtils.getGenericType(type);

            if (paramType instanceof TypeVariable<?>) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) paramType;
                if ("ENTITY".equals(typeVariable.getName())) {
                    return entityClass.getName();
                } else if ("ID".equals(typeVariable.getName())) {
                    return idClass.getName();
                }
            } else if (paramType instanceof Class<?>) {
                Class<?> clazz = (Class<?>) paramType;
                return clazz.getName();
            } else {
                return null;
            }
        }

        return null;
    }

    private String getResultType(Method method, Class<?> entityClass) {
        Type type = method.getGenericReturnType();

        Type returnType = GenericUtils.getGenericType(type);
        if (returnType instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) returnType;
            if ("ENTITY".equals(typeVariable.getName())) {
                return entityClass.getName();
            }
        } else if (returnType instanceof Class<?>) {
            Class<?> clazz = (Class<?>) returnType;
            return clazz.getName();
        } else {
            return null;
        }

        return null;
    }

    abstract protected SqlWrapper instanceSqlWrapper();

}
