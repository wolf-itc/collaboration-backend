/**
 *  PermissionEvaluator - Evaluates if current has has wanted access to accessed item
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.PermissionDTO;
import com.collaboration.model.RoleDTO;
import com.collaboration.service.PermissionService;
import com.collaboration.service.RoleService;
import com.collaboration.service.UserRoleOrchestrator;
import com.collaboration.service.UserService;

@Service
public class PermissionEvaluator {

  private final UserRoleOrchestrator userRoleOrchestrator;
  private final UserService userService;
  private final RoleService roleService;
  private final PermissionService permissionService;

  public PermissionEvaluator(final UserRoleOrchestrator userRoleOrchestrator, final UserService userService, final RoleService roleService, final PermissionService permissionService) {
    this.userRoleOrchestrator = userRoleOrchestrator;
    this.userService = userService;
    this.roleService = roleService;
    this.permissionService = permissionService;
  }

  public long getCurrentUserId() throws CollaborationException {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByUsername(username);
    return user.getId();
  }

  public void mayCreate(final long orgaId, final long itemtypeId) throws CollaborationException {
    if (!getAccess(orgaId, itemtypeId, null).contains(AccessType.CREATE)) {
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }
  }

  public void mayRead(final long orgaId, final long itemtypeId, final Long itemId) throws CollaborationException {
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.READ)) {
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }
  }

  public void mayUpdate(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.UPDATE)) {
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }
  }

  public void mayDelete(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.DELETE)) {
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }
  }

  public void mayExecute(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.EXECUTE)) {
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }
  }

  private List<AccessType> getAccess(final long orgaId, final long itemtypeId, final Long itemId) throws CollaborationException {
    var userId = getCurrentUserId();
    var userroleIds= getUserRoleIds(userId, orgaId);
    
    // If current user is Yare-Admin, then he's got all rights
    if (userroleIds.contains(AppConfig.ROLE_ADMIN)) {
      return AccessType.getAllAccessTypes();
    }
    
    List<PermissionDTO> listPermission;
    if (itemId == null) {
      // No special item given, so search for null (=item-independent permission)
      listPermission = permissionService.getByAndItemtypeidAndRoleidIn(itemtypeId, userroleIds);
    } else {
      listPermission = permissionService.getByAndItemtypeidAndItemidAndRoleidIn(itemtypeId, itemId, userroleIds);
    }
    if (listPermission.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED);
    }
    String combinedPermissions = listPermission.stream().map(PermissionDTO::getPermission).collect(Collectors.joining());
    return getAccessTypes(combinedPermissions);
  }
  
  /**
   * Return all roles of current user but only valid for requesting organization
   * 
   * @param userId
   * @param orgaId
   * @return
   * @throws CollaborationException 
   */
  private List<Long> getUserRoleIds(final long userId, final long orgaId) throws CollaborationException {
    // Spring security gives us all roles of current user. This could also be roles of another organization (is user is part of more than one organization)
    // var authentication = SecurityContextHolder.getContext().getAuthentication();
    // var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(r->r.replace("ROLE_", "")).toList();
    // Get roles not from authorities, but form item2role
    var userRoleIds = userRoleOrchestrator.getUserRoles(userId).stream().map(RoleDTO::getId).toList();
    
    // Admin has all rights, so only return this, not filtered by any orgaId
    if (userRoleIds.contains(AppConfig.ROLE_ADMIN)) {
      return List.of(AppConfig.ROLE_ADMIN);
    }
    
    // So retrieve all roles of requesting organization
    var userRolesOfOrga = roleService.getRolesByOrgaIdAndRoleIds(orgaId, userRoleIds).stream().map(r->r.getId()).toList();
    
    // And match them both
    return userRolesOfOrga;
  }

  /**
   * Translates a permission string like 'WX' into a list of AccessRights as list of enumeration-type
   * 
   * @param permissionString
   * @return
   */
  private List<AccessType> getAccessTypes(String permissionString) {
    return permissionString.chars().mapToObj(c->AccessType.getByAccessTypeId((char)c)).toList();
  }
}
