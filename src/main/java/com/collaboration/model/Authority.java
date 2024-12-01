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

    @Column(name = "userid", nullable = false)
    private long userId;

    @Column(name = "username", nullable = false)
    private String userName;

    private String authority;

    public Authority() {
    }

    public Authority(long userid, String username, String authority) {
        this.userId = userid;
        this.userName = username;
        this.authority = authority;
    }
}