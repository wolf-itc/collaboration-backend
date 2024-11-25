/**
 *  ItemType
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itemtype")
public class ItemType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "orgaid", nullable = false)
    private long orgaid;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    // Getters und Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrgaid() {
        return orgaid;
    }

    public void setOrgaid(long orgaid) {
        this.orgaid = orgaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
