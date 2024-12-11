/**
 *  PermissionEvaluator - Evaluates if current user has access to wanted itemtype
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.PermissionDTO;
import com.collaboration.model.RoleDTO;
import com.collaboration.service.ItemService;
import com.collaboration.service.PermissionService;
import com.collaboration.service.RoleService;
import com.collaboration.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionEvaluator {

  private final ItemService itemService;
  private final UserService userService;
  private final RoleService roleService;
  private final PermissionService permissionService;

  public PermissionEvaluator(final ItemService itemService, final UserService userService, final RoleService roleService, final PermissionService permissionService) {
    this.itemService = itemService;
    this.userService = userService;
    this.roleService = roleService;
    this.permissionService = permissionService;
  }

  /**
   * Checks for permission to 'c'reate
   * 
   * @param orgaId
   * @param itemtypeId
   * @throws CollaborationException
   */
  public void mayCreate(final long orgaId, final long itemtypeId) throws CollaborationException {
    log.trace("> mayCreate orgaId={} itemtypeId={}", orgaId, itemtypeId);
    
    if (!getAccess(orgaId, itemtypeId, null).contains(AccessType.CREATE)) {
      log.trace("< mayCreate: NOK");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< mayCreate: ok");
  }

  /**
   * Checks for permission to 'r'ead
   * 
   * @param orgaId  The organization the requested object belongs to
   * @param itemtypeId
   * @param itemId
   * @throws CollaborationException
   */
  public void mayRead(final long orgaId, final long itemtypeId, final Long itemId) throws CollaborationException {
    log.trace("> mayRead orgaId={} itemtypeId={}", orgaId, itemtypeId);
    
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.READ)) {
      log.trace("< mayRead: NOK");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< mayRead: ok");
  }

  /**
   * Checks for permission to 'u'pdate
   * 
   * @param orgaId
   * @param itemtypeId
   * @param itemId
   * @throws CollaborationException
   */
  public void mayUpdate(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    log.trace("> mayUpdate orgaId={} itemtypeId={}", orgaId, itemtypeId);
    
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.UPDATE)) {
      log.trace("< mayUpdate: NOK");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< mayUpdate: ok");
  }

  /**
   * Checks for permission to 'd'elete
   * 
   * @param orgaId
   * @param itemtypeId
   * @param itemId
   * @throws CollaborationException
   */
  public void mayDelete(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    log.trace("> mayDelete orgaId={} itemtypeId={}", orgaId, itemtypeId);
    
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.DELETE)) {
      log.trace("< mayDelete: NOK");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< mayDelete: ok");
  }

  /**
   * Checks for permission to e'x'ecute. Is a special right and can be user to execute a special functionality
   * 
   * @param orgaId
   * @param itemtypeId
   * @param itemId
   * @throws CollaborationException
   */
  public void mayExecute(final long orgaId, final long itemtypeId, final long itemId) throws CollaborationException {
    log.trace("> mayExecute orgaId={} itemtypeId={}", orgaId, itemtypeId);
    
    if (!getAccess(orgaId, itemtypeId, itemId).contains(AccessType.EXECUTE)) {
      log.trace("< mayExecute: NOK");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< mayExecute: ok");
  }

  /**
   * Return a list of granted access-rights to the checked resource
   * 
   * @param orgaId
   * @param itemtypeId
   * @param itemId
   * @return
   * @throws CollaborationException
   */
  private List<AccessType> getAccess(final long orgaId, final long itemtypeId, final Long itemId) throws CollaborationException {
    log.trace("> getAccess orgaId={} itemtypeId={} itemId={}", orgaId, itemtypeId, itemId);
    
    // Set the GUEST-role
    List<Long> userroleIds;
    try {
      final var userId = userService.getCurrentUserId();
      userroleIds= getUserRoleIds(userId, orgaId);
    } catch (CollaborationException ex) {
      
      // If user is not authenticated, then set the GUEST-role
      userroleIds= List.of(roleService.getRoleByRolenameAndOrgaId("GUEST", orgaId).getId());
    }
    
    // If current user is Yare-Admin, then he's got all rights
    if (userroleIds.contains(AppConfig.ROLEID_YARE_ADMIN)) {
      log.trace("< getAccess: YARE_ADMIN");
      return AccessType.getAllAccessTypes();
    }
    
    List<PermissionDTO> listPermission;
    if (itemId == null) {
      // No special item given, so search for null (=item-independent permission)
      listPermission = permissionService.getByItemtypeIdAndRoleIdsIn(itemtypeId, userroleIds);
    } else {
      listPermission = permissionService.getByItemtypeIdAndItemIdAndRoleIdsIn(itemtypeId, itemId, userroleIds);
    }
    if (listPermission.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED);
    }
    final var combinedPermissions = listPermission.stream().map(PermissionDTO::getPermission).collect(Collectors.joining());

    log.trace("< getAccess:  combinedPermissions={}", combinedPermissions);
    return getAccessTypes(combinedPermissions);
  }
  
  /**
   * Returns all roles of current user but only valid for requesting organization
   * 
   * @param userId
   * @param orgaId
   * @return
   * @throws CollaborationException 
   */
  private List<Long> getUserRoleIds(final long userId, final long orgaId) throws CollaborationException {
    log.trace("> getUserRoleIds userId={} orgaId={}", userId, orgaId);
    
    // Spring security gives us all roles of current user. This could also be roles of another organization (is user is part of more than one organization)
    // var authentication = SecurityContextHolder.getContext().getAuthentication();
    // var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(r->r.replace("ROLE_", "")).toList();
    // Get roles not from authorities, but form item2role
    var userRoleIds = itemService.getItemByUserId(userId, List.of(ItemService.SubItems.ROLES)).getRoleDTOs().stream().map(RoleDTO::getId).toList();
    
    // Admin has all rights, so only return this, not filtered by any orgaId
    if (userRoleIds.contains(AppConfig.ROLEID_YARE_ADMIN)) {
      log.trace("< getUserRoleIds: YARE_ADMIN");
      return List.of(AppConfig.ROLEID_YARE_ADMIN);
    }
    
    // So retrieve all roles of requesting organization
    var userRolesOfOrga = roleService.getRolesByOrgaIdAndRoleIds(orgaId, userRoleIds).stream().map(r->r.getId()).toList();
    
    // And match them both
    log.trace("< getUserRoleIds: userRolesOfOrga={}", userRolesOfOrga);
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
