package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.QueryEntity;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MapperInfoContextHolder;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.dao.SelectDao;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * mapper解析处理器
 *
 * @author：薛承城
 */
public class MapperInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(MapperInfoHandler.class);

    public MapperInfo execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = this.getDaoInterface(namespace);
        return MapperInfoContextHolder.get(daoInterface);
    }

    public MapperInfo execute(Class<?> daoInterface) {
        MapperInfo mapperInfo = this.getMapperInfo(daoInterface);
        mapperInfo.setEntityInfo(EntityInfoContextHolder.get(mapperInfo.getEntityClass()));
        // 如果只是继承了CrudDao，那么queryEntityClass就是空
        if (mapperInfo.getQueryEntityClass() != null) {
            mapperInfo.setQueryEntityInfo(EntityInfoContextHolder.get(mapperInfo.getQueryEntityClass()));
        }
        MethodInfoHandler methodInfoHandler = MybatisgxObjectFactory.get(MethodInfoHandler.class);
        List<MethodInfo> methodInfoList = methodInfoHandler.execute(mapperInfo, daoInterface);
        mapperInfo.setMethodInfoList(methodInfoList);
        return mapperInfo;
    }

    public MapperInfo getMapperInfo(Class<?> daoInterface) {
        DaoInterfaceParamContext context = this.getDaoInterfaceParams(daoInterface);
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setIdClass(context.getIdClass());
        mapperInfo.setEntityClass(context.getEntityClass());
        mapperInfo.setQueryEntityClass(context.getQueryEntityClass());
        mapperInfo.setDaoClass(daoInterface);
        mapperInfo.setNamespace(daoInterface.getName());
        return mapperInfo;
    }

    private Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            throw new MybatisgxException("%s namespace not found", namespace);
        }
    }

    private DaoInterfaceParamContext getDaoInterfaceParams(Type daoInterface) {
        DaoInterfaceParamContext context = new DaoInterfaceParamContext();
        Class<?> daoInterfaceClass = null;
        if (daoInterface instanceof Class) {
            daoInterfaceClass = (Class<?>) daoInterface;
        }
        if (daoInterface instanceof ParameterizedType) {
            daoInterfaceClass = (Class<?>) ((ParameterizedType) daoInterface).getRawType();
        }
        if (daoInterface == null) {
            return context;
        }
        for (Type basicDao : daoInterfaceClass.getGenericInterfaces()) {
            if (basicDao instanceof ParameterizedType) {
                ParameterizedType daoParameterizedType = (ParameterizedType) basicDao;
                Type daoRawType = daoParameterizedType.getRawType();
                Type[] daoInterfaceParams = daoParameterizedType.getActualTypeArguments();
                if (daoRawType == CurdDao.class) {
                    context.setIdClass((Class<?>) daoInterfaceParams[1]);
                    context.setEntityClass((Class<?>) daoInterfaceParams[0]);
                    return context;
                } else if (daoRawType == SelectDao.class) {
                    Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
                    Class<?> queryEntityClass = (Class<?>) daoInterfaceParams[1];
                    if (entityClass != queryEntityClass && entityClass != queryEntityClass.getAnnotation(QueryEntity.class).value()) {
                        throw new MybatisgxException("mapper查询实体对应的实体和实体不一致");
                    }
                    context.setEntityClass(entityClass);
                    context.setQueryEntityClass(queryEntityClass);
                    return context;
                } else if (daoRawType == SimpleDao.class) {
                    Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
                    Class<?> queryEntityClass = (Class<?>) daoInterfaceParams[1];
                    Class<?> idClass = (Class<?>) daoInterfaceParams[2];
                    if (entityClass != queryEntityClass && entityClass != queryEntityClass.getAnnotation(QueryEntity.class).value()) {
                        throw new MybatisgxException("mapper查询实体对应的实体和实体不一致");
                    }
                    context.setIdClass(idClass);
                    context.setEntityClass(entityClass);
                    context.setQueryEntityClass(queryEntityClass);
                    return context;
                } else {
                    return this.getDaoInterfaceParams(basicDao);
                }
            }
        }
        return context;
    }

    private static class DaoInterfaceParamContext {

        private Class<?> idClass;

        private Class<?> entityClass;

        private Class<?> queryEntityClass;

        public Class<?> getIdClass() {
            return idClass;
        }

        public void setIdClass(Class<?> idClass) {
            this.idClass = idClass;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }

        public void setEntityClass(Class<?> entityClass) {
            this.entityClass = entityClass;
        }

        public Class<?> getQueryEntityClass() {
            return queryEntityClass;
        }

        public void setQueryEntityClass(Class<?> queryEntityClass) {
            this.queryEntityClass = queryEntityClass;
        }
    }
}
