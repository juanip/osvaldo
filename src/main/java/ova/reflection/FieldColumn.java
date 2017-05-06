package ova.reflection;

import java.lang.reflect.Method;

public class FieldColumn {

    private String name;
    private Method setter;
    private Class<?> type;

    public FieldColumn(String name, Method setter, Class<?> type) {
        this.name = name;
        this.setter = setter;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Method getSetter() {
        return setter;
    }

    public Class<?> getType() {
        return type;
    }
}
