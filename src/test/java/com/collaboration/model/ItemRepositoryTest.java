/**
 *  ItemRepositoryTest
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
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

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
    public void testFindByItIdAndUserOrNonuserId() {
        // Vorbereitung
        Item item = new Item();
        item.setItId(1L);
        item.setUserOrNonuserId(100L);
        itemRepository.save(item);

        // Ausführen
        Optional<Item> result = itemRepository.findByItIdAndUserOrNonuserId(1L, 100L);

        // Überprüfen
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getItId());
        assertEquals(100L, result.get().getUserOrNonuserId());
    }

    @Test
    public void testFindByItIdNotAndUserOrNonuserId() {
        // Vorbereitung
        Item item1 = new Item();
        item1.setItId(1L);
        item1.setUserOrNonuserId(100L);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setItId(2L);
        item2.setUserOrNonuserId(100L);
        itemRepository.save(item2);

        // Ausführen
        Optional<Item> result = itemRepository.findByItIdNotAndUserOrNonuserId(1L, 100L);

        // Überprüfen
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getItId());
        assertEquals(100L, result.get().getUserOrNonuserId());
    }

    @Test
    public void testDeleteByItIdAndUserOrNonuserId() {
        // Vorbereitung
        Item item = new Item();
        item.setItId(1L);
        item.setUserOrNonuserId(100L);
        itemRepository.save(item);

        // Ausführen
        int deletedCount = itemRepository.deleteByItIdAndUserOrNonuserId(1L, 100L);

        // Überprüfen
        assertEquals(1, deletedCount);
    }

    @Test
    public void testDeleteByItIdNotAndUserOrNonuserId() {
        // Vorbereitung
        Item item1 = new Item();
        item1.setItId(1L);
        item1.setUserOrNonuserId(100L);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setItId(2L);
        item2.setUserOrNonuserId(100L);
        itemRepository.save(item2);

        // Ausführen
        int deletedCount = itemRepository.deleteByItIdNotAndUserOrNonuserId(1L, 100L);

        // Überprüfen
        assertEquals(1, deletedCount);
    }
}
