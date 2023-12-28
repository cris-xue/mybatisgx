package com.lc.mybatisx.template.model;

import org.dom4j.Element;

import java.util.function.Consumer;

public class TrimNode {

    private Element trimElement;

    public TrimNode(Element parentElement) {
        trimElement = parentElement.addElement("trim");
    }

    public TrimNode prefix(String prefix) {
        trimElement.addAttribute("prefix", prefix);
        return this;
    }

    public TrimNode suffix(String suffix) {
        trimElement.addAttribute("suffix", suffix);
        return this;
    }

    public TrimNode suffixOverrides(String suffixOverrides) {
        trimElement.addAttribute("suffixOverrides", suffixOverrides);
        return this;
    }

    public TrimNode text(Consumer<Element> consumer) {
        consumer.accept(trimElement);
        return this;
    }

}
