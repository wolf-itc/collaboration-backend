/**
 *  NonUserRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NonUserRepository extends JpaRepository<NonUser, Long> {

  @Query("SELECT n FROM NonUser n INNER JOIN Item i ON i.userornonuserid=n.id INNER JOIN Item2Orga i2o ON i2o.itemid=i.id WHERE i2o.orgaid = :orgaid")
  List<NonUser> findByOrgaid(final long orgaid);

  List<NonUser> findByName(String name); // Find NonUsers by name

  List<NonUser> findByCreatedById(int createdById); // Find NonUsers by creator ID
}
