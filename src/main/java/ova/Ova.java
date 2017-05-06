package ova;

import java.util.List;

public interface Ova {

    <T> T insert(T entity);

    <T> int delete(T entity);

    <T> T update(T entity);

    <T> List<T> find(Query<T> query);
}
