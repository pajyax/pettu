package com.petgroomer.controller;


import com.petgroomer.model.AppUser;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.AppUserRepository;
import com.petgroomer.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private AppUserRepository appUserRepository;
    private UserService userService;

    public UserController(AppUserRepository appUserRepository, UserService userService) {
        this.appUserRepository = appUserRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createdUser(
            @RequestBody AppUser user
    ){
        if (appUserRepository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        if (appUserRepository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        System.out.println(user.getEmail());

        AppUser addedUser = userService.createUser(user);
        return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> getLogin(@RequestBody LoginDTO loginDTO) {
        String token = userService.verifyLogin(loginDTO);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return new ResponseEntity<>("Invalid username/password", HttpStatus.UNAUTHORIZED);
    }
}
