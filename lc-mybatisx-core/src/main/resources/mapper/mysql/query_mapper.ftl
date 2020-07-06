<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <#--扩展方法-->
    <#list sqlWrapperList as sqlWrapper>
        <${sqlWrapper.tagName} id="${sqlWrapper.idName}" resultType="${sqlWrapper.returnType}">
            select
                <#list sqlWrapper.returnColumn?keys as key>
                    ${sqlWrapper.returnColumn[key]},
                </#list>
            from ${sqlWrapper.tableName}
            <where>
                <#list sqlWrapper.whereColumns as whereColumn>
                    ${whereColumn}
                </#list>
            </where>
        </${sqlWrapper.tagName}>
    </#list>

</mapper>