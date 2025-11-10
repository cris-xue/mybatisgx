package com.mybatisgx.converter;

import com.mybatisgx.scripting.MetaObjectHandler;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

@ConfigurationPropertiesBinding
public class MetaObjectHandlerConverter implements Converter<String, MetaObjectHandler> {

    @Override
    public MetaObjectHandler convert(String s) {
        try {
            return (MetaObjectHandler) Class.forName(s).newInstance();
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
