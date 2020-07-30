<?xml version="1.0" encoding="UTF-8" ?>
<#--<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">-->
<mapper namespace="${insertSqlWrapper.namespace}">

    <insert id="${insertSqlWrapper.methodName}" keyProperty="id" useGeneratedKeys="true" parameterType="${insertSqlWrapper.parameterType}">
        insert into ${insertSqlWrapper.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.modelWrapperList as mw>
                <if test="${mw.entityColumn} != null">
                    ${mw.dbColumn},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.modelWrapperList as mw>
                <if test="${mw.entityColumn} != null">
                    ${r'#{'} ${mw.entityColumn} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

</mapper>