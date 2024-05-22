package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.BookingDTO;

public interface IBookingService extends IGeneralService<BookingDTO>{

	public void updateBookingStatus(UUID bookingId, String status);
	
	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String filter);
	
}
