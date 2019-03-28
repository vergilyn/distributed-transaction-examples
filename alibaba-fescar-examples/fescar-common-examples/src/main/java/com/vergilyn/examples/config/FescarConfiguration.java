package com.vergilyn.examples.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;
import com.alibaba.fescar.spring.annotation.GlobalTransactionScanner;
import com.vergilyn.examples.constants.FescarConstant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory",
//        transactionManagerRef = "transactionManager",
//        basePackages = "com.vergilyn.examples.repository")
public class FescarConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource dataSource(){
        return new DruidDataSource();
    }

    @Bean
    public DataSourceProxy dataSourceProxy(DataSource dataSource){
        return new DataSourceProxy((DruidDataSource) dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSourceProxy dataSourceProxy) {
        return new JdbcTemplate(dataSourceProxy);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSourceProxy dataSourceProxy) {
        return new NamedParameterJdbcTemplate(dataSourceProxy);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSourceProxy dataSourceProxy) {
        return builder
                .dataSource(dataSourceProxy)
                .packages("com.vergilyn.examples.entity")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


    /**
     * init global transaction scanner
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner(@Value("${spring.application.name}") String applicationId){
        return new GlobalTransactionScanner(applicationId, FescarConstant.TX_SERVICE_GROUP);
    }
}
