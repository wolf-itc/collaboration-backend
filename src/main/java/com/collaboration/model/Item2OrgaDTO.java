/**
 *  Item2OrgaDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;

@Data
public class Item2OrgaDTO {
    private long id;
    private int itemId;
    private int orgaId;
}
