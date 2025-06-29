package com.lc.mybatisx.model.handler;

import java.util.HashSet;
import java.util.Set;

/**
 * 结果映射依赖树
 */
public class ResultMapDependencyTree {

    /**
     * aaaa:
     *   aaaaa:
     *     aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   aaaaa:
     *     aaaaa: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   bbbbb:
     *     aaaaa: 消除循环依赖
     *     bbbbb: 不需要消除，允许自引用，但是需要设置最大层级，最多允许10层
     *   ccccc:
     *     aaaaa: 消除循环依赖
     *     bbbbb:
     */

    /**
     * 父类
     */
    private Class<?> parent;
    /**
     * 当前类
     */
    private Class<?> clazz;
    /**
     * 当前深度【只有自引用才计算深度】
     */
    private int depth = 1;
    /**
     * 树路径
     */
    private Set<Class<?>> path = new HashSet<>(10);

    public ResultMapDependencyTree(ResultMapDependencyTree resultMapDependencyTree, Class<?> clazz) {
        this.clazz = clazz;
        if (resultMapDependencyTree != null) {
            this.parent = resultMapDependencyTree.getClazz();
            this.path.addAll(resultMapDependencyTree.getPath());
            if (this.parent == clazz) {
                this.depth = resultMapDependencyTree.depth + 1;
            }
        }
        this.path.add(clazz);
    }

    public Class<?> getParent() {
        return parent;
    }

    public void setParent(Class<?> parent) {
        this.parent = parent;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Set<Class<?>> getPath() {
        return path;
    }

    public void setPath(Set<Class<?>> path) {
        this.path = path;
    }

    public Boolean cycleRefCheck(Class<?> subClazz) {
        // 自循环引用是可以允许的
        if (this.clazz == subClazz) {
            return this.depth > 5;
        }
        return this.path.contains(subClazz);
    }
}
