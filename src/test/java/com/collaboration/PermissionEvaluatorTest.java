package com.collaboration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.PermissionDTO;
import com.collaboration.model.RoleDTO;
import com.collaboration.service.ItemService;
import com.collaboration.service.PermissionService;
import com.collaboration.service.RoleService;
import com.collaboration.service.UserService;

@ExtendWith(MockitoExtension.class)
public class PermissionEvaluatorTest {

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionEvaluator permissionEvaluator;

    @Test
    public void testMayCreate_AccessGranted() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndRoleIdsIn(anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("C")));

        // Execute
        permissionEvaluator.mayCreate(orgaId, itemtypeId);
    }

    @Test
    public void testMayCreate_AccessDenied() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndRoleIdsIn(anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("R")));

        // Assert
        assertThrows(CollaborationException.class, () -> permissionEvaluator.mayCreate(orgaId, itemtypeId));
    }

    @Test
    public void testMayRead_AccessGranted() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("R")));

        // Execute
        permissionEvaluator.mayRead(orgaId, itemtypeId, itemId);
    }

    @Test
    public void testMayRead_AccessDenied() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("C")));

        // Assert
        assertThrows(CollaborationException.class, () -> permissionEvaluator.mayRead(orgaId, itemtypeId, itemId));
    }

    @Test
    public void testMayUpdate_AccessGranted() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("U")));

        // Execute
        permissionEvaluator.mayUpdate(orgaId, itemtypeId, itemId);
    }

    @Test
    public void testMayUpdate_AccessDenied() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("R")));

        // Assert
        assertThrows(CollaborationException.class, () -> permissionEvaluator.mayUpdate(orgaId, itemtypeId, itemId));
    }

    @Test
    public void testMayDelete_AccessGranted() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("D")));

        // Execute
        permissionEvaluator.mayDelete(orgaId, itemtypeId, itemId);
    }

    @Test
    public void testMayDelete_AccessDenied() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("R")));

        // Assert
        assertThrows(CollaborationException.class, () -> permissionEvaluator.mayDelete(orgaId, itemtypeId, itemId));
    }

    @Test
    public void testMayExecute_AccessGranted() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("X")));

        // Execute
        permissionEvaluator.mayExecute(orgaId, itemtypeId, itemId);
    }

    @Test
    public void testMayExecute_AccessDenied() throws CollaborationException {
        // Setup
        long orgaId = 1L;
        long itemtypeId = 1L;
        long itemId = 1L;
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(itemService.getItemByUserId(anyLong(), any())).thenReturn(getMockItemWithRoles());
        when(roleService.getRolesByOrgaIdAndRoleIds(anyLong(), any())).thenReturn(List.of(getMockRole()));
        when(permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(anyLong(), anyLong(), any())).thenReturn(List.of(getMockPermissionDTO("R")));

        // Assert
        assertThrows(CollaborationException.class, () -> permissionEvaluator.mayExecute(orgaId, itemtypeId, itemId));
    }

    private com.collaboration.model.ItemDTO getMockItemWithRoles() {
        var itemDTO = new com.collaboration.model.ItemDTO();
        itemDTO.setRoleDTOs(List.of(getMockRoleDTO()));
        return itemDTO;
    }

    private RoleDTO getMockRoleDTO() {
        var roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        return roleDTO;
    }

    private com.collaboration.model.RoleDTO getMockRole() {
        var roleDTO = new com.collaboration.model.RoleDTO();
        roleDTO.setId(1L);
        return roleDTO;
    }

    private PermissionDTO getMockPermissionDTO(String permission) {
        var permissionDTO = new PermissionDTO();
        permissionDTO.setPermission(permission);
        return permissionDTO;
    }
}
