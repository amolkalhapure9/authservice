package org.example.authservice.myrepository;

import jakarta.transaction.Transactional;
import org.example.authservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRepository extends JpaRepository<Candidate, Integer> {

    public Candidate findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE Candidate u SET u.verification = true WHERE u.email = :email")
    int verifyUserByEmail(@Param("email") String email);

}
