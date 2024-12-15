/**
 *  RoleDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Role;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateRole_ShouldCreateRole() throws CollaborationException {
        // Arrange
        RoleDTO roleDTO = new RoleDTO(1L, 1L, "ADMIN");
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        RoleDTO result = roleService.createRole(roleDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testUpdateRole_ShouldUpdateRole() throws CollaborationException {
        // Arrange
        RoleDTO roleDTO = new RoleDTO(1L, 1L, "USER");
        Role existingRole = new Role();
        existingRole.setId(1L);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(existingRole);

        // Act
        RoleDTO result = roleService.updateRole(roleDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testUpdateRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        RoleDTO roleDTO = new RoleDTO(1L, 1L, "USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CollaborationException.class, () -> roleService.updateRole(roleDTO));
    }

    @Test
    void testDeleteRole_ShouldDeleteRoleById() throws CollaborationException {
        // Arrange
        Role existingRole = new Role();
        existingRole.setId(1L);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));

        // Act
        roleService.deleteRole(1L);

        // Assert
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void testDeleteRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CollaborationException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void testGetRoleById_ShouldReturnRole() throws CollaborationException {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setRolename("USER");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Act
        RoleDTO result = roleService.getRoleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER", result.getRolename());
    }

    @Test
    void testGetRoleById_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CollaborationException.class, () -> roleService.getRoleById(1L));
    }

    @Test
    void testGetAllRoles_ShouldReturnAllRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1L);
        role1.setRolename("USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setRolename("ADMIN");

        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

        // Act
        List<RoleDTO> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllRolesByOrgaId_ShouldReturnRolesForOrganization() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setOrgaId(2L);

        when(roleRepository.findByOrgaId(2L)).thenReturn(List.of(role));

        // Act
        List<RoleDTO> result = roleService.getAllRolesByOrgaId(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getOrgaId());
    }

    @Test
    void testGetRolesByOrgaIdAndRoleIds_ShouldReturnMatchingRoles() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setOrgaId(2L);

        when(roleRepository.findByOrgaIdAndIdIn(2L, List.of(1L))).thenReturn(List.of(role));

        // Act
        List<RoleDTO> result = roleService.getRolesByOrgaIdAndRoleIds(2L, List.of(1L));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getOrgaId());
    }

    @Test
    void testGetRoleByRolenameAndOrgaId_ShouldReturnRole() throws CollaborationException {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setRolename("ADMIN");
        role.setOrgaId(2L);

        when(roleRepository.findRoleByRolenameAndOrgaId("ADMIN", 2L)).thenReturn(Optional.of(role));

        // Act
        RoleDTO result = roleService.getRoleByRolenameAndOrgaId("ADMIN", 2L);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getRolename());
        assertEquals(2L, result.getOrgaId());
    }

    @Test
    void testGetRoleByRolenameAndOrgaId_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(roleRepository.findRoleByRolenameAndOrgaId("ADMIN", 2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CollaborationException.class, () -> roleService.getRoleByRolenameAndOrgaId("ADMIN", 2L));
    }
}
