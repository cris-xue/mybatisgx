package com.lc.mybatisx.test;

import com.lc.mybatisx.Mybatisx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Mybatisx(basePackages = {"com.lc.mybatisx.test.dao"})
@SpringBootApplication(scanBasePackages = {"com.lc.mybatisx.test"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
