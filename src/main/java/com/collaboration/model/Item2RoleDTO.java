/**
 *  Item2RoleDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.Optional;

import lombok.Data;

@Data
public class Item2RoleDTO {
    private long id;
    private long itemId;
    private long roleId;
    Optional<ItemDTO> itemDTO;
    Optional<RoleDTO> roleDTO;
    
    public Item2RoleDTO() {
    }
    
    public Item2RoleDTO(long id, long itemId, long roleId) {
      this.id = id;
      this.itemId = itemId;
      this.roleId = roleId;
    }
}
