<#macro whereTree ww>
    <#if (ww)??>
        ${ww.field} ${ww.operation} ${ww.value}
        <#if (ww.whereWrapper)??>
            ${ww.linkOp}
            <@whereTree ww=ww.whereWrapper/>
        </#if>
    </#if>
</#macro>