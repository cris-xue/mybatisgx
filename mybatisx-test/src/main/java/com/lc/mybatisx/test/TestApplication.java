package com.lc.mybatisx.test;

import com.lc.mybatisx.MybatisxScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MybatisxScan(entityBasePackages = "com.lc.mybatisx.*.model.entity", daoBasePackages = "com.lc.mybatisx.*.dao")
@SpringBootApplication(scanBasePackages = {"com.lc.mybatisx.test"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
        // localhost:8083/swagger-ui.html
    }

}
