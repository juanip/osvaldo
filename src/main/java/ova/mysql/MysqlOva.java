package ova.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import ova.Ova;
import ova.Query;
import ova.annotations.Column;
import ova.annotations.PK;
import ova.annotations.Table;
import ova.exceptions.OvaException;
import ova.reflection.FieldColumn;

public class MysqlOva implements Ova {

    Connection connection;

    public MysqlOva(Connection connection) {
        this.connection = connection;
    }

    public <T> T insert(T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> int delete(T entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    public <T> T update(T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> List<T> find(Query<T> query) {
        Table table = getTable(query.getType());

        String sql = "select * from " + table.name() + " " + getWhere(query);

        List<T> results;

        List<FieldColumn> colums = getColumns(query.getType());

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet r = st.executeQuery();

            results = new ArrayList<T>(r.getFetchSize());

            while (r.next()) {
                T entity = newEntity(query);

                colums.stream().forEach(c -> {
                    Object value = getValue(r, c);

                    if (!Objects.isNull(value)) {
                        setValue(entity, c, c.getType().cast(value));
                    }
                });

                results.add(entity);
            }

        } catch (SQLException e) {
            throw new OvaException(e);
        }

        return results;
    }

    private <T> void setValue(T entity, FieldColumn c, Object value) {
        try {
            c.getSetter().invoke(entity, value);
        } catch (Exception e) {
            throw new OvaException(e);
        }
    }

    private Object getValue(ResultSet r, FieldColumn c) {
        try {
            return r.getObject(c.getName());
        } catch (Exception e) {
            throw new OvaException(e);
        }
    }

    private <T> T newEntity(Query<T> query) {
        try {
            return query.getType().newInstance();
        } catch (Exception e) {
            throw new OvaException(e);
        }
    }

    private List<FieldColumn> getColumns(Class<?> type) {
        Field[] fields = type.getDeclaredFields();

        List<FieldColumn> columns = Arrays.stream(fields).filter(f -> !Objects.isNull(getColumn(f))).map(f -> {
            Column column = getColumn(f);
            String fieldName = f.getName();
            String setterName = getSetterName(fieldName);
            Class<?> valueType = f.getType();
            Method setter = getSetter(type, setterName, valueType);

            return new FieldColumn(column.name(), setter, valueType);
        }).collect(Collectors.toList());

        return columns;
    }

    private Method getSetter(Class<?> type, String setterName, Class<?> valueType) {
        try {
            return type.getDeclaredMethod(setterName, valueType);
        } catch (Exception e) {
            throw new OvaException("there is no setter method for " + setterName);
        }
    }

    private String getSetterName(String name) {
        return "set" + String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
    }

    private <T> Table getTable(Class<T> type) {
        Table table = type.getAnnotation(Table.class);

        if (Objects.isNull(table)) {
            throw new OvaException("The type is not annotated with @Table");
        }

        return table;
    }

    private String getWhere(Query<?> query) {

        String oql = query.getOql();

        if (Objects.isNull(oql) || oql.isEmpty()) {
            return "";
        }

        if (oql.contains("@PK")) {
            String pk = getPKName(query.getType());
            oql = oql.replace("@PK", pk);
        }

        return "where " + oql;
    }

    private String getPKName(Class<?> type) {

        Optional<Field> field = Arrays.stream(type.getDeclaredFields()).filter(this::isPK).findFirst();

        Field f = field.orElseThrow(() -> new OvaException("There is no field annotated with @PK"));

        return getColumnName(f);
    }

    private boolean isPK(Field field) {
        return !Objects.isNull(field.getAnnotation(PK.class));
    }

    private String getColumnName(Field field) {
        Column column = getColumn(field);

        if (Objects.isNull(column)) {
            throw new OvaException("The field is not annotated with @Column");
        }

        return column.name();
    }

    private Column getColumn(Field field) {
        return field.getAnnotation(Column.class);
    }
}
