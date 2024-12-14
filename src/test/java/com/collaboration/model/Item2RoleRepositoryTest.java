/**
 *  Item2RoleRepositoryTest
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
public class Item2RoleRepositoryTest {

    @Autowired
    private Item2RoleRepository item2RoleRepository;

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
    public void testFindAllByItemId() {
        // Vorbereitung
        Item2Role item2Role1 = new Item2Role();
        item2Role1.setItemId(1L);
        item2Role1.setRoleId(100L);
        item2RoleRepository.save(item2Role1);

        Item2Role item2Role2 = new Item2Role();
        item2Role2.setItemId(1L);
        item2Role2.setRoleId(101L);
        item2RoleRepository.save(item2Role2);

        // Ausführen
        List<Item2Role> result = item2RoleRepository.findAllByItemId(1L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testDeleteByItemIdAndRoleId() {
        // Vorbereitung
        Item2Role item2Role = new Item2Role();
        item2Role.setItemId(1L);
        item2Role.setRoleId(100L);
        item2RoleRepository.save(item2Role);

        // Ausführen
        int deletedCount = item2RoleRepository.deleteByItemIdAndRoleId(1L, 100L);

        // Überprüfen
        assertEquals(1, deletedCount);
    }

    @Test
    public void testDeleteAllByItemId() {
        // Vorbereitung
        Item2Role item2Role1 = new Item2Role();
        item2Role1.setItemId(1L);
        item2Role1.setRoleId(100L);
        item2RoleRepository.save(item2Role1);

        Item2Role item2Role2 = new Item2Role();
        item2Role2.setItemId(1L);
        item2Role2.setRoleId(101L);
        item2RoleRepository.save(item2Role2);

        // Ausführen
        int deletedCount = item2RoleRepository.deleteAllByItemId(1L);

        // Überprüfen
        assertEquals(2, deletedCount);
    }
}
