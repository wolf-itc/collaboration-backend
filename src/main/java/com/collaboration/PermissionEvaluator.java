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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Item2Role;
import com.collaboration.model.Item2RoleRepository;
import com.collaboration.model.Permission;
import com.collaboration.model.PermissionRepository;
import com.collaboration.model.RoleRepository;
import com.collaboration.model.UserRepository;

@Service
public class PermissionEvaluator {

  @Autowired
  PermissionRepository permissionRepository;

  @Autowired
  UserRepository userRepository;
  
  @Autowired
  RoleRepository roleRepository;

  @Autowired
  private Item2RoleRepository item2RoleRepository;

  public long getCurrentUserId() throws CollaborationException {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsername(username).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));
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
    List<Permission> listPermission;
    if (itemId == null) {
      // No special item given, so search for null (=item-independent permission)
      listPermission = permissionRepository.findByAndItemtypeidAndRoleidIn(itemtypeId, userroleIds);
    } else {
      listPermission = permissionRepository.findByAndItemtypeidAndItemidAndRoleidIn(itemtypeId, itemId, userroleIds);
    }
    if (listPermission.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED);
    }
    String combinedPermissions = listPermission.stream().map(Permission::getPermission).collect(Collectors.joining());
    return getAccessTypes(combinedPermissions);
  }
  
  /**
   * Return all roles of current user but only valid for requesting organization
   * 
   * @param userId
   * @param orgaId
   * @return
   */
  private List<Long> getUserRoleIds(final long userId, final long orgaId) {
    // Spring security gives us all roles of current user. This could also be roles of another organization (is user is part of more than one organization)
    // var authentication = SecurityContextHolder.getContext().getAuthentication();
    // var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(r->r.replace("ROLE_", "")).toList();
    // Get roles not from authorities, but form item2role
    var userRoleIds = item2RoleRepository.findAllByItemid(userId).stream().map(Item2Role::getRoleid).toList();
    
    // So retrieve all roles of requesting organization
    var userRolesOfOrga = roleRepository.findByOrgaidAndIdIn(orgaId, userRoleIds).stream().map(r->r.getId()).toList();
    
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
