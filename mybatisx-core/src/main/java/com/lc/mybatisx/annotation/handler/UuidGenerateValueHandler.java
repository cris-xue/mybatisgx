package com.lc.mybatisx.annotation.handler;

import java.util.UUID;

public class UuidGenerateValueHandler implements GenerateValueHandler<String> {

    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }

}
