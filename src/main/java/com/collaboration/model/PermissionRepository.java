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
  @Query("SELECT p FROM Permission p WHERE p.itemtypeId = :itemtypeId AND (p.itemId = :itemId OR p.itemId IS NULL) AND p.roleId IN :roleIds")
  public List<Permission> findByItemtypeIdAndItemIdAndRoleIdIn(final long itemtypeId, final long itemId, final List<Long> roleIds);

  // For create operations
  @Query("SELECT p FROM Permission p WHERE p.itemtypeId = :itemtypeId AND p.itemId IS NULL AND p.roleId IN :roleIds")
  public List<Permission> findByItemtypeIdAndRoleIdIn(final long itemtypeId, final List<Long> roleIds);

  // Searches all permissions for requesting organization. Since permissions does not have orgaId itself, the roles have to  be joined
  @Query("SELECT p FROM Permission p INNER JOIN Role r ON p.roleId=r.id WHERE r.orgaId = :orgaId")
  List<Permission> findByOrgaId(final long orgaId);
}
