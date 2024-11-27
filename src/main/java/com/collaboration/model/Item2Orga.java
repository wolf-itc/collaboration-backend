/**
 *  Item2Orga
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item2orga")
public class Item2Orga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itemid", nullable = false)
    private long itemid;

    @Column(name = "orgaid", nullable = false)
    private long orgaid;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemid")
    private Item item;
*/
    public Item2Orga(long id, long itemid, long orgaid) {
      super();
      this.id = id;
      this.itemid = itemid;
//      this.item.setItid(itemid);
      this.orgaid = orgaid;
    }

    // Getters und Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemid;
    }

    public void setItemId(long itemid) {
        this.itemid = itemid;
    }

    public long getOrgaId() {
        return orgaid;
    }

    public void setOrgaId(long orgaid) {
        this.orgaid = orgaid;
    }

}
