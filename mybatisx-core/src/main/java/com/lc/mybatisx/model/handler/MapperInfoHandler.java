package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class MapperInfoHandler extends BasicInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(MapperInfoHandler.class);

    private static TableInfoHandler tableInfoHandler = new TableInfoHandler();
    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private static ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();
    private static MethodInfoHandler methodInfoHandler = new MethodInfoHandler();

    public MapperInfo execute(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        Class<?> daoInterface = getDaoInterface(namespace);
        MapperInfo mapperInfo = getMapperInfo(daoInterface);
        TableInfo tableInfo = tableInfoHandler.execute(mapperInfo);
        mapperInfo.setTableInfo(tableInfo);
        /*ResultMapInfo resultMapInfo = resultMapInfoHandler.execute(mapperInfo.getEntityClass());
        mapperInfo.setResultMapInfo(resultMapInfo);*/

        List<MethodInfo> methodInfoList = methodInfoHandler.execute(mapperInfo, daoInterface);
        mapperInfo.setMethodInfoList(methodInfoList);

        return mapperInfo;
    }

    public MapperInfo getMapperInfo(Class<?> daoInterface) {
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];

        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setIdClass(idClass);
        mapperInfo.setEntityClass(entityClass);
        mapperInfo.setNamespace(daoInterface.getName());
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
