/**
 *  RoleDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;

@Data
public class RoleDTO {
    private long id;
    private long orgaId;
    private String rolename;
    
    public RoleDTO() {
    }

    public RoleDTO(long id, long orgaId, String rolename) {
      super();
      this.id = id;
      this.orgaId = orgaId;
      this.rolename = rolename;
    }
    
}
