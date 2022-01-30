package com.lc.mybatisx.parse;

import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.handler.AbstractMapperHandler;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/12 14:33
 */
public class DaoInterfaceParse {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapperHandler.class);

    private static List<Class<?>> basicTypeList = new ArrayList<>();

    static {
        basicTypeList.add(int.class);
        basicTypeList.add(Integer.class);
        basicTypeList.add(Long.class);
        basicTypeList.add(String.class);
    }

    public InterfaceNode parse(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
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

        List<MethodNode> methodNodeList = parseMethod(daoInterface, interfaceNode);
        interfaceNode.setMethodNodeList(methodNodeList);

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

        Boolean basicType = isBasicType(clazz);
        if (!basicType) {
            typeParamNode.setAnnotations(clazz.getAnnotations());
            List<FieldNode> fieldNodeList = parseField(clazz);
            typeParamNode.setFieldNodeList(fieldNodeList);
        }

        return typeParamNode;
    }

    private List<MethodNode> parseMethod(Class<?> daoInterface, InterfaceNode interfaceNode) {
        Method[] methods = daoInterface.getMethods();

        List<MethodNode> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            MethodNode methodNode = new MethodNode();

            methodNode.setMethod(method);
            methodNode.setName(method.getName());
            methodNode.setMethodParamNodeList(parseMethodParam(interfaceNode, method));
            methodNode.setReturnNode(parseMethodReturn(interfaceNode, method));
            methodNode.setDynamic(method.getAnnotation(Dynamic.class));

            methodNodeList.add(methodNode);
        }

        return methodNodeList;
    }

    private List<FieldNode> parseField(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);

        List<FieldNode> fieldNodeList = new ArrayList<>();
        for (Field field : fields) {
            FieldNode fieldNode = new FieldNode();

            fieldNode.setType(field.getType());
            fieldNode.setName(field.getName());
            /*fieldNode.setId(field.getAnnotation(Id.class));
            fieldNode.setColumn(field.getAnnotation(Column.class));
            fieldNode.setLogicDelete(field.getAnnotation(LogicDelete.class));
            fieldNode.setVersion(field.getAnnotation(Version.class));*/

            fieldNodeList.add(fieldNode);
        }

        return fieldNodeList;
    }

    private List<MethodParamNode> parseMethodParam(InterfaceNode interfaceNode, Method method) {
        Parameter[] parameters = method.getParameters();
        List<MethodParamNode> methodParamNodeList = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Parameter parameter = parameters[i];
            Class<?> clazz = getMethodParamType(interfaceNode, parameter);

            Boolean basicType = isBasicType(clazz);
            methodParamNode.setBasicType(basicType);
            methodParamNode.setType(clazz);
            methodParamNode.setContainerType(getContainerType(parameter.getType()));
            if (!basicType) {
                methodParamNode.setFieldNodeList(parseField(clazz));
            }
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                methodParamNode.setName(param.value());
                methodParamNode.setParam(param);
            }
            Annotation[] annotations = parameter.getAnnotations();
            if (ArrayUtils.isNotEmpty(annotations)) {
                methodParamNode.setAnnotations(parameter.getAnnotations());
            }

            methodParamNodeList.add(methodParamNode);
        }

        return methodParamNodeList;
    }

    private ReturnNode parseMethodReturn(InterfaceNode interfaceNode, Method method) {
        Class<?> clazz = getMethodReturnType(interfaceNode, method);

        ReturnNode returnNode = new ReturnNode();
        returnNode.setType(clazz);
        Boolean basicType = isBasicType(clazz);
        returnNode.setBasicType(basicType);
        returnNode.setContainerType(getContainerType(clazz));
        if (!basicType) {
            returnNode.setFieldNodeList(parseField(clazz));
        }
        return returnNode;
    }

    private Class<?> getMethodParamType(InterfaceNode interfaceNode, Parameter parameter) {
        Type type = parameter.getParameterizedType();
        return getMethodType(interfaceNode, type);
    }

    private Class<?> getMethodReturnType(InterfaceNode interfaceNode, Method method) {
        Type type = method.getGenericReturnType();
        return getMethodType(interfaceNode, type);
    }

    private Class<?> getMethodType(InterfaceNode interfaceNode, Type type) {
        Type actualType = GenericUtils.getGenericType(type);
        String actualTypeName = actualType.getTypeName();

        TypeParamNode idTypeParamNode = interfaceNode.getIdTypeParamNode();
        String idTypeParamName = idTypeParamNode.getName();

        TypeParamNode entityTypeParamNode = interfaceNode.getEntityTypeParamNode();
        String entityTypeParamName = entityTypeParamNode.getName();

        if (actualTypeName.equals(idTypeParamName)) {
            return idTypeParamNode.getType();
        } else if (actualTypeName.equals(entityTypeParamName)) {
            return entityTypeParamNode.getType();
        } else {
            return (Class<?>) actualType;
        }
    }

    private Class<?> getContainerType(Type type) {
        if (TypeUtils.isAssignable(Collection.class, type)) {
            return Collection.class;
        } else if (TypeUtils.isAssignable(Map.class, type)) {
            return Map.class;
        }
        return null;
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

    private Boolean isBasicType(Type type) {
        for (Class<?> bt : basicTypeList) {
            if (type == bt) {
                return true;
            }
        }
        return false;
    }

}
