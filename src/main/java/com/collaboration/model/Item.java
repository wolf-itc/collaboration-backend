/**
 *  Item
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
@Table(name = "item")
public class Item {

//  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//  private Set<Item2Orga> item2orga = new HashSet<>();


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  @Column(name = "itid", nullable = false)
  private long itId;
  
  @Column(name = "userornonuserid", nullable = false)
  private long userOrNonuserId;
  
  
  public Item() {
  }
  
  public Item(final long id, final long itId, final long userOrNonuserId) {
    this.id = id;
    this.itId = itId;
    this.userOrNonuserId = userOrNonuserId;
  }
}
