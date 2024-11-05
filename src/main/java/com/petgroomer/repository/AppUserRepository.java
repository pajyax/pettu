package com.petgroomer.repository;

import com.petgroomer.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<AppUser> findByUsername(String username);

}
