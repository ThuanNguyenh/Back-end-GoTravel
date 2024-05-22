package com.gotravel.gotravel.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;

@Repository
public interface BookingResponsitory extends JpaRepository<Booking, UUID>{
	List<Booking> findAllByTourTourIdAndStatusOrderByCreateAtDesc(UUID tourId, ConfirmationBooking status);
}
