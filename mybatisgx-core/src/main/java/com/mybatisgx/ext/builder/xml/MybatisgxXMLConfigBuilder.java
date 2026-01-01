package com.mybatisgx.ext.builder.xml;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import org.apache.ibatis.builder.xml.PatchXMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.parsing.XPathParser;

import java.io.InputStream;
import java.util.Properties;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/24 12:10
 */
public class MybatisgxXMLConfigBuilder extends PatchXMLConfigBuilder {

    public MybatisgxXMLConfigBuilder(InputStream inputStream) {
        this(inputStream, null, null);
    }

    public MybatisgxXMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
        this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    public MybatisgxXMLConfigBuilder(XPathParser parser, String environment, Properties props) {
        super(new MybatisgxConfiguration());
        ErrorContext.instance().resource("SQL Mapper Configuration");
        this.configuration.setVariables(props);
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;
    }
}
