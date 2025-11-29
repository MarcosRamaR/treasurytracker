package com.mvm.auth.repository;

import com.mvm.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JpaRepository have many methods from interact with DB
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
