package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.context.MapperInfoContextHolder;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
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
public class MapperInfoHandler extends BasicInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(MapperInfoHandler.class);

    private static MethodInfoHandler methodInfoHandler = new MethodInfoHandler();

    public MapperInfo execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);
        return MapperInfoContextHolder.get(daoInterface);
    }

    public MapperInfo execute(Class<?> daoInterface) {
        MapperInfo mapperInfo = this.getMapperInfo(daoInterface);
        EntityInfo entityInfo = EntityInfoContextHolder.get(mapperInfo.getEntityClass());
        mapperInfo.setEntityInfo(entityInfo);

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
}
