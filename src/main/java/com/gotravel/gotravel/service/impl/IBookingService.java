package com.gotravel.gotravel.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.BookingTourDateDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;

import io.jsonwebtoken.io.IOException;

public interface IBookingService extends IGeneralService<BookingDTO> {

	public void updateBookingConfirm(UUID bookingId, String confirm);

	public void updateBookingStatus(UUID bookingId, boolean status);

	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String confirmation);

	public List<BookingDTO> returnAllMyBookings(UUID userId);

	public void removeAllBooking();

	public List<UUID> updateBookingStatusWithSchedule() throws IOException; // tự động set lại sau mỗi 5 phút

	public List<BookingDTO> findAllBookingsForTourAndUser(UUID tourId, UUID userId);

	public List<BookingDTO> findAllBookingsByTourAndCheckIn(UUID tourId, Date checkIn);

	public Page<BookingDTO> returnAllMyBookingsFilter(UUID userId, ConfirmationBooking confirmation, Pageable pageable);

	public Page<BookingTourDateDTO> returnAllBookingOfUserWithTourIdAndCheckIn(UUID userId, Date checkInDateFilter,
			Date checkOutDateFilter, ConfirmationBooking confirmation, String categoryName, String keyword,
			UUID categoryId, Pageable pageable);

	public int countToursByTourIdAndConfirmation(UUID userId, String confirmation);
//	public void updateBookingCancel(UUID bookingId, boolean cancelBooking, boolean status);

	public List<BookingDTO> filterBookings(Date checkInDate, Date checkOutDate, ConfirmationBooking confirmation);

}
