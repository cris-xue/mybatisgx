package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.handler.AbstractMapperHandler;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ObjectUtils;
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

        MapperNode mapperNode = new MapperNode();
        mapperNode.setInterfaceClass(daoInterface);
        mapperNode.setTypeParamNodeList(parseTypeParam(daoInterface));
        mapperNode.setActionNodeList(parseAction(daoInterface, mapperNode.getTypeParamNodeList()));

        return mapperNode;
    }

    public List<TypeParamNode> parseTypeParam(Class<?> daoInterface) {
        List<TypeParamNode> typeParamNodeList = new ArrayList<>();

        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

        TypeParamNode idTypeParamNode = new TypeParamNode();
        idTypeParamNode.setName("ID");
        idTypeParamNode.setType((Class<?>) daoInterfaceParams[1]);

        TypeParamNode entityTypeParamNode = new TypeParamNode();
        entityTypeParamNode.setName("ENTITY");
        entityTypeParamNode.setType((Class<?>) daoInterfaceParams[0]);
        entityTypeParamNode.setFieldNodeList(parseField(daoInterfaceParams[0]));

        typeParamNodeList.add(idTypeParamNode);
        typeParamNodeList.add(entityTypeParamNode);

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

    private List<MethodParamNode> parseMethodParam(Method method, List<TypeParamNode> typeParamNodeList) {
        Parameter[] parameters = method.getParameters();

        List<MethodParamNode> methodParamNodeList = new ArrayList<>();
        for (Parameter parameter : parameters) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Type type = parameter.getParameterizedType();
            String actualTypeName = type.getTypeName();
            Class<?> clazz = getActualMethodParam(typeParamNodeList, type);

            methodParamNode.setName(actualTypeName);
            methodParamNode.setType(clazz);
            methodParamNode.setFieldNodeList(parseField(clazz));
            methodParamNode.setParam(parameter.getAnnotation(Param.class));

            methodParamNodeList.add(methodParamNode);
        }

        return methodParamNodeList;
    }

    private ResultNode parseMethodResult(Method method, List<TypeParamNode> typeParamNodeList) {
        Type type = method.getGenericReturnType();
        Class<?> clazz = getActualMethodParam(typeParamNodeList, type);

        ResultNode resultNode = new ResultNode();
        resultNode.setType(clazz);
        resultNode.setFieldNodeList(parseField(clazz));

        return resultNode;
    }

    public Class<?> getActualMethodParam(List<TypeParamNode> typeParamNodeList, Type type) {
        Type actualType = GenericUtils.getGenericType(type);
        String actualTypeName = actualType.getTypeName();
        List<TypeParamNode> filterTypeParamNode = typeParamNodeList
                .stream()
                .filter(i -> actualTypeName.equals(i.getName()))
                .collect(Collectors.toList());

        return ObjectUtils.isNotEmpty(filterTypeParamNode) ? filterTypeParamNode.get(0).getType() : (Class<?>) actualType;
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
