package com.lc.mybatisx.model;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.handler.AbstractMapperHandler;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
        mapperNode.setTypeParamNodeList(parseTypeParam(daoInterfaceParams));
        mapperNode.setActionNodeList(parseAction(daoInterface, mapperNode.getTypeParamNodeList()));

        return mapperNode;
    }

    public List<TypeParamNode> parseTypeParam(Type[] daoInterfaceParams) {
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];

        List<TypeParamNode> typeParamNodeList = new ArrayList<>();

        TypeParamNode idTypeParamNode = new TypeParamNode();
        idTypeParamNode.setName("ID");
        idTypeParamNode.setType(idClass);
        typeParamNodeList.add(idTypeParamNode);

        TypeParamNode classTypeParamNode = new TypeParamNode();
        classTypeParamNode.setName("ENTITY");
        classTypeParamNode.setType(entityClass);
        classTypeParamNode.setFieldNodeList(parseField(entityClass));
        typeParamNodeList.add(classTypeParamNode);

        return typeParamNodeList;
    }

    public List<ActionNode> parseAction(Class<?> daoInterface, List<TypeParamNode> typeParamNodeList) {
        Method[] methods = daoInterface.getMethods();

        List<ActionNode> actionNodeList = new ArrayList<>();
        for (Method method : methods) {
            ActionNode actionNode = new ActionNode();
            actionNode.setMethod(method);
            actionNode.setName(method.getName());
            actionNode.setMethodParamNodeList(parseMethodParam(method, typeParamNodeList));
            actionNode.setResultNode(parseMethodResult(method, typeParamNodeList));
            actionNode.setAnnotationList(Arrays.asList(method.getAnnotations()));

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
            fieldNode.setAnnotationList(Arrays.asList(field.getAnnotations()));

            fieldNodeList.add(fieldNode);
        }

        return fieldNodeList;
    }

    private List<MethodParamNode> parseMethodParam(Method method, List<TypeParamNode> typeParamNodeList) {
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
            methodParamNode.setAnnotationList(Arrays.asList(parameter.getAnnotations()));

            methodParamNodeList.add(methodParamNode);
        }

        return methodParamNodeList;
    }

    private ResultNode parseMethodResult(Method method, List<TypeParamNode> typeParamNodeList) {
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
