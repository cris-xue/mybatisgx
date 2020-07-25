<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${updateSqlWrapper.namespace}">

    <update id="${updateSqlWrapper.methodName}" <#if updateSqlWrapper.parameterType??>parameterType="${updateSqlWrapper.parameterType}"</#if>>
        update ${updateSqlWrapper.tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list updateSqlWrapper.modelWrapperList as mw>
                <if test="${mw.entityColumn} != null">
                    ${mw.dbColumn} = ${r'#{'} ${mw.entityColumn} ${r'}'},
                </if>
            </#list>
        </trim>
        where id = ${r'#{id}'}
    </update>

</mapper>