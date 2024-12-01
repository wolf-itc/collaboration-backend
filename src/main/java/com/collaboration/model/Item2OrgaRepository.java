/**
 *  Item2OrgaRepository
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
public interface Item2OrgaRepository extends JpaRepository<Item2Orga, Long> {

  List<Item2Orga> findAllByItemId(final long itenId);
  
  int deleteByItemIdAndOrgaId(final long itemId, final long orgaId);

  int deleteAllByItemId(final long itemId);
}
