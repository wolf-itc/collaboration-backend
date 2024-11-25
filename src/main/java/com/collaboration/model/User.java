/**
 *  User
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id; // needed by spring-security
  private String username; // needed by spring-security
  private String password; // needed by spring-security
  private boolean enabled; // needed by spring-security
  private String lastname;
  private String firstname;
  private String email;
  private String postcode;
  private String city;
  private String address;
  private String telephone;
  private String mobile;
  private String idcardnumber;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastlogin;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastfaillogin;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastnotifications;

  private int faillogincount;
  private String shownotifications;
  private boolean showintro;
  private boolean showhelp;
  private boolean showclearname;
  private boolean showcontact;
  private String language;
  private String activationkey;
  private String mailSubject;
  private String mailBody;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
      this.enabled = enabled;
  }
}