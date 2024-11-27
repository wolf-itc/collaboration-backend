/**
 *  NonUser
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "nonuser")
@Data
public class NonUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id; // Primary key, auto-incremented

  @Column(nullable = false, length = 500)
  private String name; // Name of the non-user, max 500 characters

  @Column(nullable = false, length = 50000)
  private String content; // Content associated with the non-user, max 50000 characters

  @Column(name = "createdbyid", nullable = false)
  private int createdById; // ID of the user who created this record

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "item", referencedColumnName = "userornonuserid")
  private Item Item;
}
