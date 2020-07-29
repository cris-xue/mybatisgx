package com.lc.mybatisx.handler;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.wrapper.SqlWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

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
        String parameterType = this.getParameterType(method, entityClass);
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

    private String getParameterType(Method method, Class<?> entityClass) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 1) {
            TypeVariable typeVariable = (TypeVariable) parameters[0].getParameterizedType();
            String parameterName = typeVariable.getName();
            if ("ENTITY".equals(parameterName)) {
                return entityClass.getName();
            } else {

            }
        }
        return null;
    }

    private String getResultType(Method method, Class<?> entityClass) {
        Type returntype = method.getGenericReturnType();
        if (returntype instanceof Class<?>) {
            Class<?> resultType = (Class<?>) returntype;
            return resultType.getName();
        } else if (returntype instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            Type rawType = parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (rawType instanceof Map<?, ?>) {
                Class<?> resultType = (Class<?>) rawType;
                return resultType.getName();
            } else if (rawType instanceof Class<?> && rawType == List.class) {
                Type listActualType = actualTypeArguments[0];
                if (listActualType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) actualTypeArguments[0];
                    if ("ENTITY".equals(typeVariable.getName())) {
                        return entityClass.getName();
                    }
                    return typeVariable.getName();
                }
                return listActualType.getTypeName();
            } else {
                // rawType
                return null;
            }
        } else if (returntype instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) returntype;
            if ("ENTITY".equals(typeVariable.getName())) {
                return entityClass.getName();
            }
        }
        return null;
    }

    abstract protected SqlWrapper instanceSqlWrapper();

}
