package com.mybatisgx.template;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/4/1 21:08
 */
public class MybatisXmlHelper {

    private static final String IS_NOT_EMPTY = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%s)";

    public static Element buildTrimOrIfElement(MethodInfo methodInfo, ColumnInfo columnInfo, Element trimElement, String testExpression) {
        if (methodInfo.getDynamic() && columnInfo.getGenerateValue() == null) {
            return MybatisXmlHelper.buildIfElement(trimElement, testExpression);
        }
        return trimElement;
    }

    public static Element buildIfElement(Element parentElement, String testExpression) {
        Element ifElement = parentElement.addElement("if");
        ifElement.addAttribute("test", testExpression);
        return ifElement;
    }

    public static Element buildBindElement(String name, String value) {
        Element bindElement = DocumentHelper.createElement("bind");
        bindElement.addAttribute("name", name);
        bindElement.addAttribute("value", value);
        return bindElement;
    }

    public static Element buildForeachElement(String collection) {
        Element foreachElement = DocumentHelper.createElement("foreach");
        foreachElement.addAttribute("index", "index");
        foreachElement.addAttribute("item", "item");
        foreachElement.addAttribute("collection", collection);
        foreachElement.addAttribute("open", "(");
        foreachElement.addAttribute("close", ")");
        foreachElement.addAttribute("separator", ",");
        foreachElement.addText("#{item}");
        return foreachElement;
    }

    public static Element buildTrimElement(String prefix, String suffix, String suffixOverrides) {
        Element trimElement = DocumentHelper.createElement("trim");
        trimElement.addAttribute("prefix", prefix);
        trimElement.addAttribute("suffix", suffix);
        trimElement.addAttribute("suffixOverrides", suffixOverrides);
        return trimElement;
    }

    public static String getTestExpression(List<String> pathItemList) {
        List<String> isNotEmptyList = new ArrayList(5);
        List<String> lastPathItem = new ArrayList(5);
        for (int i = 0; i < pathItemList.size(); i++) {
            String isNotEmptyIndex = "%" + (i + 1) + "$s";
            lastPathItem.add(isNotEmptyIndex);
            String lastPathItemString = StringUtils.join(lastPathItem, ".");
            isNotEmptyList.add(String.format(IS_NOT_EMPTY, lastPathItemString));
        }
        String[] paths = pathItemList.toArray(new String[pathItemList.size()]);
        return String.format(StringUtils.join(isNotEmptyList, " and "), paths);
    }

    public static String getValueExpression(List<String> pathItemList, ColumnInfo columnInfo) {
        String valuePath = StringUtils.join(pathItemList, ".");
        return String.format("#{%s%s},", valuePath, buildTypeHandler(columnInfo));
    }

    private static String buildTypeHandler(ColumnInfo columnInfo) {
        String typeHandler = columnInfo.getTypeHandler();
        if (StringUtils.isNotBlank(typeHandler)) {
            return String.format(", typeHandler=%s", typeHandler);
        }
        return "";
    }
}
