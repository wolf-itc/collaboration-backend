/**
 *  NonUserRepository
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
public interface NonUserRepository extends JpaRepository<NonUser, Long> {

    List<NonUser> findByName(String name); // Find NonUsers by name

    List<NonUser> findByCreatedById(int createdById); // Find NonUsers by creator ID
}
