/**
 *  UserRoleOrchestrator
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Item2Role;
import com.collaboration.model.Item2RoleRepository;
import com.collaboration.model.RoleDTO;

@Service
public class UserRoleOrchestrator {

  private final UserService userService;
  private final RoleService roleService;
  private final ItemService itemService;
  private final Item2RoleRepository item2RoleRepository;

  @Autowired
  public UserRoleOrchestrator(UserService userService, RoleService roleService, ItemService itemService, Item2RoleRepository item2RoleRepository) {
      this.userService = userService;
      this.roleService = roleService;
      this.itemService = itemService;
      this.item2RoleRepository = item2RoleRepository;
  }

  public List<RoleDTO> getUserRoles(final long userId) throws CollaborationException {
    // Check if user exists
    userService.getUserById(userId);
   
    // Retrieve item for this user
    var item = itemService.getItemByUserId(userId);
    
    // And the roles of this item
    var roleIds = item2RoleRepository.findAllByItemId(item.getId()).stream().map(Item2Role::getRoleId).toList();

    // Map the roles to only the names of the roles
    return roleService.getRolesByRoleIds(roleIds);
  }
  
}
