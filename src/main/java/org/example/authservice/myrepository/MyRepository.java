package org.example.authservice.myrepository;

import org.example.authservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRepository extends JpaRepository<Candidate, Integer> {

    public Candidate findByEmail(String email);

}
