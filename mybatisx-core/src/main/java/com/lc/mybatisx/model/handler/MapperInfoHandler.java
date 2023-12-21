package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.MapperInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class MapperInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(MapperInfoHandler.class);

    public MapperInfo execute(Class<?> daoInterface) {
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setIdClass((Class<?>) daoInterfaceParams[1]);
        mapperInfo.setEntityClass((Class<?>) daoInterfaceParams[0]);
        return mapperInfo;
    }

    private Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
            ParameterizedTypeImpl daoSuperInterfaceClass = (ParameterizedTypeImpl) daoSuperInterfaceType;
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (TypeUtils.isAssignable(Dao.class, daoInterface)) {
                return daoInterfaceParams;
            }
        }
        logger.info("{} un extend {}", SimpleDao.class.getName());
        return null;
    }

}
