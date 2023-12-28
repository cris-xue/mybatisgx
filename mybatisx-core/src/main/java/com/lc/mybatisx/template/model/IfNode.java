package com.lc.mybatisx.template.model;

import org.dom4j.Element;

import java.util.function.Consumer;

public class IfNode {

    private Element element;

    public IfNode(Element parentElement) {
        element = parentElement.addElement("if");
    }

    public IfNode test(String javaColumnName) {
        element.addAttribute("test", String.format("%s != null", javaColumnName));
        return this;
    }

    public IfNode text(Consumer<Element> consumer) {
        consumer.accept(element);
        return this;
    }

}
