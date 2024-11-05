package com.petgroomer.service;


import com.petgroomer.dto.GroomerDTO;
import com.petgroomer.exceptions.BookingNotFoundException;
import com.petgroomer.exceptions.InvalidCredentialsException;
import com.petgroomer.exceptions.ResourceAlreadyExistsException;
import com.petgroomer.exceptions.ResourceNotFoundException;
import com.petgroomer.model.Booking;
import com.petgroomer.model.Groomer;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.GroomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroomerService {

    private GroomerRepository groomerRepository;
    private JWTService jwtService;

    @Autowired
    public GroomerService(GroomerRepository groomerRepository, JWTService jwtService) {
        this.groomerRepository = groomerRepository;
        this.jwtService = jwtService;
    }

    public List<GroomerDTO> getAllGroomers() {
        return groomerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GroomerDTO getGroomerById(Long id) {
        return groomerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with id " + id));
    }

//    public GroomerDTO createGroomer(GroomerDTO groomerDTO) {
//        Groomer groomer = convertToEntity(groomerDTO);
//        Groomer savedGroomer = groomerRepository.save(groomer);
//        return convertToDTO(savedGroomer);
//    }

    public GroomerDTO createGroomer(GroomerDTO groomerDTO) {
        Optional<Groomer> existingGroomer = groomerRepository.findByEmail(groomerDTO.getEmail());
        if (existingGroomer.isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        Groomer groomer =   convertToEntity(groomerDTO);
        groomer.setPassword(BCrypt.hashpw(groomer.getPassword(), BCrypt.gensalt(10)));
        Groomer savedGroomer = groomerRepository.save(groomer);

        GroomerDTO responseDTO = convertToDTO(savedGroomer);

        return responseDTO;
    }

    public String groomerLogin(LoginDTO loginDTO) {
        Groomer groomer = groomerRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email"));

        if (!BCrypt.checkpw(loginDTO.getPassword(), groomer.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        return jwtService.generateGroomerToken(groomer);
    }

    public GroomerDTO getGroomerByEmail(String email) {
        Groomer groomer = groomerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with email " + email));
        return convertToDTO(groomer);
    }

    public GroomerDTO updateGroomer(Long id, GroomerDTO groomerDTO) {
        Groomer groomer = groomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with id " + id));
        groomer.setName(groomerDTO.getName());
        groomer.setEmail(groomerDTO.getEmail());
        groomer.setPhone(groomerDTO.getPhone());
        groomer.setAddress(groomerDTO.getAddress());
        groomer.setPassword(groomerDTO.getPassword());
        groomer.setLicenseNumber(groomerDTO.getLicenseNumber());
        groomer.setInsuranceDetails(groomerDTO.getInsuranceDetails());
        groomer.setServiceTypes(groomerDTO.getServiceTypes());
        groomer.setPrice(groomerDTO.getPrice());
        groomer.setAvailability(groomerDTO.getAvailability());

        groomer.setProfilePicture(groomerDTO.getProfilePicture());
        Groomer updatedGroomer = groomerRepository.save(groomer);
        return convertToDTO(updatedGroomer);
    }

    public void deleteGroomer(Long id) {
        if (!groomerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Groomer not found with id " + id);
        }
        groomerRepository.deleteById(id);
    }

    // Accept or reject bookings based on availability
    public Booking manageBooking(Long groomerId, Long bookingId, boolean accept) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with id " + groomerId));
        Booking booking = groomer.getBookings().stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.setStatus(accept ? "Accepted" : "Rejected");
        return booking;
    }

    // View and manage their own bookings
    public List<Booking> getGroomerBookings(Long groomerId) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with id " + groomerId));
        return groomer.getBookings();
    }

    // Update booking status after service completion
    public Booking updateBookingStatus(Long groomerId, Long bookingId, String status) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Groomer not found with id " + groomerId));
        Booking booking = groomer.getBookings().stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.setStatus(status);
        return booking;
    }

    private GroomerDTO convertToDTO(Groomer groomer) {
        GroomerDTO dto = new GroomerDTO();
        dto.setId(groomer.getId());
        dto.setName(groomer.getName());
        dto.setEmail(groomer.getEmail());
        dto.setPhone(groomer.getPhone());
        dto.setAddress(groomer.getAddress());
        dto.setPassword(groomer.getPassword());
        dto.setLicenseNumber(groomer.getLicenseNumber());
        dto.setInsuranceDetails(groomer.getInsuranceDetails());
        dto.setServiceTypes(groomer.getServiceTypes());
        dto.setPrice(groomer.getPrice());
        dto.setAvailability(groomer.getAvailability());
        dto.setProfilePicture(groomer.getProfilePicture());
        dto.setRole(groomer.getRole());
//        dto.setBookingIds(groomer.getBookings().stream().map(Booking::getId).collect(Collectors.toList()));
//        dto.setReviewIds(groomer.getReviews().stream().map(review -> review.getId()).collect(Collectors.toList()));
        dto.setBookingIds(groomer.getBookings() != null
                ? groomer.getBookings().stream()
                .map(Booking::getId)
                .collect(Collectors.toList())
                : Collections.emptyList());

        return dto;
    }

    private Groomer convertToEntity(GroomerDTO dto) {
        Groomer groomer = new Groomer();
        groomer.setId(dto.getId());
        groomer.setName(dto.getName());
        groomer.setEmail(dto.getEmail());
        groomer.setPhone(dto.getPhone());
        groomer.setAddress(dto.getAddress());
        groomer.setPassword(dto.getPassword());
        groomer.setLicenseNumber(dto.getLicenseNumber());
        groomer.setInsuranceDetails(dto.getInsuranceDetails());
        groomer.setServiceTypes(dto.getServiceTypes());
        groomer.setPrice(dto.getPrice());
        groomer.setAvailability(dto.getAvailability());
        groomer.setProfilePicture(dto.getProfilePicture());
        groomer.setRole(dto.getRole());
        return groomer;
    }
}
