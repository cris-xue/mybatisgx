package com.mybatisgx.executor;

import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.model.MethodInfo;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/4 16:05
 */
public class ValueProcessPrepareContext {

    private MethodInfo methodInfo;

    private boolean isProcess;

    private MethodCommandType commandType;

    private Object parameterObject;

    public ValueProcessPrepareContext(boolean isProcess, MethodInfo methodInfo) {
        this.isProcess = isProcess;
        this.setMethodInfo(methodInfo);
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        if (methodInfo != null) {
            this.methodInfo = methodInfo;
            this.commandType = methodInfo.getMethodCommandType();
        }
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
    }
}
