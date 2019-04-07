package com.demo.netty.rpc.msg;

/**
 * ·InvokerMsg
 * ·李文彬
 * 2019/4/7 ·10:36
 */
public class InvokerMsg {
    private String className;  //服务名称
    private String methodName;  //方法名称
    private Class<?>[] parames;  //参数列表
    private Object[] values;  //参数值

    public InvokerMsg() {
    }

    public InvokerMsg(String className, String methodName, Class<?>[] parames, Object[] values) {
        this.className = className;
        this.methodName = methodName;
        this.parames = parames;
        this.values = values;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParames() {
        return parames;
    }

    public void setParames(Class<?>[] parames) {
        this.parames = parames;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
