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
    private long orgaid;
    private String rolename;
}
