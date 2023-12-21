package com.lc.mybatisx.model.handler;

public class BasicInfoHandler {

    protected Class<?> getDaoInterface(String namespace) {
        try {
            return Class.forName(namespace);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getResultMap(Class<?> entityClass) {
        return entityClass.getSimpleName() + "FullColumnResultMap";
    }

}
