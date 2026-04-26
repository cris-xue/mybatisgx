package com.mybatisgx.template;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/4/1 21:08
 */
public class MybatisXmlHelper {

    private static final String IS_NOT_EMPTY_1 = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%1$s)";
    private static final String IS_NOT_EMPTY_2 = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%1$s.%2$s)";
    private static final String IS_NOT_EMPTY_3 = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%1$s.%2$s.%3$s)";

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

    public static String getTestExpression(List<String> pathItemList) {
        String[] paths = pathItemList.toArray(new String[pathItemList.size()]);
        if (paths.length == 1) {
            List<String> isNotEmptyList = Arrays.asList(IS_NOT_EMPTY_1);
            return String.format(StringUtils.join(isNotEmptyList, " and "), paths);
        }
        if (paths.length == 2) {
            List<String> isNotEmptyList = Arrays.asList(IS_NOT_EMPTY_1, IS_NOT_EMPTY_2);
            return String.format(StringUtils.join(isNotEmptyList, " and "), paths);
        }
        if (paths.length == 3) {
            List<String> isNotEmptyList = Arrays.asList(IS_NOT_EMPTY_1, IS_NOT_EMPTY_2, IS_NOT_EMPTY_3);
            return String.format(StringUtils.join(isNotEmptyList, " and "), paths);
        }
        return "";
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
