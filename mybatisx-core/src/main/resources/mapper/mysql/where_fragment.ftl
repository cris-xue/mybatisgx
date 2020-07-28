<where>
    <#macro whereTree ww>
        <#if ww??>
            ${ww.field} ${ww.operation} ${ww.value}
            <#if ww.whereWrapper??>
                ${ww.linkOp}
                <@whereTree ww = ww.whereTree/>
            </#if>
        </#if>
    </#macro>
    <!-- 调用宏 生成递归树 -->
    <@whereTree ww = updateSqlWrapper.whereWrapper/>
</where>