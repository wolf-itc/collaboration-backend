/**
 *  Item2Orga
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item2orga")
public class Item2Orga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itemid", nullable = false)
    private long itemId;

    @Column(name = "orgaid", nullable = false)
    private long orgaId;

    public Item2Orga(long id, long itemId, long orgaId) {
      super();
      this.id = id;
      this.itemId = itemId;
      this.orgaId = orgaId;
    }

    // Getters und Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getOrgaId() {
        return orgaId;
    }

    public void setOrgaId(long orgaId) {
        this.orgaId = orgaId;
    }
}
