package com.lc.mybatisx.test.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 18:00
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("Authorization").description("AccessToken令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("demo")
                .apiInfo(getApiInfo())
                .select()
                //设置basePackage会将包下的所有被@Api标记类的所有方法作为api
                // .apis(RequestHandlerSelectors.basePackage("com.es.wms.tenant.controller"))
                //只有标记了@ApiOperation的方法才会暴露出给swagger
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.regex("/api/.*")).build()
                .globalOperationParameters(pars);
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("API接口文档")
                .description("swagger2 demo api")
                .termsOfServiceUrl("http://localhost/swagger-ui.html")
                .version("1.0")
                .contact(new Contact("Kiana", "http://localhost/swagger-ui.html", "wwssyyzzcc@qq.com"))
                .build();
    }

}