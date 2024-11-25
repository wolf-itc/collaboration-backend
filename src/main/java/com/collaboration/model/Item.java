/**
 *  Item
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itid", nullable = false)
    private long itid;

    @Column(name = "userornonuserid", nullable = false)
    private long userornonuserid;

    public Item(long id, long itid, long userornonuserid) {
      this.id = id;
      this.itid = itid;
      this.userornonuserid = userornonuserid;
    }

    public Item() {
    }

    // Getters und Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItid() {
        return itid;
    }

    public void setItid(long itid) {
        this.itid = itid;
    }

    public long getUserornonuserid() {
        return userornonuserid;
    }

    public void setUserornonuserid(long userornonuserid) {
        this.userornonuserid = userornonuserid;
    }
}
