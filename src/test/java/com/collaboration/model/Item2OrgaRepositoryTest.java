/**
 *  Item2OrgaRepositoryTest
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
public class Item2OrgaRepositoryTest {

    @Autowired
    private Item2OrgaRepository item2OrgaRepository;

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
        Item2Orga item2Orga1 = new Item2Orga();
        item2Orga1.setItemId(1L);
        item2Orga1.setOrgaId(100L);
        item2OrgaRepository.save(item2Orga1);

        Item2Orga item2Orga2 = new Item2Orga();
        item2Orga2.setItemId(1L);
        item2Orga2.setOrgaId(101L);
        item2OrgaRepository.save(item2Orga2);

        // Ausführen
        List<Item2Orga> result = item2OrgaRepository.findAllByItemId(1L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testDeleteByItemIdAndOrgaId() {
        // Vorbereitung
        Item2Orga item2Orga = new Item2Orga();
        item2Orga.setItemId(1L);
        item2Orga.setOrgaId(100L);
        item2OrgaRepository.save(item2Orga);

        // Ausführen
        int deletedCount = item2OrgaRepository.deleteByItemIdAndOrgaId(1L, 100L);

        // Überprüfen
        assertEquals(1, deletedCount);
    }

    @Test
    public void testDeleteAllByItemId() {
        // Vorbereitung
        Item2Orga item2Orga1 = new Item2Orga();
        item2Orga1.setItemId(1L);
        item2Orga1.setOrgaId(100L);
        item2OrgaRepository.save(item2Orga1);

        Item2Orga item2Orga2 = new Item2Orga();
        item2Orga2.setItemId(1L);
        item2Orga2.setOrgaId(101L);
        item2OrgaRepository.save(item2Orga2);

        // Ausführen
        int deletedCount = item2OrgaRepository.deleteAllByItemId(1L);

        // Überprüfen
        assertEquals(2, deletedCount);
    }
}
