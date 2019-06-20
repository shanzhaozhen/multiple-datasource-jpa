package org.shanzhaozhen.multipledatasourcejpa.dao.impl;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class MySQLBaseDaoImpl<T> extends BaseDaoImpl<T> {

    @PersistenceContext(unitName = "mysqlPersistenceUnit")
    private EntityManager mysqlEntityManager;

    @Override
    public EntityManager chooseEntityManager() {
        return mysqlEntityManager;
    }
}
