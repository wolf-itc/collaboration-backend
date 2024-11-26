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
  public List<Permission> findByItemtypeidAndItemidAndRoleidIn(final long itemtypeid, final long itemid, final List<Long> roleids);

  // For create operations
  @Query("SELECT p FROM Permission p WHERE p.itemtypeid = :itemtypeid AND p.itemid IS NULL AND p.roleid IN :roleids")
  public List<Permission> findByItemtypeidAndRoleidIn(final long itemtypeid, final List<Long> roleids);

  // Searches all permissions for requesting organization. Since permissions does not have orgaid itself, the roles have to  be joined
  @Query("SELECT p FROM Permission p INNER JOIN Role r ON p.roleid=r.id WHERE r.orgaid = :orgaid")
  List<Permission> findByOrgaid(final long orgaid);
}
