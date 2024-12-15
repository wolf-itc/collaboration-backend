/**
 *  NonuserRepositoryTest
 *  
 *  DataJpaTest will not run as TestNG because repositories get not injected
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class NonuserRepositoryTest {

    @Autowired
    private NonuserRepository nonuserRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setUp() throws SQLException {
        // Ensure H2
        Connection connection = dataSource.getConnection();
        String driverName = connection.getMetaData().getDriverName();
        assertTrue(driverName.toUpperCase().contains("H2"));
    }

    @Test
    public void testFindByOrgaId() {
        // Vorbereitung
        Nonuser nonuser1 = new Nonuser();
        nonuser1.setName("Nonuser 1");
        nonuser1.setUserId(100L);
        nonuser1.setOrgaId(200L);
        nonuser1.setContent("content");
        nonuserRepository.save(nonuser1);

        Nonuser nonuser2 = new Nonuser();
        nonuser2.setName("Nonuser 2");
        nonuser2.setUserId(101L);
        nonuser2.setOrgaId(200L);
        nonuser2.setContent("content");
        nonuserRepository.save(nonuser2);

        // Ausführen
        List<Nonuser> result = nonuserRepository.findByOrgaId(200L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByName() {
        // Vorbereitung
        Nonuser nonuser = new Nonuser();
        nonuser.setName("John Doe");
        nonuser.setUserId(100L);
        nonuser.setOrgaId(200L);
        nonuser.setContent("content");
        nonuserRepository.save(nonuser);

        // Ausführen
        List<Nonuser> result = nonuserRepository.findByName("John Doe");

        // Überprüfen
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByUserId() {
        // Vorbereitung
        Nonuser nonuser1 = new Nonuser();
        nonuser1.setName("John Doe");
        nonuser1.setUserId(100L);
        nonuser1.setOrgaId(200L);
        nonuser1.setContent("content");
        nonuserRepository.save(nonuser1);

        Nonuser nonuser2 = new Nonuser();
        nonuser2.setName("Jane Smith");
        nonuser2.setUserId(100L);
        nonuser2.setOrgaId(200L);
        nonuser2.setContent("content");
        nonuserRepository.save(nonuser2);

        // Ausführen
        List<Nonuser> result = nonuserRepository.findByUserId(100L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
