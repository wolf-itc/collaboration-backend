/**
 *  ItemRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  
  @Query("SELECT i FROM Item i WHERE i.itId = :itId AND i.userOrNonuserId = :userOrNonuserId")
  Optional<Item> findByItIdAndUserOrNonuserId(final long itId, final long userOrNonuserId);

  @Query("SELECT i FROM Item i WHERE i.itId <> :itId AND i.userOrNonuserId = :userOrNonuserId")
  Optional<Item> findByItIdNotAndUserOrNonuserId(final long itId, final long userOrNonuserId);
  
  @Modifying
  @Query("DELETE FROM Item i WHERE i.itId = :itId AND i.userOrNonuserId = :userOrNonuserId")
  int deleteByItIdAndUserOrNonuserId(final long itId, final long userOrNonuserId);

  @Modifying
  @Query("DELETE FROM Item i WHERE i.itId <> :itId AND i.userOrNonuserId = :userOrNonuserId")
  int deleteByItIdNotAndUserOrNonuserId(final long itId, final long userOrNonuserId);
}
