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

  @Column(name = "idcardnumber", nullable = true)
  private String idcardNumber;

  @Column(name = "lastlogin", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastLogin;

  @Column(name = "lastfaillogin", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastFaillogin;

  @Column(name = "lastnotifications", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastNotifications;

  @Column(name = "faillogincount", nullable = false)
  private int failloginCount;

  @Column(name = "shownotifications", nullable = false)
  private String showNotifications;

  @Column(name = "showintro", nullable = false)
  private boolean showIntro;

  @Column(name = "showhelp", nullable = false)
  private boolean showHelp;
  
  @Column(name = "showclearname", nullable = false)
  private boolean showClearname;

  @Column(name = "showcontact", nullable = false)
  private boolean showContact;

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