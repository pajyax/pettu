package com.petgroomer.controller;


import com.petgroomer.dto.BookingDTO;
import com.petgroomer.model.Groomer;
import com.petgroomer.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //GET ALL BOOKINGS (GET : "/bookings")
    @GetMapping
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // CREATE BOOKING (POST : "/bookings")
    @PostMapping
    public BookingDTO createBooking(@RequestBody BookingDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }

    // View a booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        Optional<BookingDTO> bookingDTO = Optional.ofNullable(bookingService.getBookingById(id));
        return bookingDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // View and manage customer bookings
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingDTO>> getCustomerBookings(@PathVariable Long customerId) {
        List<BookingDTO> bookings = bookingService.getCustomerBookings(customerId);
        return ResponseEntity.ok(bookings);
    }

    // View groomer profile by booking ID
    @GetMapping("/{bookingId}/groomer")
    public ResponseEntity<Groomer> getGroomerByBooking(@PathVariable Long bookingId) {
        Optional<Groomer> groomer = Optional.ofNullable(bookingService.getGroomerByBooking(bookingId));
        return groomer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update booking
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        BookingDTO updatedBookingDTO = bookingService.updateBooking(id, bookingDTO);
        return updatedBookingDTO != null ? ResponseEntity.ok(updatedBookingDTO) : ResponseEntity.notFound().build();
    }

    // Reschedule booking
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<BookingDTO> rescheduleBooking(@PathVariable Long id, @RequestBody LocalDateTime newDateTime) {
        try {
            BookingDTO updatedBookingDTO = bookingService.rescheduleBooking(id, newDateTime);
            return ResponseEntity.ok(updatedBookingDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cancel booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}

