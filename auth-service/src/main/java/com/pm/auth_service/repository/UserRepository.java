package com.pm.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.auth_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // UUID is unique identifier for User model
    Optional<User> findByEmail(String email); // exposing the method - calls user repo to find user by email address
}
