/**
 *  UserDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;

@Data
public class OrganizationDTO {
  private long id;
  private String name;
  private String description;
  private String logo;
  private String prefix;
}