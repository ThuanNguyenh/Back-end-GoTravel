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
		bookingDTO.setTotal(booking.getTotalPrice());

		if (booking.getUser() != null) {
			bookingDTO.setUserId(booking.getUser().getId());
			bookingDTO.setUser(userConverter.toDTO(booking.getUser()));
		} else {
			bookingDTO.setUserId(null);
			bookingDTO.setUser(null);
		}

		if (booking.getTour() != null) {
			bookingDTO.setTourId(booking.getTour().getTourId());
			bookingDTO.setTour(tourConverter.toDTO(booking.getTour()));

		} else {
			bookingDTO.setTourId(null);
			bookingDTO.setTour(null);
		}

		bookingDTO.setStatus(booking.getStatus());

		if (booking.getConfirmation() != null) {
			bookingDTO.setConfirmation(booking.getConfirmation().name());
		} else {
			bookingDTO.setConfirmation(null);
		}

		bookingDTO.setCheckInDate(booking.getCheckIn());
		bookingDTO.setCheckOutDate(booking.getCheckOut());

		return bookingDTO;

	}

	public Booking toEntity(BookingDTO bookingDTO) {

		Booking booking = new Booking();

		booking.setBookingId(bookingDTO.getBookingId());
		booking.setTotalPrice(bookingDTO.getTotal());
		booking.setCheckIn(bookingDTO.getCheckInDate());
		booking.setCheckOut(bookingDTO.getCheckOutDate());
		booking.setStatus(bookingDTO.getStatus());
//		booking.setConfirmation(ConfirmationBooking.valueOf(bookingDTO.getConfirmation()));

		if (bookingDTO.getUser() != null) {
			booking.setUser(userRepository.findById(bookingDTO.getUser().getUserId()).get());

		} else {
			booking.setUser(null);
		}

		if (bookingDTO.getTour() != null) {
			booking.setTour(tourRepository.findById(bookingDTO.getTour().getTourId()).get());

		} else {
			booking.setTour(null);
		}

		return booking;

	}

}
