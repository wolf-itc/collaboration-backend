/**
 *  Permission
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "permission")
@Data
public class Permission {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long itemtypeid;
    private Long itemid; // Optional, therefore Long
    private long roleid;

    private String permission;

    public Permission(long itemtypeid, long itemid, Long roleid, String permission) {
        this.itemtypeid = itemtypeid;
        this.itemid = itemid;
        this.roleid = roleid;
        this.permission = permission;
    }

    public Permission() {
    }
}
