package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.Version;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.parse.SqlModel;
import com.lc.mybatisx.utils.GenericUtils;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.LogicDeleteWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.VersionWrapper;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
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

    protected SqlModel sqlModel;
    protected SqlWrapper sqlWrapper;

    public void init(SqlModel sqlModel, String namespace, Method method, Type[] daoInterfaceParams) {
    }

    protected Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
            ParameterizedTypeImpl daoSuperInterfaceClass = (ParameterizedTypeImpl) daoSuperInterfaceType;
            // Class<?> daoSuperInterface = daoSuperInterfaceClass.getRawType();
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (TypeUtils.isAssignable(Dao.class, daoInterface)) {
                return daoInterfaceParams;
            }
            /*if (daoSuperInterface == CURDInterface || daoSuperInterface == SimpleDao.class) {
                return daoInterfaceParams;
            }*/
        }

        logger.info("{} un extend {}", SimpleDao.class.getName());
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

        sqlWrapper.setLogicDeleteWrapper(buildLogicDeleteWrapper(entityClass));

        return sqlWrapper;
    }

    protected LogicDeleteWrapper buildLogicDeleteWrapper(Class<?> entityClass) {
        Field logicDeleteField = ReflectUtils.getField(entityClass, LogicDelete.class);
        if (logicDeleteField != null) {
            LogicDeleteWrapper logicDeleteWrapper = new LogicDeleteWrapper();
            String logicDeleteFieldName = logicDeleteField.getName();
            logicDeleteWrapper.setDbColumn(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, logicDeleteFieldName));
            LogicDelete logicDelete = logicDeleteField.getAnnotation(LogicDelete.class);
            logicDeleteWrapper.setValue(logicDelete.delete());
            logicDeleteWrapper.setNotValue(logicDelete.notDelete());
            return logicDeleteWrapper;
        }
        return null;
    }

    protected VersionWrapper buildVersionWrapper(Class<?> entityClass) {
        Field field = ReflectUtils.getField(entityClass, Version.class);
        if (field != null) {
            Version version = field.getAnnotation(Version.class);
            String javaColumn = field.getName();
            String dbColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn);

            VersionWrapper versionWrapper = new VersionWrapper();
            versionWrapper.setDbColumn(dbColumn);
            versionWrapper.setJavaColumn(javaColumn);
            versionWrapper.setInitValue(version.initValue());
            versionWrapper.setIncrement(version.increment());
            versionWrapper.buildSql();

            return versionWrapper;
        }
        return null;
    }

    /**
     * 获取表名
     *
     * @param entityClass
     * @return
     */
    private String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        return table != null ? table.name() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName());
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

            Class<?> clazz = this.getGenericType(type, idClass, entityClass);
            return clazz.getName();
        }

        return null;
    }

    private String getResultType(Method method, Class<?> entityClass) {
        Type type = method.getGenericReturnType();
        Class<?> clazz = this.getGenericType(type, null, entityClass);
        return clazz != null ? clazz.getName() : null;
    }

    protected Class<?> getGenericType(Type type, Class<?> idClass, Class<?> entityClass) {
        Type genericType = GenericUtils.getGenericType(type);
        if (genericType instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) genericType;
            if ("ENTITY".equals(typeVariable.getName())) {
                return entityClass;
            } else if ("ID".equals(typeVariable.getName())) {
                return idClass;
            }
        } else if (genericType instanceof Class<?>) {
            Class<?> clazz = (Class<?>) genericType;
            if (clazz == Map.class) {
                return entityClass;
            } else {
                return clazz;
            }
        }

        return null;
    }

    protected abstract List<XNode> readTemplate();

    abstract protected SqlWrapper instanceSqlWrapper();

    public SqlWrapper getSqlWrapper() {
        return sqlWrapper;
    }
}
