package com.lc.mybatisx.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class ActionNode {

    private List<Annotation> annotationList;

    private Method method;

    private List<ParamNode> paramNodeList;

    private ResultNode resultNode;

}
