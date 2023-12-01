<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <insert id="insertSelective" keyProperty="id" useGeneratedKeys="true" parameterType="${parameterType}">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${columnMap[key]},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columnMap?keys as key>
                <if test="${key} != null">
                    ${r'#{'} ${key} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

    <!--
    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${methodNode.methodParamNodeList[0].type}">
        insert into ${interfaceNode.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list methodNode.methodParamNodeList[0].fieldNodeList as fieldNode>
                <if test="${fieldNode.name} != null">
                    ${fieldNode.name},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list methodNode.methodParamNodeList[0].fieldNodeList as fieldNode>
                <if test="${fieldNode.name} != null">
                    ${r'#{'} ${fieldNode.name} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>
    -->

</mapper>