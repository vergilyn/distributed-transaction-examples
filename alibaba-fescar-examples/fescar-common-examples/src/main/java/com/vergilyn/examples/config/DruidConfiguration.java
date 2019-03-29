package com.vergilyn.examples.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;

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
public class DruidConfiguration {

    @Bean
    @Primary
    public DataSource dataSource(){
        return DruidDataSourceBuilder.create().build();
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
}
