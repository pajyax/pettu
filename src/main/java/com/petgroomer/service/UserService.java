package com.petgroomer.service;


import com.petgroomer.exceptions.InvalidCredentialsException;
import com.petgroomer.exceptions.ResourceAlreadyExistsException;
import com.petgroomer.model.AppUser;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private AppUserRepository appuserRepository;
    private JWTService jwtService;

    @Autowired
    public UserService(AppUserRepository appuserRepository, JWTService jwtService) {
        this.appuserRepository = appuserRepository;
        this.jwtService = jwtService;
    }

    public AppUser createUser(AppUser user) {
        if (appuserRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        return appuserRepository.save(user);
    }

    public String verifyLogin(LoginDTO loginDTO) {
        AppUser appUser = appuserRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!BCrypt.checkpw(loginDTO.getPassword(), appUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtService.generateToken(appUser);
    }
}

