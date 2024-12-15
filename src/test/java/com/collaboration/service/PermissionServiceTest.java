/**
 *  PermissionDTO
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
import com.collaboration.model.Permission;
import com.collaboration.model.PermissionDTO;
import com.collaboration.model.PermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;
    
    @InjectMocks
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createPermission_ShouldCreatePermission() throws CollaborationException {
        // Arrange
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setItemtypeId(1L);
        permissionDTO.setItemId(2L);
        permissionDTO.setRoleId(3L);
        permissionDTO.setPermission("READ");

        Permission permission = new Permission();
        permission.setId(1L);

        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        // Act
        PermissionDTO result = permissionService.createPermission(permissionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void updatePermission_ShouldUpdatePermission() throws CollaborationException {
        // Arrange
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(1L);
        permissionDTO.setPermission("WRITE");

        Permission existingPermission = new Permission();
        existingPermission.setId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(existingPermission);

        // Act
        PermissionDTO result = permissionService.updatePermission(permissionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void deletePermission_ShouldDeletePermissionById() throws CollaborationException {
        // Arrange
        Permission existingPermission = new Permission();
        existingPermission.setId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existingPermission));

        // Act
        permissionService.deletePermission(1L);

        // Assert
        verify(permissionRepository).deleteById(1L);
    }

    @Test
    void getPermissionById_ShouldReturnPermission() throws CollaborationException {
        // Arrange
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setPermission("READ");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        // Act
        PermissionDTO result = permissionService.getPermissionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("READ", result.getPermission());
    }

    @Test
    void getAllPermissions_ShouldReturnAllPermissions() {
        // Arrange
        Permission permission1 = new Permission();
        permission1.setId(1L);
        permission1.setPermission("READ");

        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setPermission("WRITE");

        when(permissionRepository.findAll()).thenReturn(List.of(permission1, permission2));

        // Act
        List<PermissionDTO> result = permissionService.getAllPermissions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllPermissionsByOrgaId_ShouldReturnPermissionsForOrganization() {
        // Arrange
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setItemtypeId(2L);

        when(permissionRepository.findByOrgaId(2L)).thenReturn(List.of(permission));

        // Act
        List<PermissionDTO> result = permissionService.getAllPermissionsByOrgaId(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getItemtypeId());
    }

    @Test
    void getByItemtypeIdAndItemIdAndRoleIdsIn_ShouldReturnMatchingPermissions() {
        // Arrange
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setItemtypeId(1L);
        permission.setItemId(2L);
        permission.setRoleId(3L);

        when(permissionRepository.findByItemtypeIdAndItemIdAndRoleIdIn(1L, 2L, List.of(3L)))
                .thenReturn(List.of(permission));

        // Act
        List<PermissionDTO> result = permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(1L, 2L, List.of(3L));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getItemtypeId());
        assertEquals(2L, result.get(0).getItemId());
        assertEquals(3L, result.get(0).getRoleId());
    }

    @Test
    void getByItemtypeIdAndRoleIdsIn_ShouldReturnMatchingPermissions() {
        // Arrange
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setItemtypeId(1L);
        permission.setRoleId(3L);

        when(permissionRepository.findByItemtypeIdAndRoleIdIn(1L, List.of(3L)))
                .thenReturn(List.of(permission));

        // Act
        List<PermissionDTO> result = permissionService.getByItemtypeIdAndRoleIdsIn(1L, List.of(3L));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getItemtypeId());
        assertEquals(3L, result.get(0).getRoleId());
    }

    @Test
    void createPermission_ShouldThrowException_WhenSaveFails() {
        // Arrange
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPermission("READ");

        when(permissionRepository.save(any(Permission.class))).thenThrow(new RuntimeException("DB Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> permissionService.createPermission(permissionDTO));
    }
}
