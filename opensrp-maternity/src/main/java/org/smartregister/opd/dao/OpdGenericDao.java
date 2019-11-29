package org.smartregister.opd.dao;

import java.util.List;

public interface OpdGenericDao<T> {
    boolean saveOrUpdate(T t);

    T findOne(T t);

    boolean delete(T t);

    List<T> findAll();
}
