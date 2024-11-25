/**
 *  Item2OrgaRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Item2OrgaRepository extends JpaRepository<Item2Orga, Long> {
  void deleteByItemIdAndOrgaId(final long itemid, final long orgaid);
}
