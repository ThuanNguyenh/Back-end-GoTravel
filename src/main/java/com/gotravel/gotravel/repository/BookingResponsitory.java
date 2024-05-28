package com.gotravel.gotravel.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;

@Repository
public interface BookingResponsitory extends JpaRepository<Booking, UUID> {
	List<Booking> findAllByTourTourIdAndConfirmationOrderByCreateAtDesc(UUID tourId, ConfirmationBooking confirmation);

	List<Booking> findAllByUserId(UUID userId);

	List<Booking> findAllByTourTourIdAndUserId(UUID tourId, UUID userId);

	@Query("SELECT b from Booking b WHERE b.tour.tourId = :tourId AND b.checkIn = :checkIn")
	List<Booking> findAllByTourAndCheckIn(UUID tourId, Date checkIn);

	@Query("SELECT b from Booking b WHERE b.tour.tourId = :tourId")
	List<Booking> findAllByTour_Id(UUID tourId);

}
