/**
 *  AuthorityRepositoryTest
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
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

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
    public void testFindAll() throws SQLException {
        // Vorbereitung
        Authority authority = new Authority();
        authority.setUserId(1);
        authority.setUserName("Karl");
        authority.setAuthority("ROLE_USER");
        authorityRepository.save(authority);

        // Ausführen
        List<Authority> result = authorityRepository.findAll();

        // Überprüfen
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByUserId() {
        // Vorbereitung
        Authority authority = new Authority();
        authority.setUserId(1L);
        authority.setUserName("Karl");
        authority.setAuthority("ROLE_USER");
        authorityRepository.save(authority);

        // Ausführen
        List<Authority> result = authorityRepository.findByUserId(1L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testDeleteAllByUserName() {
        // Vorbereitung
        Authority authority = new Authority();
        authority.setUserName("testUser");
        authorityRepository.save(authority);

        // Ausführen
        authorityRepository.deleteAllByUserName("testUser");

        // Überprüfen
        List<Authority> result = authorityRepository.findAll();
        assertEquals(0, result.size());
    }
}
