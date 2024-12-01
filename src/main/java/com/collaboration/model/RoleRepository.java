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
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  List<Role> findByOrgaId(final long orgaid);
  
  List<Role> findByOrgaIdAndRolenameIn(final long orgaId, final List<String> rolenames);
  
  List<Role> findByOrgaIdAndIdIn(final long orgaId, final List<Long> ids);
  
  Optional<Role> findRoleByRolenameAndOrgaId(final String rolename, long orgaId);
}
