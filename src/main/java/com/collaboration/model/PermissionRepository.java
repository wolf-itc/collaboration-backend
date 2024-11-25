/**
 *  PermissionRepository
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
public interface PermissionRepository extends JpaRepository<Permission, Long> {

  // For operation on a special item
  @Query("SELECT p FROM Permission p WHERE p.itemtypeid = :itemtypeid AND (p.itemid = :itemid OR p.itemid IS NULL) AND p.roleid IN :roleids")
  public List<Permission> findByAndItemtypeidAndItemidAndRoleidIn(final long itemtypeid, final long itemid, final List<Long> roleids);

  // For create operations
  @Query("SELECT p FROM Permission p WHERE p.itemtypeid = :itemtypeid AND  p.itemid IS NULL AND p.roleid IN :roleids")
  public List<Permission> findByAndItemtypeidAndRoleidIn(final long itemtypeid, final List<Long> roleids);

//  List<Permission> findByOrgaid(final long orgaid);
}
