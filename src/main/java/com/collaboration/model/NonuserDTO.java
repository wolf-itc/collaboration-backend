/**
 *  NonuserDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NonuserDTO {
    private long id;        // Primary key
    private String name;    // Name of the non-user
    private String content; // Content associated with the non-user
    @Schema(description = "This UserId will be filled automatically")
    private long userId;     // ID of the creator
    @Schema(description = "This OrgaId will only be saved while creation, but not while updating")
    private long orgaId;

    // The itemtype for this non-user
    private long itemtypeId;
}
