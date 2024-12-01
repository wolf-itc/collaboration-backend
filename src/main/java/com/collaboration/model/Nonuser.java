/**
 *  Nonuser
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

@Entity
@Table(name = "nonuser")
@Data
public class Nonuser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id; // Primary key, auto-incremented

  @Column(nullable = false, length = 500)
  private String name; // Name of the non-user, max 500 characters

  @Column(nullable = false, length = 50000)
  private String content; // Content associated with the non-user, max 50000 characters

  @Column(name = "userid", nullable = false)
  private long userId; // ID of the user who created this record

  @Column(name = "orgaid", nullable = false)
  private long orgaId; // ID of the organization that created this record
}
