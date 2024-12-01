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
import lombok.Data;

@Data
@Entity
@Table(name = "item2orga")
public class Item2Orga {

//  @ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(name = "itemid", referencedColumnName = "id")
//  private Item item;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "orgaid", nullable = false)
  private long orgaId;

  @Column(name = "itemid", nullable = false) //(insertable=false, updatable=false)
  private long itemId;

  
  public Item2Orga() {
  }
  
  public Item2Orga(long id, long itemId, long orgaId) {
    this.id = id;
    this.itemId = itemId;
    this.orgaId = orgaId;
  }
}
