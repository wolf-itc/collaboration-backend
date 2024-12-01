/**
 *  ItemDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class ItemDTO {
  
  private long id;
  private long itId;
  private long userOrNonuserId;
  private Set<Item2OrgaDTO> item2Orgas = new HashSet<>();
  private Set<Item2RoleDTO> item2Roles = new HashSet<>();

  public ItemDTO() {
  }

  public ItemDTO(long id, long itId, long userOrNonuserId) {
    this.id = id;
    this.itId = itId;
    this.userOrNonuserId = userOrNonuserId;
  }
}
