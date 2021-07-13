package com.lc.mybatisx.handler;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 12:57
 */
public class CURDMapper {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapperHandler.class);

    private static Map<String, Class<? extends AbstractMapperHandler>> mapperHandlerMap = new HashMap<>();

    static {
        mapperHandlerMap.put("insert", InsertMapperHandler.class);
        mapperHandlerMap.put("delete", DeleteMapperHandler.class);
        mapperHandlerMap.put("update", UpdateMapperHandler.class);
        mapperHandlerMap.put("find", QueryMapperHandler.class);
    }

    public static List<XNode> getNodeList(MapperBuilderAssistant builderAssistant, String namespace) {
        DaoParse daoParse = new DaoParse();
        daoParse.parse(builderAssistant);
        return null;
    }

    /*public static List<XNode> getNodeList(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        Class<?> idClass = (Class<?>) daoInterfaceParams[1];
        Method[] methods = daoInterface.getMethods();
        List<Method> methodList = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Configuration configuration = builderAssistant.getConfiguration();
            if (configuration.hasStatement(namespace + "." + method.getName())) {
                continue;
            }

            methodList.add(method);
        }

        List<XNode> xNodeList = new ArrayList<>();
        for (int i = 0; i < methodList.size(); i++) {
            Method method = methodList.get(i);
            SqlModel sqlModel = SqlModel.parse(method.getName());

            Class<? extends AbstractMapperHandler> abstractMapperHandler = mapperHandlerMap.get(sqlModel.getAction());
            if (abstractMapperHandler == null) {
                continue;
            }
            try {
                AbstractMapperHandler amh = abstractMapperHandler.newInstance();
                amh.init(sqlModel, namespace, method, daoInterfaceParams);
                List<XNode> xNode = amh.readTemplate();
                xNodeList.addAll(xNode);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return xNodeList;
    }*/

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

    public static List<String> parseMethodKeyword(String methodName) {
        // methodName = "findTop10ByIdAndNameIsOrAgeLessThanAndAgeLessThan";
        // updateByIdSelect
        // findById、findByIs、findByNameIsAndAgeIs
        // 创建 Pattern 对象
        String regex = "[a-z]+|By|And|Or|GroupBy|OrderBy|[A-Z][a-z]+|[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        // 创建 matcher 对象
        List<String> methodKeywordList = new ArrayList<>();
        Matcher matcher = pattern.matcher(methodName);
        while (matcher.find()) {
            methodKeywordList.add(matcher.group());
        }

        return methodKeywordList;
    }

}

