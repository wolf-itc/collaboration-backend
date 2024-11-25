/**
 *  Role
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "orgaid", nullable = false)
  private long orgaid;

  @Column(name = "rolename", nullable = false, length = 100)
  private String rolename;

  // Getters and Setters
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

  public String getRolename() {
    return rolename;
  }

  public void setRolename(String rolename) {
    this.rolename = rolename;
  }
}
