/**
 *  Authority
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "authorities")
@Data
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userid;
    private String username;

    private String authority;

    public Authority(long userid, String username, String authority) {
        this.userid = userid;
        this.username = username;
        this.authority = authority;
    }

    public Authority() {
    }
}