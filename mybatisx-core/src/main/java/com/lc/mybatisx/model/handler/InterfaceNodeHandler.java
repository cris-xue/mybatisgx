package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Table;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.EntityTypeParamNode;
import com.lc.mybatisx.model.FieldNode;
import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.TypeParamNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class InterfaceNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceNodeHandler.class);

    private StructNodeHandler structNodeHandler = new StructNodeHandler();

    public InterfaceNode execute(String namespace) {
        return parseMapper(namespace);
    }

    public InterfaceNode parseMapper(String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);

        InterfaceNode interfaceNode = new InterfaceNode();
        interfaceNode.setName(daoInterface.getName());
        interfaceNode.setInterfaceClass(daoInterface);

        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        interfaceNode.setIdTypeParamNode(parseTypeParam("ID", daoInterfaceParams[1]));
        interfaceNode.setEntityTypeParamNode(parseEntityTypeParam("ENTITY", daoInterfaceParams[0]));

        return interfaceNode;
    }

    private EntityTypeParamNode parseEntityTypeParam(String name, Type daoInterface) {
        TypeParamNode typeParamNode = parseTypeParam(name, daoInterface);

        EntityTypeParamNode entityTypeParamNode = new EntityTypeParamNode();
        BeanUtils.copyProperties(typeParamNode, entityTypeParamNode);

        Class<?> clazz = (Class<?>) daoInterface;
        entityTypeParamNode.setTable(clazz.getAnnotation(Table.class));

        List<FieldNode> fieldNodeList = entityTypeParamNode.getFieldNodeList();
        fieldNodeList.forEach(fn -> {
            /*LogicDelete logicDelete = fn.getLogicDelete();
            if (logicDelete != null) {
                entityTypeParamNode.setLogicDelete(logicDelete);
            }

            Version version = fn.getVersion();
            if (version != null) {
                entityTypeParamNode.setVersion(version);
            }*/
        });

        return entityTypeParamNode;
    }

    private TypeParamNode parseTypeParam(String name, Type daoInterface) {
        Class<?> clazz = (Class<?>) daoInterface;

        TypeParamNode typeParamNode = new TypeParamNode();
        typeParamNode.setName(name);
        typeParamNode.setType(clazz);

        Boolean basicType = structNodeHandler.isBasicType(clazz);
        if (!basicType) {
            typeParamNode.setAnnotations(clazz.getAnnotations());
            List<FieldNode> fieldNodeList = structNodeHandler.parseField(clazz);
            typeParamNode.setFieldNodeList(fieldNodeList);
        }

        return typeParamNode;
    }

    private static Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Type[] getDaoInterfaceParams(Class<?> daoInterface) {
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
