/**
 *  RoleRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  List<Role> findByOrgaid(final long orgaid);
  
  List<Role> findByOrgaidAndRolenameIn(final long orgaid, final List<String> rolenames);
  
  List<Role> findByOrgaidAndIdIn(final long orgaid, final List<Long> ids);
}
