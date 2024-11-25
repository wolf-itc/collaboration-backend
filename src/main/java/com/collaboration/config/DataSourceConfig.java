/**
 *  DataSourceConfig
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    /* This would need: spring.datasource.h2.jdbc-url=jdbc:h2:mem:xxx in application.properties
    @Bean(name = "h2DB")
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        return dataSourceBuilder.build();
    }
    */

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postgresDB")
    @ConfigurationProperties(prefix = "spring.datasource.postgres.config")
    public HikariDataSource getDataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

/*
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.h2")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "h2DB")
    @ConfigurationProperties(prefix = "spring.datasource.h2.config")
    public HikariDataSource getDataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
*/
}