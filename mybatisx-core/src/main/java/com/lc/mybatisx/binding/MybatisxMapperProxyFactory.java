package com.lc.mybatisx.binding;

import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MapperProxyFactory<T></>
 *
 * @param <T>
 */
public class MybatisxMapperProxyFactory<T> {

    private final Class<T> mapperInterface;
    private final Map<Method, MybatisxMapperProxy.MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

    public MybatisxMapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MybatisxMapperProxy.MapperMethodInvoker> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MybatisxMapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession) {
        final MybatisxMapperProxy<T> mapperProxy = new MybatisxMapperProxy<>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }

}
