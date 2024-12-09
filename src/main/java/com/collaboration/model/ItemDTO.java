/**
 *  ItemDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ItemDTO {
  
  private long id;
  private long itId;
  private long userOrNonuserId;
  private List<OrganizationDTO> organizationDTOs = new ArrayList<>();
  private List<RoleDTO> roleDTOs = new ArrayList<>();

  public ItemDTO() {
  }

  public ItemDTO(long id, long itId, long userOrNonuserId) {
    this.id = id;
    this.itId = itId;
    this.userOrNonuserId = userOrNonuserId;
  }
}
