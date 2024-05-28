package com.gotravel.gotravel.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.UUID;


import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.BookingTourDateDTO;
import com.gotravel.gotravel.entity.Booking;

import io.jsonwebtoken.io.IOException;

public interface IBookingService extends IGeneralService<BookingDTO> {

	public void updateBookingConfirm(UUID bookingId, String confirm);

	public void updateBookingStatus(UUID bookingId, boolean status);

	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String confirmation);
	
	public List<BookingDTO> returnAllMyBookings(UUID userId);

	public void removeAllBooking();
	
	public List<UUID> updateBookingStatusWithSchedule() throws IOException;
	
	public List<BookingDTO> findAllBookingsForTourAndUser(UUID tourId, UUID userId);
	
	public List<BookingDTO> findAllBookingsByTourAndCheckIn(UUID tourId, Date checkIn);
	
	public List<BookingTourDateDTO> returnAllBookingOfUserWithTourIdAndCheckIn(UUID userId);
	
//	public void updateBookingCancel(UUID bookingId, boolean cancelBooking, boolean status);

}
