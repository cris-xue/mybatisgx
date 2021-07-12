package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.handler.AbstractMapperHandler;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/12 14:33
 */
public class DaoParse {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapperHandler.class);

    public MapperNode parse(MapperBuilderAssistant builderAssistant) {
        String namespace = builderAssistant.getCurrentNamespace();
        return parseMapper(namespace);
    }

    public MapperNode parseMapper(String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

        MapperNode mapperNode = new MapperNode();
        mapperNode.setInterfaceClass(daoInterface);
        mapperNode.setPrimaryKeyTypeParamNode(parseTypeParam("ID", daoInterfaceParams[1]));
        mapperNode.setEntityTypeParamNode(parseTypeParam("ENTITY", daoInterfaceParams[0]));
        mapperNode.setActionNodeList(parseAction(daoInterface, mapperNode.getEntityTypeParamNode(), mapperNode.getPrimaryKeyTypeParamNode()));

        return mapperNode;
    }

    public TypeParamNode parseTypeParam(String paramName, Type daoInterfaceParam) {
        TypeParamNode typeParamNode = new TypeParamNode();
        typeParamNode.setName(paramName);
        typeParamNode.setType((Class<?>) daoInterfaceParam);
        typeParamNode.setFieldNodeList(parseField(daoInterfaceParam));
        return typeParamNode;
    }

    public List<ActionNode> parseAction(Class<?> daoInterface, TypeParamNode entity, TypeParamNode pk) {
        Method[] methods = daoInterface.getMethods();

        List<ActionNode> actionNodeList = new ArrayList<>();
        for (Method method : methods) {
            ActionNode actionNode = new ActionNode();
            actionNode.setMethod(method);
            actionNode.setName(method.getName());
            actionNode.setMethodParamNodeList(parseMethodParam(method, entity, pk));
            actionNode.setResultNode(parseMethodResult(method, entity, pk));
            actionNode.setDynamic(method.getAnnotation(Dynamic.class));

            actionNodeList.add(actionNode);
        }

        return actionNodeList;
    }

    public List<FieldNode> parseField(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);

        List<FieldNode> fieldNodeList = new ArrayList<>();
        for (Field field : fields) {
            FieldNode fieldNode = new FieldNode();

            fieldNode.setType(field.getType());
            fieldNode.setName(field.getName());
            fieldNode.setId(field.getAnnotation(Id.class));
            fieldNode.setColumn(field.getAnnotation(Column.class));
            fieldNode.setLogicDelete(field.getAnnotation(LogicDelete.class));

            fieldNodeList.add(fieldNode);
        }

        return fieldNodeList;
    }

    private List<MethodParamNode> parseMethodParam(Method method, TypeParamNode entity, TypeParamNode pk) {
        List<TypeParamNode> typeParamNodeList = Arrays.asList(entity, pk);
        Parameter[] parameters = method.getParameters();

        List<MethodParamNode> methodParamNodeList = new ArrayList<>();
        for (Parameter parameter : parameters) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Type type = parameter.getParameterizedType();
            Type actualType = GenericUtils.getGenericType(type);
            String actualTypeName = actualType.getTypeName();

            Class<?> clazz;
            if ("ID".equals(actualTypeName) || "ENTITY".equals(actualTypeName)) {
                TypeParamNode typeParamNode = typeParamNodeList.stream().filter(i -> actualTypeName.equals(i.getName())).collect(Collectors.toList()).get(0);
                clazz = typeParamNode.getType();
            } else {
                clazz = parameter.getType();
            }
            methodParamNode.setName(actualTypeName);
            methodParamNode.setFieldNodeList(parseField(clazz));
            methodParamNode.setParam(parameter.getAnnotation(Param.class));

            methodParamNodeList.add(methodParamNode);
        }

        return methodParamNodeList;
    }

    private ResultNode parseMethodResult(Method method, TypeParamNode entity, TypeParamNode pk) {
        List<TypeParamNode> typeParamNodeList = Arrays.asList(entity, pk);

        Type type = method.getGenericReturnType();
        Type actualType = GenericUtils.getGenericType(type);
        String actualTypeName = actualType.getTypeName();

        TypeParamNode typeParamNode;
        Class<?> clazz;
        if ("ID".equals(actualTypeName) || "ENTITY".equals(actualTypeName)) {
            typeParamNode = typeParamNodeList.stream().filter(i -> actualTypeName.equals(i.getName())).collect(Collectors.toList()).get(0);
            clazz = typeParamNode.getType();
        } else {
            clazz = (Class<?>) actualType;
        }

        ResultNode resultNode = new ResultNode();
        resultNode.setType(clazz);
        resultNode.setFieldNodeList(parseField(clazz));

        return resultNode;
    }

    public void parseMethodName(ActionNode actionNode) {

    }

    protected static Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static Type[] getDaoInterfaceParams(Class<?> daoInterface) {
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
