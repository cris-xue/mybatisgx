package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.QueryEntity;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MapperInfoContextHolder;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.commons.lang3.reflect.TypeUtils;
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
        Type[] daoInterfaceParams = this.getDaoInterfaceParams(daoInterface);
        Class<?> idClass = null;
        Class<?> entityClass = null;
        Class<?> queryEntityClass = null;
        if (daoInterfaceParams.length == 2) {
            idClass = (Class<?>) daoInterfaceParams[1];
            entityClass = (Class<?>) daoInterfaceParams[0];
        }
        if (daoInterfaceParams.length == 3) {
            idClass = (Class<?>) daoInterfaceParams[2];
            queryEntityClass = (Class<?>) daoInterfaceParams[1];
            entityClass = (Class<?>) daoInterfaceParams[0];
            if (entityClass != queryEntityClass && entityClass != queryEntityClass.getAnnotation(QueryEntity.class).value()) {
                throw new MybatisgxException("mapper查询实体对应的实体和实体不一致");
            }
        }

        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setIdClass(idClass);
        mapperInfo.setEntityClass(entityClass);
        mapperInfo.setQueryEntityClass(queryEntityClass);
        mapperInfo.setDaoClass(daoInterface);
        mapperInfo.setNamespace(daoInterface.getName());
        return mapperInfo;
    }

    private Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        for (Type daoSuperInterfaceType : daoInterface.getGenericInterfaces()) {
            ParameterizedType daoSuperInterfaceClass = (ParameterizedType) daoSuperInterfaceType;
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (TypeUtils.isAssignable(daoInterface, Dao.class)) {
                return daoInterfaceParams;
            }
        }
        logger.info("{} un extend {}", SimpleDao.class.getName());
        return null;
    }

    private Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            throw new MybatisgxException("%s namespace not found", namespace);
        }
    }
}
