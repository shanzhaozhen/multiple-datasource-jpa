package org.shanzhaozhen.multipledatasourcejpa.dao.impl;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class OracleBaseDaoImpl<T> extends BaseDaoImpl<T> {

    @PersistenceContext(unitName = "oraclePersistenceUnit")
    private EntityManager oracleEntityManager;

    @Override
    public EntityManager chooseEntityManager() {
        return oracleEntityManager;
    }

}
