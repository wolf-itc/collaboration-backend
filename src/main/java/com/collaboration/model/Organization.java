/**
 *  Organization
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "organization")
@Data
public class Organization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private String description;
  private byte[] logo;
  private String prefix;
}