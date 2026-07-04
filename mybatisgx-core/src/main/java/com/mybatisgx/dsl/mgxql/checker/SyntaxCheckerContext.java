package com.mybatisgx.dsl.mgxql.checker;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * MGXQL语法校验上下文，仅持有从MgxqlStatement提取的语法信息
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SyntaxCheckerContext {

    private Set<String> definedAliases = new LinkedHashSet<>();

    private boolean hasMultipleEntities;

    private List<String> errors = new ArrayList<>();

    public Set<String> getDefinedAliases() {
        return definedAliases;
    }

    public void registerAlias(String alias) {
        this.definedAliases.add(alias);
    }

    public boolean isAliasDefined(String alias) {
        return this.definedAliases.contains(alias);
    }

    public boolean isHasMultipleEntities() {
        return hasMultipleEntities;
    }

    public void setHasMultipleEntities(boolean hasMultipleEntities) {
        this.hasMultipleEntities = hasMultipleEntities;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
}
