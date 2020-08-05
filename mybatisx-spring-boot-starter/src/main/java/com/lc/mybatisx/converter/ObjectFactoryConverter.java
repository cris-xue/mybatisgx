package com.lc.mybatisx.converter;

import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

@ConfigurationPropertiesBinding
public class ObjectFactoryConverter implements Converter<String, ObjectWrapperFactory> {

    @Override
    public ObjectWrapperFactory convert(String s) {
        try {
            return (ObjectWrapperFactory) Class.forName(s).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
