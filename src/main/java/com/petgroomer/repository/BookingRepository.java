package com.petgroomer.repository;

import com.petgroomer.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //    List<Booking> findByOwnerName(String ownerName);
//        List<Booking> findByCustomerId(Long ownerId);
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByGroomerId(Long groomerId);

}
