package com.lc.mybatisx.handler;

import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
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
        mapperHandlerMap.put("find", QueryMapperHandler.class);
    }

    public static List<XNode> getNodeList(MapperBuilderAssistant builderAssistant, String namespace) {
        Class<?> daoInterface = getDaoInterface(namespace);
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
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

        // InsertMapperHandler insertMapperHandler = new InsertMapperHandler(builderAssistant, namespace);
        // List<XNode> insertList = insertMapperHandler.readTemplate();

        for (int i = 0; i < methodList.size(); i++) {
            Method method = methodList.get(i);
            List<String> methodKeywordList = parseMethodKeyword(method.getName());

            Class<? extends AbstractMapperHandler> abstractMapperHandler = mapperHandlerMap.get(methodKeywordList.get(0));
            if (abstractMapperHandler == null) {
                continue;
            }
            try {
                AbstractMapperHandler amh = abstractMapperHandler.newInstance();
                amh.init(namespace, methodList, daoInterfaceParams);
                amh.readTemplate();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        QueryMapperHandler queryMapperHandler = new QueryMapperHandler(namespace, methodList, daoInterfaceParams);
        List<XNode> queryList = queryMapperHandler.readTemplate();

        UpdateMapperHandler updateMapperHandler = new UpdateMapperHandler(builderAssistant, namespace);
        List<XNode> updateList = updateMapperHandler.readTemplate();

        DeleteMapperHandler deleteMapperHandler = new DeleteMapperHandler(builderAssistant, namespace);
        List<XNode> deleteList = deleteMapperHandler.readTemplate();

        List<XNode> curdList = new ArrayList<>();
        // curdList.addAll(insertList);
        curdList.addAll(queryList);
        curdList.addAll(updateList);
        curdList.addAll(deleteList);
        return curdList;
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

