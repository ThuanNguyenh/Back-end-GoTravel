package com.gotravel.gotravel.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;
import com.gotravel.gotravel.repository.TourRepository;
import com.gotravel.gotravel.repository.UserRepository;

@Component
public class BookingConverter {

	@Autowired
	private UserConverter userConverter;

	@Autowired
	private TourConverter tourConverter;
	
	@Autowired
	private TourRepository tourRepository;
	
	@Autowired
	private UserRepository userRepository;

	public BookingDTO toDTO(Booking booking) {

		BookingDTO bookingDTO = new BookingDTO();

		bookingDTO.setBookingId(booking.getBookingId());
		bookingDTO.setNumGuest(booking.getNumGuest());
		bookingDTO.setTotalPrice(booking.getTotalPrice());
		bookingDTO.setUser(userConverter.toDTO(booking.getUser()));
		bookingDTO.setTour(tourConverter.toDTO(booking.getTour()));
		
		if (booking.getStatus() != null) {
			bookingDTO.setStatus(booking.getStatus().name());
		} else {
			bookingDTO.setStatus(null);
		}
		
		
		bookingDTO.setCheckInDate(booking.getCheckIn());
		bookingDTO.setCheckOutDate(booking.getCheckOut());

		return bookingDTO;

	}

	public Booking toEntity(BookingDTO bookingDTO) {

		Booking booking = new Booking();

		booking.setBookingId(bookingDTO.getBookingId());
		booking.setTotalPrice(bookingDTO.getTotalPrice());
		booking.setCheckIn(bookingDTO.getCheckInDate());
		booking.setCheckOut(bookingDTO.getCheckOutDate());
		
		booking.setUser(userRepository.findById(bookingDTO.getUser().getUserId()).get());

		booking.setTour(tourRepository.findById(bookingDTO.getTour().getTourId()).get());
		
		return booking;

	}

}
