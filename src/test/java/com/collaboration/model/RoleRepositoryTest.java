/**
 *  RoleRepositoryTest
 *  
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private long testOrgaId;
    private long testOrgaId2;

    @BeforeEach
    public void setUp() {
        // Set up organization id (mock or hardcode for testing)
        testOrgaId = 1L;
        testOrgaId2 = 2L;

        // Save a couple of roles for testing
        Role role1 = new Role();
        role1.setOrgaId(testOrgaId);
        role1.setRolename("Admin");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setOrgaId(testOrgaId);
        role2.setRolename("User");
        roleRepository.save(role2);

        Role role3 = new Role();
        role3.setOrgaId(testOrgaId2);
        role3.setRolename("Manager");
        roleRepository.save(role3);
    }

    @Test
    public void testFindByOrgaId() {
        // Abrufen der Rollen für eine gegebene OrgaId
        List<Role> result = roleRepository.findByOrgaId(testOrgaId);

        // Überprüfen
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertEquals(testOrgaId, result.get(0).getOrgaId());
    }

    @Test
    public void testFindByOrgaIdAndRolenameIn() {
        // Abrufen der Rollen anhand der OrgaId und einer Liste von Rolennamen
        List<Role> result = roleRepository.findByOrgaIdAndRolenameIn(testOrgaId, List.of("Admin", "User"));

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByOrgaIdAndIdIn() {
        // Abrufen der Rollen anhand der OrgaId und einer Liste von Ids
        List<Long> ids = LongStream.rangeClosed(1, 100).boxed().toList();
        List<Role> result = roleRepository.findByOrgaIdAndIdIn(testOrgaId, ids);

        // Überprüfen
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindRoleByRolenameAndOrgaId() {
        // Abrufen der Rolle anhand des Rollennamens und der OrgaId
        Optional<Role> result = roleRepository.findRoleByRolenameAndOrgaId("Admin", testOrgaId);

        // Überprüfen
        assertTrue(result.isPresent());
        assertEquals("Admin", result.get().getRolename());
        assertEquals(testOrgaId, result.get().getOrgaId());
    }

    @Test
    public void testFindRoleByRolenameAndOrgaIdNotFound() {
        // Abrufen einer Rolle, die nicht existiert
        Optional<Role> result = roleRepository.findRoleByRolenameAndOrgaId("NonExistentRole", testOrgaId);

        // Überprüfen, dass kein Ergebnis gefunden wird
        assertTrue(result.isEmpty());
    }
}
