package lava.core;

import lava.constant.Constants;
import lava.util.StringUtil;

/**
 * Created by xie,pengqi on 2018/5/7.
 */
public class Data {
    public Data() {
    }

    @Override
    public String toString() {
        return StringUtil.join(Constants.empty,"Data [type=", this.type, "]");
    }

    public Data(Data dataInfo) {
        this.type = dataInfo.getType();
        this.value = dataInfo.getValue();
        this.source = dataInfo.getSource();
    }
    @SuppressWarnings("rawtypes")
    public Data(Object value) {
        this.type = getClass(value);
        this.value = value;
    }
    @SuppressWarnings("rawtypes")
    public Data(Class type, Object value) {
        this.type = type;
        this.value = value;
    }

    @SuppressWarnings("rawtypes")
    public Data(Class type, Object value, String source) {
        this.type = type;
        this.value = value;
        this.source = source;
    }

    private String source;
    private Object value;
    @SuppressWarnings("rawtypes")
    private Class type = Object.class;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @SuppressWarnings("rawtypes")
    public Class getType() {
        return type;
    }

    @SuppressWarnings("rawtypes")
    public void setType(Class type) {
        this.type = type;
    }

    public static Class getClass(Object obj) {
        return obj == null ? Object.class : obj.getClass();
    }

    public static Class getType(Data data) {
        return data == null ? Object.class : data.getType();
    }
}
