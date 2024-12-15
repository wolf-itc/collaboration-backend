/**
 *  OrganizationRepositoryTest
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

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

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
    public void testSaveOrganization() {
        // Vorbereitung
        Organization organization = new Organization();
        organization.setName("Test Organization");
        organization.setDescription("A description of the test organization.");
        organization.setLogo(new byte[] { 1, 2, 3, 4 });

        // Speichern
        organizationRepository.save(organization);

        // Abrufen
        Organization result = organizationRepository.findById(organization.getId()).orElse(null);

        // Überprüfen
        assertNotNull(result);
        assertEquals("Test Organization", result.getName());
        assertEquals("A description of the test organization.", result.getDescription());
    }

    @Test
    public void testFindAll() {
        // Vorbereitung
        Organization organization1 = new Organization();
        organization1.setName("Organization 1");
        organization1.setDescription("Description 1");
        organizationRepository.save(organization1);

        Organization organization2 = new Organization();
        organization2.setName("Organization 2");
        organization2.setDescription("Description 2");
        organizationRepository.save(organization2);

        // Abrufen
        long count = organizationRepository.count();

        // Überprüfen
        assertEquals(2, count);
    }
}
