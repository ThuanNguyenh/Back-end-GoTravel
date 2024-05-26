package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.entity.Booking;

public interface IBookingService extends IGeneralService<BookingDTO>{
	
	public BookingDTO createBooking(Booking booking);

	public void updateBookingConfirm(UUID bookingId, String status);
	
	public void updateBookingStatus(UUID bookingId, boolean status);
	
	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String filter);
	
	public void removeAllBooking();
	
}
