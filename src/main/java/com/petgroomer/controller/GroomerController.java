package com.petgroomer.controller;


import com.petgroomer.dto.GroomerDTO;
import com.petgroomer.model.Booking;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.GroomerRepository;
import com.petgroomer.service.GroomerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.List;

@RestController
@RequestMapping("/groomers")
public class GroomerController {

    private static final Logger logger = LoggerFactory.getLogger(GroomerController.class);

    @Autowired
    private GroomerService groomerService;

    @Autowired
    private GroomerRepository groomerRepository;

    // GET ALL GROOMERS
    @GetMapping
    public List<GroomerDTO> getAllGroomers() {
        logger.info("Fetching all groomers");
        return groomerService.getAllGroomers();
    }

    // GET GROOMER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<GroomerDTO> getGroomerById(@PathVariable Long id) {
        logger.info("Fetching groomer with ID: {}", id);
        GroomerDTO groomerDTO = groomerService.getGroomerById(id);
        return groomerDTO != null ? ResponseEntity.ok(groomerDTO) : ResponseEntity.notFound().build();
    }

    // Register Groomer
    @PostMapping("/groomer-signup")
    public ResponseEntity<?> createGroomer(@RequestBody GroomerDTO groomerDTO) {
        if (groomerRepository.existsByEmail(groomerDTO.getEmail())) {
            logger.warn("Attempted to register with existing email: {}", groomerDTO.getEmail());
            System.out.println("Checking email: " + groomerDTO.getEmail());

            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        logger.info("Registering new groomer: {}", groomerDTO.getEmail());
        GroomerDTO createdGroomerDTO = groomerService.createGroomer(groomerDTO);

        return new ResponseEntity<>(createdGroomerDTO, HttpStatus.CREATED);
    }

    // Login Groomer
    @PostMapping("/groomer-login")
    public ResponseEntity<?> groomerLogin(@RequestBody LoginDTO loginDTO) {
        String token = groomerService.groomerLogin(loginDTO);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return new ResponseEntity<>("Invalid username/password", HttpStatus.UNAUTHORIZED);
    }

    // UPDATE GROOMER
    @PutMapping("/{id}")
    public ResponseEntity<GroomerDTO> updateGroomer(@PathVariable Long id, @RequestBody GroomerDTO groomerDTO) {
        GroomerDTO updatedGroomerDTO = groomerService.updateGroomer(id, groomerDTO);
        return updatedGroomerDTO != null ? ResponseEntity.ok(updatedGroomerDTO) : ResponseEntity.notFound().build();
    }

    // DELETE GROOMER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroomer(@PathVariable Long id) {
        groomerService.deleteGroomer(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{groomerId}/bookings/{bookingId}/manage")
    public ResponseEntity<Booking> manageBooking(@PathVariable Long groomerId, @PathVariable Long bookingId, @RequestParam boolean accept) {
        Booking booking = groomerService.manageBooking(groomerId, bookingId, accept);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{groomerId}/bookings")
    public ResponseEntity<List<Booking>> getGroomerBookings(@PathVariable Long groomerId) {
        List<Booking> bookings = groomerService.getGroomerBookings(groomerId);
        return bookings != null ? ResponseEntity.ok(bookings) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{groomerId}/bookings/{bookingId}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long groomerId, @PathVariable Long bookingId, @RequestParam String status) {
        Booking booking = groomerService.updateBookingStatus(groomerId, bookingId, status);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }
}
