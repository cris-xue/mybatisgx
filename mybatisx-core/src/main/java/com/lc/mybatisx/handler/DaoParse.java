package com.lc.mybatisx.handler;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/12 14:33
 */
public class DaoParse {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapperHandler.class);

    private static List<Class<?>> basicTypeList = new ArrayList<>();

    static {
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
        interfaceNode.setTypeParamNodeList(parseTypeParam(daoInterface));
        interfaceNode.setMethodNodeList(parseAction(daoInterface, interfaceNode.getTypeParamNodeList()));

        return interfaceNode;
    }

    public List<TypeParamNode> parseTypeParam(Class<?> daoInterface) {
        List<TypeParamNode> typeParamNodeList = new ArrayList<>();

        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);

        TypeParamNode idTypeParamNode = new TypeParamNode();
        idTypeParamNode.setName("ID");
        idTypeParamNode.setType((Class<?>) daoInterfaceParams[1]);

        TypeParamNode entityTypeParamNode = new TypeParamNode();
        entityTypeParamNode.setName("ENTITY");
        Class<?> clazz = (Class<?>) daoInterfaceParams[0];
        entityTypeParamNode.setType(clazz);
        Boolean basicType = isBasicType(clazz);
        if (!basicType) {
            entityTypeParamNode.setFieldNodeList(parseField(clazz));
        }

        typeParamNodeList.add(idTypeParamNode);
        typeParamNodeList.add(entityTypeParamNode);

        return typeParamNodeList;
    }

    public List<MethodNode> parseAction(Class<?> daoInterface, List<TypeParamNode> typeParamNodeList) {
        Method[] methods = daoInterface.getMethods();

        List<MethodNode> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            MethodNode methodNode = new MethodNode();

            methodNode.setMethod(method);
            methodNode.setName(method.getName());
            methodNode.setMethodParamNodeList(parseMethodParam(method, typeParamNodeList));
            methodNode.setReturnNode(parseMethodResult(method, typeParamNodeList));
            methodNode.setDynamic(method.getAnnotation(Dynamic.class));

            methodNodeList.add(methodNode);
        }

        return methodNodeList;
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

        for (int i = 0; i < parameters.length; i++) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Parameter parameter = parameters[i];
            Class<?> clazz = getActualMethodParam(typeParamNodeList, parameter.getParameterizedType());

            Boolean basicType = isBasicType(clazz);
            methodParamNode.setBasicType(basicType);
            methodParamNode.setType(clazz);
            methodParamNode.setContainerType(isContainerType(parameter.getType()));
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

    private ReturnNode parseMethodResult(Method method, List<TypeParamNode> typeParamNodeList) {
        Type type = method.getGenericReturnType();
        Class<?> clazz = getActualMethodParam(typeParamNodeList, type);

        ReturnNode returnNode = new ReturnNode();
        returnNode.setType(clazz);
        Boolean basicType = isBasicType(clazz);
        if (!basicType) {
            returnNode.setFieldNodeList(parseField(clazz));
        }
        return returnNode;
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

    private Class<?> isContainerType(Type type) {
        if (TypeUtils.isAssignable(Collection.class, type)) {
            return Collection.class;
        } else if (TypeUtils.isAssignable(Map.class, type)) {
            return Map.class;
        }
        return null;
    }

    public void parseMethodName(MethodNode methodNode) {

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

    private Boolean isBasicType(Class<?> clazz) {
        for (Class<?> bt : basicTypeList) {
            if (clazz == bt) {
                return true;
            }
        }
        return false;
    }

}
