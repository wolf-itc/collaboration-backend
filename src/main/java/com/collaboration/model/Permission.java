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

    @Column(name = "itemtypeid", nullable = false)
    private long itemtypeId;

    @Column(name = "itemid")
    private Long itemId; // Optional, therefore Long
    
    @Column(name = "roleid", nullable = false)
    private long roleId;

    @Column(name = "permission", nullable = false)
    private String permission;

    public Permission(long itemtypeid, long itemId, Long roleId, String permission) {
        this.itemtypeId = itemtypeid;
        this.itemId = itemId;
        this.roleId = roleId;
        this.permission = permission;
    }
    
    public Permission() {
    }
}
