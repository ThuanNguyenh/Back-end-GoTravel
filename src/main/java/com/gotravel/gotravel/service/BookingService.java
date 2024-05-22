package com.gotravel.gotravel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.BookingConverter;
import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.service.impl.IBookingService;
import com.gotravel.gotravel.service.impl.ITourService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingService implements IBookingService {

	@Autowired
	private BookingResponsitory bookingResponsitory;

	@Autowired
	private BookingConverter bookingConverter;
	
	@Autowired
	private ITourService tourService;

	@Override
	public List<BookingDTO> findAll() {

		List<Booking> bookings = bookingResponsitory.findAll();

		List<BookingDTO> bookingDTOs = new ArrayList<>();

		for (Booking b : bookings) {
			BookingDTO bookingDTO = bookingConverter.toDTO(b);
			bookingDTOs.add(bookingDTO);
		}

		return bookingDTOs;
	}

	@Override
	public BookingDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BookingDTO save(BookingDTO bookingDTO) {

		Booking booking = new Booking();
		booking = bookingConverter.toEntity(bookingDTO);

		booking.setCheckIn(bookingDTO.getCheckInDate());
		booking.setCheckOut(bookingDTO.getCheckOutDate());
		booking.setNumGuest(bookingDTO.getNumGuest());
		booking.setTotalPrice(bookingDTO.getTotalPrice());

		booking.setStatus(ConfirmationBooking.PENDING);

		LocalDateTime today = LocalDateTime.now();
		booking.setCreateAt(today);

		bookingResponsitory.save(booking);

		return bookingConverter.toDTO(booking);
	}

	@Override
	public BookingDTO update(UUID bookingId, BookingDTO updateBookingDTO) {

		return null;
	}

	// update booking status
	@Override
	public void updateBookingStatus(UUID bookingId, String status) {
		Optional<Booking> bookingOp = bookingResponsitory.findById(bookingId);

		if (bookingOp.isEmpty()) {
			throw new RuntimeException("Booking with ID " + bookingId + "not found.");
		}
		
		try {
			Booking booking = bookingOp.get();
			// chuyển đổi chuỗi status thành 1 hằng số của enum của lớp ConfirmationBooking
			ConfirmationBooking confirmation = Enum.valueOf(ConfirmationBooking.class, status);
			// kiểm tra nếu như status hiện tại không giống với confirmation thì set lại trạng thái booking
			if (!booking.getStatus().equals(confirmation)) {
				booking.setStatus(confirmation);
				bookingResponsitory.save(booking);
			}
		} catch (IllegalArgumentException e) {
			// Xử lý giá trị trạng thái không hợp lệ (ví dụ, ném ra một ngoại lệ, ghi log lỗi, v.v.)
		    throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + status);
		}

	}
	
	// return all booking of host
	@Override
	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String filter) {
		
		// chuyển chuỗi filter thành hằng số enum của lớp ConfirmationBooking
		ConfirmationBooking confirmation = Enum.valueOf(ConfirmationBooking.class, filter);
		
		if (filter.isEmpty()) {
			confirmation = ConfirmationBooking.PENDING;
		}
		
		// Lấy ra danh sách tất cả các tour của user
		List<TourDTO> tours = tourService.getAllTourOfUser(userId);
		
		// lấy ra các tourId của các tours
		List<UUID> tourIds = tours.stream().map(TourDTO::getTourId).toList();
		
		// Lọc và trả về danh sách booking
		List<BookingDTO> bookings = new ArrayList<>();
		for (UUID tourId : tourIds) {
			List<Booking> tourBookings = bookingResponsitory.findAllByTourTourIdAndStatusOrderByCreateAtDesc(tourId, confirmation);
			List<BookingDTO> tourBookingDTOs = tourBookings.stream().map(bookingConverter::toDTO).toList();
			bookings.addAll(tourBookingDTOs);
		}
		
		return bookings;
	}

	@Override
	public void remove(UUID id) {

		if (bookingResponsitory.existsById(id)) {
			bookingResponsitory.deleteById(id);
		} else {
			throw new EntityNotFoundException("Không tìm thấy thông tin đặt tour với id: " + id);
		}

	}

}
