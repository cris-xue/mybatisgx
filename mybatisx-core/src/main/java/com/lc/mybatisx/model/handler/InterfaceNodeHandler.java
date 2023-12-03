package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.Table;
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
        Class<?> daoInterface = getDaoInterface(namespace);

        InterfaceNode interfaceNode = new InterfaceNode();
        interfaceNode.setInterfaceName(daoInterface.getName());
        interfaceNode.setInterfaceClass(daoInterface);

        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        interfaceNode.setIdNode(parseIdNode("ID", daoInterfaceParams[1]));
        interfaceNode.setEntityNode(parseEntityNode("ENTITY", daoInterfaceParams[0]));
        interfaceNode.setTableName(interfaceNode.getEntityNode().getTable().name());

        return interfaceNode;
    }

    private IdNode parseIdNode(String name, Type idClass) {
        IdNode idNode = (IdNode) parseStructNode(new IdNode(), idClass);
        idNode.setName(name);
        return idNode;
    }

    private EntityNode parseEntityNode(String name, Type entityClass) {
        EntityNode entityNode = (EntityNode) parseStructNode(new EntityNode(), entityClass);
        entityNode.setName(name);

        /*EntityTypeParamNode entityTypeParamNode = new EntityTypeParamNode();
        BeanUtils.copyProperties(entityNode, entityTypeParamNode);*/

        Class<?> clazz = (Class<?>) entityClass;
        entityNode.setTable(clazz.getAnnotation(Table.class));

        List<FieldNode> fieldNodeList = null; //entityTypeParamNode.getFieldNodeList();
        /*fieldNodeList.forEach(fn -> {
         *//*LogicDelete logicDelete = fn.getLogicDelete();
            if (logicDelete != null) {
                entityTypeParamNode.setLogicDelete(logicDelete);
            }

            Version version = fn.getVersion();
            if (version != null) {
                entityTypeParamNode.setVersion(version);
            }*//*
        });*/

        return entityNode;
    }

    private StructNode parseStructNode(StructNode structNode, Type daoInterfaceParamClass) {
        Class<?> clazz = (Class<?>) daoInterfaceParamClass;
        structNode.setType(clazz);
        Boolean basicType = structNodeHandler.isBasicType(clazz);
        if (!basicType) {
            structNode.setAnnotations(clazz.getAnnotations());
            List<FieldNode> fieldNodeList = structNodeHandler.parseField(clazz);
            structNode.setFieldNodeList(fieldNodeList);
        }
        return structNode;
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
