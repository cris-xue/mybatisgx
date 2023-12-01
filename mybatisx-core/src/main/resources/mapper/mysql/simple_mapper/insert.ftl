<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <insert id="insert" keyProperty="id" useGeneratedKeys="true" parameterType="${methodNode.methodParamNode.typeName}">
        insert into ${interfaceNode.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list methodNode.methodParamNode.fieldNodeList as fieldNode>
                ${fieldNode.name},
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list methodNode.methodParamNode.fieldNodeList as fieldNode>
                ${r'#{'} ${fieldNode.name} ${r'}'},
            </#list>
        </trim>
    </insert>

</mapper>