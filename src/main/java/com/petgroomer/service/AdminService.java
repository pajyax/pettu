package com.petgroomer.service;

import com.petgroomer.dto.AdminDTO;
import com.petgroomer.exceptions.InvalidCredentialsException;
import com.petgroomer.exceptions.ResourceAlreadyExistsException;
import com.petgroomer.exceptions.ResourceNotFoundException;
import com.petgroomer.model.Admin;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private AdminRepository adminRepository;
    private JWTService jwtService;

    @Autowired
    public AdminService(AdminRepository adminRepository, JWTService jwtService) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
    }

    public List<AdminDTO> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AdminDTO getAdminById(Long id) {
        return adminRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
    }

    public AdminDTO getAdminByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with username " + username));
        return convertToDTO(admin);
    }

    public AdminDTO createAdmin(AdminDTO adminDTO) {
        Optional<Admin> existingAdmin = adminRepository.findByUsername(adminDTO.getUsername());
        if (existingAdmin.isPresent()) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        Admin admin =   convertToEntity(adminDTO);
        admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt(10)));
        Admin savedAdmin = adminRepository.save(admin);

        AdminDTO responseDTO = convertToDTO(savedAdmin);

        return responseDTO;
    }

    public String adminLogin(LoginDTO loginDTO) {
        Admin admin = adminRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username"));

        if (!BCrypt.checkpw(loginDTO.getPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return jwtService.generateAdminToken(admin);
    }


    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
        admin.setUsername(adminDTO.getUsername());
        admin.setRole(adminDTO.getRole());
        // Assuming password is not updated via DTO
        Admin updatedAdmin = adminRepository.save(admin);
        return convertToDTO(updatedAdmin);
    }

    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found with id " + id);
        }
        adminRepository.deleteById(id);
    }

    private AdminDTO convertToDTO(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setRole(admin.getRole());
        return adminDTO;
    }

    private Admin convertToEntity(AdminDTO adminDTO) {
        Admin admin = new Admin();
        admin.setId(adminDTO.getId());
        admin.setUsername(adminDTO.getUsername());
        admin.setRole(adminDTO.getRole());
        // Password should be handled carefully, this is just a placeholder
        admin.setPassword("defaultPassword");
        return admin;
    }

}
