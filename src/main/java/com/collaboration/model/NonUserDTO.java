/**
 *  NonUserDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;

@Data
public class NonUserDTO {
    private long id;           // Primary key
    private String name;       // Name of the non-user
    private String content;    // Content associated with the non-user
    private int createdById;   // ID of the creator
}
