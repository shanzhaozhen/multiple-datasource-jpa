package org.shanzhaozhen.multipledatasourcejpa.dao.impl;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.shanzhaozhen.multipledatasourcejpa.common.QueryType;
import org.shanzhaozhen.multipledatasourcejpa.dao.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Component
public class BaseDaoImpl<T> implements BaseDao<T> {

    @PersistenceContext(unitName = "mysqlPersistenceUnit")
    private EntityManager mysqlEntityManager;

    @Override
    public EntityManager chooseEntityManager() {
        return mysqlEntityManager;
    }

    @Override
    public Query setParams(Query query, Object... params) {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

    @Override
    public Query createQuery(QueryType queryType, String sql) {
        EntityManager entityManager = chooseEntityManager();
        Query query;
        if (queryType.equals(QueryType.Query)) {
            query =  entityManager.createQuery(sql);
        } else {
            query =  entityManager.createNativeQuery(sql);
        }
        return query;
    }

    @Override
    public Query createQuery(QueryType queryType, Class clazz, String sql) {
        EntityManager entityManager = chooseEntityManager();
        if (queryType.equals(QueryType.Query)) {
            return entityManager.createQuery(sql, clazz);
        } else {
            return entityManager.createNativeQuery(sql, clazz);
        }
    }

    @Override
    public Query createQueryAndSetParams(QueryType queryType, String sql, Object... params) {
        Query query = createQuery(queryType, sql);
        setParams(query, params);
        return query;
    }

    @Override
    public Query createQueryAndSetParams(QueryType queryType, Class clazz, String sql, Object... params) {
        Query query = createQuery(queryType, clazz, sql);
        setParams(query, params);
        return query;
    }

    @Override
    public Long countBySQL(String sql, Object... params) {
        if (sql.lastIndexOf("order by") != -1) {
            sql = sql.substring(0, sql.lastIndexOf("order by"));
        }
        Query query = createQueryAndSetParams(QueryType.NativeQuery, "select count(*) from (" + sql +") t", params);
        return Long.parseLong(query.getResultList().get(0).toString());
    }

    /**
     * 原生sql使用（非entity）
     */

    @Override
    public T getSingleBySQL(Class clazz, String sql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.NativeQuery, sql, params);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.aliasToBean(clazz));
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<T> getListBySQL(Class clazz, String sql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.NativeQuery, sql, params);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.aliasToBean(clazz));
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public List<T> getListBySQL(Class clazz, String sql, Pageable pageable, Object... params) {
        if (pageable.getSort().isSorted()) {
            String[] sort = pageable.getSort().toString().split(": ");
            if (sql.lastIndexOf("order by") != -1) {
//                String tempSort = sql.substring(hql.lastIndexOf("order by"));
//                sql = sql.substring(0, hql.lastIndexOf("order by"));
                sql = sql + " " + sort[0] + " " + sort[1];
            } else {
                sql = sql + " order by " + sort[0] + " " + sort[1];
            }
        }
        Query query = createQueryAndSetParams(QueryType.NativeQuery, sql, params);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .unwrap(NativeQuery.class)
            .setResultTransformer(Transformers.aliasToBean(clazz));
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public Page<T> getPageBySQL(Class clazz, String sql, Pageable pageable, Object... params) {
        List<T> list = getListBySQL(clazz, sql, pageable, params);
        Long total = countBySQL(sql, params);
        Page<T> page = new PageImpl<>(list, pageable, total);
        return page;
    }

    @Override
    public List<Map<String, Object>> getForMapListBySQL(String sql, Pageable pageable, Object... params) {
        if (pageable != null) {
            if (pageable.getSort().isSorted()) {
                String[] sort = pageable.getSort().toString().split(": ");
                if (sql.lastIndexOf("order by") != -1) {
//                String tempSort = sql.substring(hql.lastIndexOf("order by"));
//                sql = sql.substring(0, hql.lastIndexOf("order by"));
                    sql = sql + " " + sort[0] + " " + sort[1];
                } else {
                    sql = sql + " order by " + sort[0] + " " + sort[1];
                }
            }
        }

        Query query = createQueryAndSetParams(QueryType.NativeQuery, sql, params);
        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                    .setMaxResults(pageable.getPageSize());
        }
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> list = query.getResultList();
        return list;
    }

    @Override
    public Page<Map<String, Object>> getPageBySQL(String sql, Pageable pageable, Object... params) {
        List<Map<String, Object>> list = getForMapListBySQL(sql, pageable, params);
        Long total = countBySQL(sql, params);
        Page<Map<String, Object>> page = new PageImpl<>(list, pageable, total);
        return page;
    }


    /**
     * 原生sql使用
     */

    @Override
    public T getSingleBySQLAndEntity(Class clazz, String sql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.NativeQuery, clazz, sql, params);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<T> getListBySQLAndEntity(Class clazz, String sql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.NativeQuery, clazz, sql, params);
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public List<T> getListBySQLAndEntity(Class clazz, String sql, Pageable pageable, Object... params) {
        if (pageable.getSort().isSorted()) {
            String[] sort = pageable.getSort().toString().split(": ");
            if (sql.lastIndexOf("order by") != -1) {
//                String tempSort = sql.substring(hql.lastIndexOf("order by"));
//                sql = sql.substring(0, hql.lastIndexOf("order by"));
                sql = sql + " " + sort[0] + " " + sort[1];
            } else {
                sql = sql + " order by " + sort[0] + " " + sort[1];
            }
        }
        Query query = createQueryAndSetParams(QueryType.NativeQuery, clazz, sql, params);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public Page<T> getPageBySQLAndEntity(Class clazz, String sql, Pageable pageable, Object... params) {
        List<T> list = getListBySQLAndEntity(clazz, sql, pageable, params);
        Page<T> page = new PageImpl<>(list, pageable, list.size());
        return page;
    }

    @Override
    public Long count(String hql, Object... params) {
        hql = hql.substring(hql.indexOf("from"));
        if (hql.lastIndexOf("order by") != -1) {
            hql = hql.substring(0, hql.lastIndexOf("order by"));
        }
        Query query = createQueryAndSetParams(QueryType.Query, "select count(*) " + hql, params);
        return (Long) query.getResultList().get(0);
    }


    /**
     * hql使用
     */
    @Override
    public T getSingle(Class clazz, String hql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.Query, clazz, hql, params);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<T> getList(Class clazz, String hql, Object... params) {
        Query query = createQueryAndSetParams(QueryType.Query, clazz, hql, params);
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public List<T> getList(Class clazz, String hql, Pageable pageable, Object... params) {
        if (pageable.getSort().isSorted()) {
            String[] sort = pageable.getSort().toString().split(": ");
            if (hql.lastIndexOf("order by") != -1) {
//                String tempSort = hql.substring(hql.lastIndexOf("order by"));
//                hql = hql.substring(0, hql.lastIndexOf("order by"));
                hql = hql + " " + sort[0] + " " + sort[1];
            } else {
                hql = hql + " order by " + sort[0] + " " + sort[1];
            }
        }
        Query query = createQueryAndSetParams(QueryType.Query, clazz, hql, params);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
        List<T> list = query.getResultList();
        return list;
    }

    @Override
    public Page<T> getPage(Class clazz, String hql, Pageable pageable, Object... params) {
        List<T> list = this.getList(clazz, hql, pageable, params);
        Long total = this.count(hql, params);
        Page<T> page = new PageImpl<>(list, pageable, total);
        return page;
    }

}
