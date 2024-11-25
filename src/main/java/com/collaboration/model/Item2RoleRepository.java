/**
 *  Item2RoleRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Item2RoleRepository extends JpaRepository<Item2Role, Long> {
    
  void deleteByItemidAndRoleid(final long itemid, final long roleid);
    
  List<Item2Role> findAllByItemid(final long itemid);
}
