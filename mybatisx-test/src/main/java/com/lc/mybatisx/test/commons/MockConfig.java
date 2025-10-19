package com.lc.mybatisx.test.commons;

import com.lc.mybatisx.sql.TenantContextHolder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockConfig {

    @Bean
    public FilterRegistrationBean<?> aaaa() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setFilter((request, response, chain) -> {
            TenantContextHolder.set("111111111");
            chain.doFilter(request, response);
        });
        return filterRegistrationBean;
    }
}
