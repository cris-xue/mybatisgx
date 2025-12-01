package com.mybatisgx.model.handler;

import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MapperInfoContextHolder;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.model.EntityInfo;
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
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
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
        EntityInfo entityInfo = EntityInfoContextHolder.get(mapperInfo.getEntityClass());
        mapperInfo.setEntityInfo(entityInfo);

        MethodInfoHandler methodInfoHandler = MybatisgxObjectFactory.get(MethodInfoHandler.class);
        List<MethodInfo> methodInfoList = methodInfoHandler.execute(mapperInfo, daoInterface);
        mapperInfo.setMethodInfoList(methodInfoList);
        return mapperInfo;
    }

    public MapperInfo getMapperInfo(Class<?> daoInterface) {
        Type[] daoInterfaceParams = this.getDaoInterfaceParams(daoInterface);
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];

        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setIdClass(idClass);
        mapperInfo.setEntityClass(entityClass);
        mapperInfo.setDaoClass(daoInterface);
        mapperInfo.setNamespace(daoInterface.getName());
        return mapperInfo;
    }

    private Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
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
            throw new RuntimeException("namespace not found");
        }
    }
}
