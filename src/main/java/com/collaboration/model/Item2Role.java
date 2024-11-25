/**
 *  Item2Role
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item2role")
public class Item2Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itemid", nullable = false)
    private long itemid;

    @Column(name = "roleid", nullable = false)
    private long roleid;

    public Item2Role(long id, long itemid, long roleid) {
        this.id = id;
        this.itemid = itemid;
        this.roleid = roleid;
    }

    public Item2Role() {
    }
    
    // Getters und Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemid() {
        return itemid;
    }

    public void setItemid(long itemid) {
        this.itemid = itemid;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }
}
