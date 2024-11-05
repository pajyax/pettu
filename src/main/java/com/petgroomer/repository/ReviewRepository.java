package com.petgroomer.repository;

import com.petgroomer.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGroomerId(Long groomerId);
    List<Review> findByBookingId(Long bookingId);
    List<Review> findByCustomerId(Long customerId);
}

