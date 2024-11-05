package com.petgroomer.repository;

import com.petgroomer.model.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroomerRepository extends JpaRepository<Groomer, Long> {
    Optional<Groomer> findByEmail(String email);

    boolean existsByEmail(String email);
    // Add custom query methods if needed
}
