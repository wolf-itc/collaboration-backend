package com.collaboration.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class DataSourceConfigTest {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    @Qualifier("postgresDB")
    private DataSource dataSource;

    @Test
    public void testDataSourceProperties() {
        // Überprüfen, ob die DataSourceProperties korrekt erstellt wurden
        assertNotNull(dataSourceProperties);
        assertNotNull(dataSourceProperties.getUrl());
        assertNotNull(dataSourceProperties.getUsername());
        assertNotNull(dataSourceProperties.getPassword());
    }

    @Test
    public void testHikariDataSource() throws SQLException {
        // Überprüfen, ob der HikariDataSource korrekt erstellt wurde
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof HikariDataSource);

        // Überprüfen, ob eine Verbindung hergestellt werden kann
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
    }

    @Test
    public void testDataSourceType() {
        // Überprüfen, ob der DataSource-Typ korrekt ist
        assertTrue(dataSource instanceof HikariDataSource);
    }
}