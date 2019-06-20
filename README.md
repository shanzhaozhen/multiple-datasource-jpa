# multiple-datasource-jpa

### 介绍
该项目主要是一个使用Spring Data JPA 实现多数据源的demo，可以更好的对应开发过程中使用多数据库和进行多表复杂查询的情况。

### 相关技术
该项目使用的相关技术（工具）主要有以下:

* Spring Boot
* Spring Data JPA
* Druid

### 当前实现

|序号|实现|
|:---:|:---|
|1|实现多数据源的连接|
|2|实现 Spring Data JPA 提供的repository接口|
|3|实现自由度较高的sql执行方式（通过EntityManager实现）|

### 配置提要

```java
/**
 * 配置自动生成创建人或修改人id
 */
@Configuration
public class MyAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Integer id = UserDetailsUtils.getSysUserId();
        return Optional.ofNullable(id);
    }
    
}
```
```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager",
        basePackages = {"org.shanzhaozhen.multipledatasourcejpa.repository.mysql"})//设置dao（repo）所在位置
public class MySQLDataSourceConfig {

    //...

    /**
    * 该处配置主要是用来配置对应连接的数据源使用的方言、jpa的命名规则还有数据库的映射规则
    */
    public Map<String, Object> getMysqlProperties() {
        Map<String, Object> map = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        map.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
        map.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        map.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        return map;
    }
    
    @Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mysqlDataSource)
                .properties(getMysqlProperties())
                .packages("com.fnl56.recruitment.bean.primary") //设置实体类所在位置
                .persistenceUnit("mysqlPersistenceUnit")
                .build();
    }
    
    //...
    
}
```

```java
/**
* 该处实现了BaseDao的数据库查询方法
* 
* 其中需要注意的地方有两点
* EntityManager： EntityManager的注入方式建议使用@PersistenceContext
*   注入，假如使用@Autowire方式注入的话，查询或者更新操作不会清除上下文记录，
*   会保持上一次的查询结果
* 
* chooseEntityManager：多数据源的实现方式主要是使用继承BaseDaoImpl对chooseEntityManager
*   方法进行重写，切换到目标数据源的EntityManager以实现查询
* 
*/
@Component
public class BaseDaoImpl<T> implements BaseDao<T> {

    @PersistenceContext(unitName = "mysqlPersistenceUnit")
    private EntityManager mysqlEntityManager;

    @Override
    public EntityManager chooseEntityManager() {
        return mysqlEntityManager;
    }
    
    //...

    /**
    * 原生分页实现
    */
    @Override
    public List<T> getListBySQL(Class clazz, String sql, Pageable pageable, Object... params) {
        if (pageable.getSort().isSorted()) {
            String[] sort = pageable.getSort().toString().split(": ");
        if (sql.lastIndexOf("order by") != -1) {
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

    //...

    /**
    * hql实现分页
    */
    @Override
    public List<T> getList(Class clazz, String hql, Pageable pageable, Object... params) {
        if (pageable.getSort().isSorted()) {
            String[] sort = pageable.getSort().toString().split(": ");
            if (hql.lastIndexOf("order by") != -1) {
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
```

```java
/**
* 通过枚举给dao方法判断是原生sql查询还是使用jpa提供的hql方法查询
*/
public enum QueryType {

    Query,
    NativeQuery;

    QueryType() {
    }
    
}
```
