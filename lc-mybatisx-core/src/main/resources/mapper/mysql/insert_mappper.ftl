<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${insertSqlWrapper.parameterType}">
        insert into ${insertSqlWrapper.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.dbColumn?keys as key>
                <if test="${key} != null">
                    ${insertSqlWrapper.dbColumn[key]},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list insertSqlWrapper.entityColumn?keys as key>
                <if test="${key} != null">
                    ${r'#{'} ${key} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

</mapper>