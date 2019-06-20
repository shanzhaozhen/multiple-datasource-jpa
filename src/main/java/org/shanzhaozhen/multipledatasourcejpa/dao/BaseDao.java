package org.shanzhaozhen.multipledatasourcejpa.dao;

import org.shanzhaozhen.multipledatasourcejpa.common.QueryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

    EntityManager chooseEntityManager();

    Query setParams(Query query, Object... params);

    Query createQuery(QueryType queryType, String sql);

    Query createQuery(QueryType queryType, Class clazz, String sql);

    Query createQueryAndSetParams(QueryType queryType, String sql, Object... params);

    Query createQueryAndSetParams(QueryType queryType, Class clazz, String sql, Object... params);

    Long countBySQL(String sql, Object... params);

    /**
     * 原生sql使用（非entity）
     */

    T getSingleBySQL(Class clazz, String sql, Object... params);

    List<T> getListBySQL(Class clazz, String sql, Object... params);

    List<T> getListBySQL(Class clazz, String sql, Pageable pageable, Object... params);

    Page<T> getPageBySQL(Class clazz, String sql, Pageable pageable, Object... params);

    Page<Map<String, Object>> getPageBySQL(String sql, Pageable pageable, Object... params);

    List<Map<String, Object>> getForMapListBySQL(String sql, Pageable pageable, Object... params);

    /**
     * 原生sql使用
     */
    T getSingleBySQLAndEntity(Class clazz, String sql, Object... params);

    List<T> getListBySQLAndEntity(Class clazz, String sql, Object... params);

    List<T> getListBySQLAndEntity(Class clazz, String sql, Pageable pageable, Object... params);

    Page<T> getPageBySQLAndEntity(Class clazz, String sql, Pageable pageable, Object... params);


    /**
     * hql使用
     */

    Long count(String hql, Object... params);

    T getSingle(Class clazz, String hql, Object... params);

    List<T> getList(Class clazz, String hql, Object... params);

    List<T> getList(Class clazz, String hql, Pageable pageable, Object... params);

    Page<T> getPage(Class clazz, String hql, Pageable pageable, Object... params);

}
