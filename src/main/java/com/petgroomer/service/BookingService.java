package com.petgroomer.service;


import com.petgroomer.dto.BookingDTO;
import com.petgroomer.exceptions.BookingCancellationException;
import com.petgroomer.exceptions.BookingNotFoundException;
import com.petgroomer.exceptions.InvalidBookingException;
import com.petgroomer.model.Booking;
import com.petgroomer.model.Customer;
import com.petgroomer.model.Groomer;
import com.petgroomer.repository.BookingRepository;
import com.petgroomer.repository.CustomerRepository;
import com.petgroomer.repository.GroomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GroomerRepository groomerRepository;

    @Autowired
    private CustomerRepository customerRepository;


    // Get all bookings
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Book a service
    public BookingDTO createBooking(BookingDTO bookingDTO) {
//        if (bookingRepository.existsById(bookingDTO.getId())) {
//            throw new BookingAlreadyExistsException("Booking already exists with id: " + bookingDTO.getId());
//        }

        Booking booking = convertToEntity(bookingDTO);

        if (bookingDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
                    .orElseThrow(() -> new InvalidBookingException("Customer not found with id: " + bookingDTO.getCustomerId()));
            booking.setCustomer(customer);
        } else {
            throw new InvalidBookingException("Customer ID is required.");
        }

        if (bookingDTO.getGroomerId() != null) {
            Groomer groomer = groomerRepository.findById(bookingDTO.getGroomerId())
                    .orElseThrow(() -> new InvalidBookingException("Groomer not found with id: " + bookingDTO.getGroomerId()));
            booking.setGroomer(groomer);
        } else {
            throw new InvalidBookingException("Groomer ID is required.");
        }

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }


    // View a booking by ID
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return convertToDTO(booking);
    }

    // View and manage their own bookings (for customers)
    public List<BookingDTO> getCustomerBookings(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new InvalidBookingException("Customer not found with id: " + customerId);
        }
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // View groomer profiles and reviews (for customers)
    public Groomer getGroomerByBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        return booking.getGroomer();
    }

    // Update booking
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.setPetName(bookingDTO.getPetName());
        booking.setOwnerName(bookingDTO.getOwnerName());
        booking.setOwnerEmail(bookingDTO.getOwnerEmail());
        booking.setService(bookingDTO.getService());
        booking.setDateTime(bookingDTO.getDateTime());
        booking.setStatus(bookingDTO.getStatus());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    // Cancel booking
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException(id);
        }
        try {
            bookingRepository.deleteById(id);
        } catch (Exception e) {
            throw new BookingCancellationException(id, "An error occurred while trying to cancel the booking.");
        }
    }

    // Reschedule booking
    public BookingDTO rescheduleBooking(Long id, LocalDateTime newDateTime) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.setDateTime(newDateTime);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    // Convert Booking to BookingDTO
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setPetName(booking.getPetName());
        dto.setOwnerName(booking.getOwnerName());
        dto.setOwnerEmail(booking.getOwnerEmail());
        dto.setService(booking.getService());
        dto.setDateTime(booking.getDateTime());
        dto.setStatus(booking.getStatus());
        // Ensure customer is not null before accessing its ID
        if (booking.getCustomer() != null) {
            dto.setCustomerId(booking.getCustomer().getId());
        }

        // Ensure groomer is not null before accessing its ID
        if (booking.getGroomer() != null) {
            dto.setGroomerId(booking.getGroomer().getId());
        }
        return dto;
    }

    // Convert BookingDTO to Booking
    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setPetName(dto.getPetName());
        booking.setOwnerName(dto.getOwnerName());
        booking.setOwnerEmail(dto.getOwnerEmail());
        booking.setService(dto.getService());
        booking.setDateTime(dto.getDateTime());
        booking.setStatus(dto.getStatus());

        // Set Customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new InvalidBookingException("Customer not found with id: " + dto.getCustomerId()));
        booking.setCustomer(customer);

        // Set Groomer
        Groomer groomer = groomerRepository.findById(dto.getGroomerId())
                .orElseThrow(() -> new InvalidBookingException("Groomer not found with id: " + dto.getGroomerId()));
        booking.setGroomer(groomer);

        return booking;
    }
}
