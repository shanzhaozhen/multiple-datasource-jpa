package org.shanzhaozhen.multipledatasourcejpa.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "sqlServerEntityManagerFactory",
        transactionManagerRef = "sqlServerTransactionManager",
        basePackages = {"org.shanzhaozhen.multipledatasourcejpa.repository.sqlserver"})//设置dao（repo）所在位置
public class SQLServerDataSourceConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Autowired
    @Qualifier("sqlServerDataSource")
    private DataSource sqlServerDataSource;

    @Bean(name = "sqlServerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sqlserver")
    public DataSource sqlServerDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    public Map<String, Object> getOracleProperties() {
        Map<String, Object> map = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        map.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        map.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        map.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        return map;
    }

    @Bean(name = "sqlServerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sqlServerEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(sqlServerDataSource)
                .properties(getOracleProperties())
                .packages("com.fnl56.recruitment.bean.salary2") //设置实体类所在位置
                .persistenceUnit("sqlServerPersistenceUnit")
                .build();
    }

    @Bean(name = "sqlServerEntityManager")
    public EntityManager sqlServerEntityManager(EntityManagerFactoryBuilder builder) {
        return sqlServerEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "sqlServerTransactionManager")
    PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(sqlServerEntityManagerFactory(builder).getObject());
    }

}
