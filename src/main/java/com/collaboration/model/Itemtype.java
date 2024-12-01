/**
 *  Itemtype
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "itemtype")
public class Itemtype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "orgaid", nullable = false)
    private long orgaId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;
}
