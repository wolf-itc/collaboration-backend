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
    private long itemtypeId;
    private Long itemId; // Optional, therefore Long
    private long roleId;
    private String permission;
}
