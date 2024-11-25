/**
 *  UserDTO
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class UserDTO {
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
    private OffsetDateTime lastlogin;
    private OffsetDateTime lastfaillogin;
    private OffsetDateTime lastnotifications;
    private int faillogincount;
    private String shownotifications;
    private boolean showintro;
    private boolean showhelp;
    private boolean showclearname;
    private boolean showcontact;
    private String language;
    private String activationkey;
    private String currentUsername;
    private String oldPassword;
    private String mailSubject;
    private String mailBody;
    
    // The orgaid from which the request comes
    private long orgaid;
}