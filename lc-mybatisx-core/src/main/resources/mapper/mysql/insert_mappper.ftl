<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${sqlWrapper.parameterType}">
        insert into ${sqlWrapper.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list sqlWrapper.returnColumn?keys as key>
                <if test="${key} != null">
                    ${sqlWrapper.returnColumn[key]},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list sqlWrapper.returnColumn?keys as key>
                <if test="${key} != null">
                    ${r'#{'} ${key} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

</mapper>