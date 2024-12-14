/**
 *  ItemtypeRepositoryTest
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
public class ItemtypeRepositoryTest {

    @Autowired
    private ItemtypeRepository itemtypeRepository;

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
        Itemtype itemtype1 = new Itemtype();
        itemtype1.setOrgaId(100L);
        itemtype1.setName("Itemtype 1");
        itemtypeRepository.save(itemtype1);

        Itemtype itemtype2 = new Itemtype();
        itemtype2.setOrgaId(100L);
        itemtype2.setName("Itemtype 2");
        itemtypeRepository.save(itemtype2);

        // Ausführen
        List<Itemtype> result = itemtypeRepository.findByOrgaId(100L);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
