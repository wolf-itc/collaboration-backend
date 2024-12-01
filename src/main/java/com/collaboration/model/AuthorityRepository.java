/**
 *  AuthorityRepository
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    @Transactional
    @Modifying
    @Query(nativeQuery=true, value="DELETE FROM Authorities WHERE username = :userName")
    void deleteAllByUserName(@Param("userName") String userName);

    List<Authority> findByUserId(long userId);
}
