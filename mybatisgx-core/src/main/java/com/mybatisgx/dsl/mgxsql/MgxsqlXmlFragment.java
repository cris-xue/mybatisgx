package com.mybatisgx.dsl.mgxsql;

/**
 * mgxsql XML 片段工具类，集中管理 MyBatis XML 标签的生成逻辑
 * <p>
 * 统一 <if>、<foreach>、<bind>、<where>、<set>、#{param} 等片段的输出格式，
 * 避免在 MgxsqlScanner 和 MgxsqlConditionBodyProcessor 中分散手动拼接
 *
 * @author 薛承城
 * @date 2026/7/10
 */
public final class MgxsqlXmlFragment {

    private MgxsqlXmlFragment() {
    }

    /**
     * 生成 &lt;if&gt; 标签
     *
     * @param testExpr OGNL 测试表达式
     * @param body     标签体内容
     * @return 完整的 &lt;if&gt; 标签字符串
     */
    public static String ifTag(String testExpr, String body) {
        return "<if test=\"" + testExpr + "\"> " + body + "</if>";
    }

    /**
     * 生成简单类型的 &lt;foreach&gt; 标签（item 固定为 "item"，body 固定为 #{item}）
     *
     * @param collectionName 集合参数名
     * @return 完整的 &lt;foreach&gt; 标签字符串
     */
    public static String foreachSimple(String collectionName) {
        return "<foreach item=\"item\" collection=\"" + collectionName
                + "\" open=\"(\" close=\")\" separator=\",\">#{item}</foreach>";
    }

    /**
     * 生成复杂类型的 &lt;foreach&gt; 标签（自定义 item 和 body）
     *
     * @param itemName       迭代变量名
     * @param collectionName 集合参数名
     * @param valueExpr      迭代体中的值表达式（如 #{item.id}）
     * @return 完整的 &lt;foreach&gt; 标签字符串
     */
    public static String foreachComplex(String itemName, String collectionName, String valueExpr) {
        return "<foreach item=\"" + itemName + "\" collection=\"" + collectionName
                + "\" open=\"(\" close=\")\" separator=\",\">" + valueExpr + "</foreach>";
    }

    /**
     * 生成 &lt;bind&gt; 标签
     *
     * @param name  绑定变量名
     * @param value 绑定值表达式
     * @return 完整的 &lt;bind&gt; 标签字符串
     */
    public static String bindTag(String name, String value) {
        return "<bind name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    /**
     * 生成 MyBatis 参数引用
     *
     * @param name 参数名
     * @return #{name} 形式的参数引用
     */
    public static String paramRef(String name) {
        return "#{" + name + "}";
    }

    /**
     * 生成 &lt;where&gt; 开始标签
     */
    public static String openWhere() {
        return "<where>";
    }

    /**
     * 生成 &lt;choose&gt; 开始标签
     */
    public static String chooseOpen() {
        return "<choose>";
    }

    /**
     * 生成 &lt;/choose&gt; 结束标签
     */
    public static String chooseClose() {
        return "</choose>";
    }

    /**
     * 生成 &lt;when&gt; 分支标签
     *
     * @param testExpr guard 处理后的 OGNL 表达式
     * @param body     分支体内容（已翻译）
     * @return 完整的 &lt;when&gt; 标签字符串
     */
    public static String whenTag(String testExpr, String body) {
        return "<when test=\"" + testExpr + "\"> " + body + "</when>";
    }

    /**
     * 生成 &lt;otherwise&gt; 兜底分支标签
     *
     * @param body 分支体内容（已翻译）
     * @return 完整的 &lt;otherwise&gt; 标签字符串
     */
    public static String otherwiseTag(String body) {
        return "<otherwise> " + body + "</otherwise>";
    }

    /**
     * 生成 &lt;/where&gt; 结束标签
     */
    public static String closeWhere() {
        return "</where>";
    }

    /**
     * 生成 &lt;set&gt; 开始标签
     */
    public static String openSet() {
        return "<set>";
    }

    /**
     * 生成 &lt;/set&gt; 结束标签
     */
    public static String closeSet() {
        return "</set>";
    }
}
