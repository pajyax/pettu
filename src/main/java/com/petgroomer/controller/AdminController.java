package com.petgroomer.controller;

import com.petgroomer.dto.AdminDTO;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.AdminRepository;
import com.petgroomer.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    // Get all admins
    @GetMapping
    public List<AdminDTO> getAllAdmins() {
        return adminService.getAllAdmins();
    }


    // Get admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        AdminDTO adminDTO = adminService.getAdminById(id);
        if (adminDTO != null) {
            return ResponseEntity.ok(adminDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get admin by username
    @GetMapping("/username")
    public ResponseEntity<AdminDTO> getAdminByUsername(@RequestParam String username) {
        AdminDTO adminDTO = adminService.getAdminByUsername(username);
        if (adminDTO != null) {
            return ResponseEntity.ok(adminDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Register Admin
    @PostMapping("/admin-signup")
    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO) {
        if (adminRepository.existsByUsername(adminDTO.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        AdminDTO createdAdminDTO = adminService.createAdmin(adminDTO);

        return new ResponseEntity<>(createdAdminDTO, HttpStatus.CREATED);
    }

    // Login Admin
    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO loginDTO) {
        String token = adminService.adminLogin(loginDTO);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return new ResponseEntity<>("Invalid username/password", HttpStatus.UNAUTHORIZED);
    }

    // Update an admin
    @PutMapping("/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminDTO adminDetails) {
        AdminDTO updatedAdmin = adminService.updateAdmin(id, adminDetails);
        if (updatedAdmin != null) {
            return ResponseEntity.ok(updatedAdmin);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Delete an admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
