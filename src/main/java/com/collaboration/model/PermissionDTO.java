/**
 *  PermissionDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;

@Data
public class PermissionDTO {
    private long id;
    private long itemtypeid;
    private Long itemid; // Optional, therefore Long
    private long roleid;
    private String permission;
}
