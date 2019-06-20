package org.shanzhaozhen.multipledatasourcejpa.dao.impl;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class SQLServerBaseDaoImpl<T> extends BaseDaoImpl<T> {

    @PersistenceContext(unitName = "sqlServerPersistenceUnit")
    private EntityManager sqlServerEntityManager;

    @Override
    public EntityManager chooseEntityManager() {
        return sqlServerEntityManager;
    }

}
