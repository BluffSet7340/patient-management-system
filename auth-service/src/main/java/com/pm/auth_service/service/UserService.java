package com.pm.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pm.auth_service.model.User;
import com.pm.auth_service.repository.UserRepository;

@Service
public class UserService {
         
    // dependency injection - an object uses a constructor where the arguments of that constructor are other objects that are needed for it to work together
    private final UserRepository userRepository; // UserService has dependency on UserRepository 

    // constructor
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository; 
    }

    // .... business logic that uses the injected userRepository

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
