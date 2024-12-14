/**
 *  PermissionRepositoryTest
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
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

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
    public void testSavePermission() {
        // Vorbereitung
        Permission permission = new Permission();
        permission.setItemtypeId(1L);
        permission.setItemId(100L);
        permission.setRoleId(200L);
        permission.setPermission("READ");

        // Speichern
        permissionRepository.save(permission);

        // Abrufen
        Permission result = permissionRepository.findById(permission.getId()).orElse(null);

        // Überprüfen
        assertNotNull(result);
        assertEquals("READ", result.getPermission());
        assertEquals(100L, result.getItemId());
    }

    @Test
    public void testFindByItemtypeIdAndItemIdAndRoleIdIn() {
        // Vorbereitung
        Permission permission = new Permission();
        permission.setItemtypeId(1L);
        permission.setItemId(100L);
        permission.setRoleId(200L);
        permission.setPermission("READ");
        permissionRepository.save(permission);

        // Abrufen
        List<Permission> result = permissionRepository.findByItemtypeIdAndItemIdAndRoleIdIn(1L, 100L, List.of(200L));

        // Überprüfen
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("READ", result.get(0).getPermission());
    }

    @Test
    public void testFindByItemtypeIdAndRoleIdIn() {
        // Vorbereitung
        Permission permission = new Permission();
        permission.setItemtypeId(1L);
        permission.setItemId(null); // ItemId is null for create operations
        permission.setRoleId(200L);
        permission.setPermission("WRITE");
        permissionRepository.save(permission);

        // Abrufen
        List<Permission> result = permissionRepository.findByItemtypeIdAndRoleIdIn(1L, List.of(200L));

        // Überprüfen
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WRITE", result.get(0).getPermission());
    }

    @Test
    public void testFindByOrgaId() {
        // Vorbereitung
        // Hier nehmen wir an, dass eine Role mit einer orgaId in der Datenbank existiert
        // Sie müssen einen Wert für orgaId anpassen und sicherstellen, dass eine entsprechende Role vorhanden ist
        long orgaId = 123L;

        List<Permission> result = permissionRepository.findByOrgaId(orgaId);

        // Überprüfen
        assertNotNull(result);
    }
}
