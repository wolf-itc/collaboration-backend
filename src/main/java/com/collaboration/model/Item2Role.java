/**
 *  Item2Role
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "item2role")
public class Item2Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itemid", nullable = false)
    private long itemId;

    @Column(name = "roleid", nullable = false)
    private long roleId;

    public Item2Role() {
    }

    public Item2Role(long id, long itemId, long roleId) {
        this.id = id;
        this.itemId = itemId;
        this.roleId = roleId;
    }
}
