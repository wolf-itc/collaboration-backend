/**
 *  NonuserRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NonuserRepository extends JpaRepository<Nonuser, Long> {

  @Query("SELECT n FROM Nonuser n INNER JOIN Item i ON i.userOrNonuserId=n.id INNER JOIN Item2Orga i2o ON i2o.itemId=i.id WHERE i2o.orgaId = :orgaId")
  List<Nonuser> findByOrgaId(final long orgaId);

  List<Nonuser> findByName(String name); // Find Nonusers by name

  List<Nonuser> findByUserId(int userId); // Find Nonusers by creator ID
}
