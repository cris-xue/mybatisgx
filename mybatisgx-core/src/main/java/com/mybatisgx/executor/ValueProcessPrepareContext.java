package com.mybatisgx.executor;

import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.model.MethodInfo;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/4 16:05
 */
public class ValueProcessPrepareContext {

    private MethodInfo methodInfo;

    private boolean isProcess = false;

    private MethodCommandType commandType;

    private Object parameterObject;

    private MetaObject metaObject;

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public boolean getProcess() {
        return isProcess;
    }

    public void setProcess(boolean process) {
        isProcess = process;
    }

    public MethodCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(MethodCommandType commandType) {
        this.commandType = commandType;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Object parameterObject) {
        this.parameterObject = parameterObject;
        this.metaObject = SystemMetaObject.forObject(parameterObject);
    }

    public MetaObject getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }
}
