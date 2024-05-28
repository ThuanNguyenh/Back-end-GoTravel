package com.gotravel.gotravel.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.BookingConverter;
import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.BookingTourDateDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.enums.ConfirmationBooking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.service.impl.IBookingService;
import com.gotravel.gotravel.service.impl.ITourService;

import io.jsonwebtoken.io.IOException;
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
	public void removeAllBooking() {
		bookingResponsitory.deleteAll();
	}

	@Override
	public BookingDTO findById(UUID id) {
		Booking booking;
		booking = bookingResponsitory.findById(id).get();
		return bookingConverter.toDTO(booking);
	}

	@Override
	public BookingDTO save(BookingDTO bookingDTO) {

		Booking newBooking = new Booking();

		newBooking = bookingConverter.toEntity(bookingDTO);

		newBooking.setCheckIn(bookingDTO.getCheckInDate());
		newBooking.setCheckOut(bookingDTO.getCheckOutDate());
		newBooking.setNumGuest(bookingDTO.getNumGuest());
		newBooking.setTotalPrice(bookingDTO.getTotal());
		newBooking.setStatus(false);
		newBooking.setConfirmation(ConfirmationBooking.RESERVE);

		LocalDateTime today = LocalDateTime.now();
		newBooking.setCreateAt(today);

		bookingResponsitory.save(newBooking);

		return bookingConverter.toDTO(newBooking);
	}

	@Override
	public BookingDTO update(UUID bookingId, BookingDTO updateBookingDTO) {

		return null;
	}

	// update booking confirm
	@Override
	public void updateBookingConfirm(UUID bookingId, String confirm) {

		Optional<Booking> bookingOp = bookingResponsitory.findById(bookingId);

		if (!bookingOp.isPresent()) {
			throw new RuntimeException("Booking with ID " + bookingId + " not found.");
		}

		try {
			Booking booking = bookingOp.get();
			ConfirmationBooking confirmationBooking = Enum.valueOf(ConfirmationBooking.class, confirm);
			if (!booking.getConfirmation().equals(confirmationBooking)) {
				booking.setConfirmation(confirmationBooking);
				bookingResponsitory.save(booking);
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + confirm);
		}

	}

	// update booking status
	@Override
	public void updateBookingStatus(UUID bookingId, boolean status) {
		Optional<Booking> bookingOptional = bookingResponsitory.findById(bookingId);

		if (!bookingOptional.isPresent()) {
			throw new RuntimeException("Booking with ID " + bookingId + "not found.");
		}

		Booking booking = bookingOptional.get();

		if (booking.getStatus() != status) {
			booking.setStatus(status);
			booking.setConfirmation(ConfirmationBooking.valueOf(ConfirmationBooking.PENDING.name()));
			bookingResponsitory.save(booking);
		}

	}

	// return all booking of host
	@Override
	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String confirmation) {

		// chuyển chuỗi filter thành hằng số enum của lớp ConfirmationBooking
		ConfirmationBooking confirmations = Enum.valueOf(ConfirmationBooking.class, confirmation);

		// nếu như filter rỗng thì set confirmation mặc đinhj là PENDING
		if (confirmation.isEmpty()) {
			confirmations = ConfirmationBooking.PENDING;
		}

		// Lấy ra danh sách tất cả các tour của user
		List<TourDTO> tours = tourService.getAllTourOfUser(userId);

		// lấy ra các tourId của các tours
		List<UUID> tourIds = tours.stream().map(TourDTO::getTourId).toList();

		// Lọc và trả về danh sách booking
		List<BookingDTO> bookings = new ArrayList<>();
		for (UUID tourId : tourIds) {
			// lấy ra tất cả các booking theo tourId và confirmation
			List<Booking> tourBookings = bookingResponsitory
					.findAllByTourTourIdAndConfirmationOrderByCreateAtDesc(tourId, confirmations);
			// convert booking sang bookingDTO
			List<BookingDTO> tourBookingDTOs = tourBookings.stream().map(bookingConverter::toDTO).toList();
			// add bookingDTO vào ds bookings
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

	@Override
	public List<BookingDTO> returnAllMyBookings(UUID userId) {

		List<Booking> bookings = bookingResponsitory.findAllByUserId(userId);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	@Override
	public List<UUID> updateBookingStatusWithSchedule() throws IOException {

		// lấy ra danh sách tất cả các booking
		List<Booking> bookings = bookingResponsitory.findAll().stream()
				// lọc ds loại bỏ những trạng thái REJECTED
				.filter(booking -> booking.getStatus() && booking.getConfirmation() != ConfirmationBooking.REJECTED)
				.toList();

		LocalDate currentDate = LocalDate.now();
		List<UUID> updateBooking = new ArrayList<>();

		// lặp qua từng booking để kiểm tra và cập nhật trạng thái
		for (Booking booking : bookings) {
			LocalDate checkInDate = booking.getCheckIn().toLocalDate(); // chuyển đổi date thành localDate
			LocalDate checkOutDate = booking.getCheckOut().toLocalDate();

			if (booking.getConfirmation() == ConfirmationBooking.CONFIRMED && checkInDate.equals(currentDate)
					&& currentDate.isBefore(checkOutDate)) {
				// nếu ngày hiện tại bằng với ngày checkIn và trước ngày checkout
				// lấy trạng thái CONFIRMED
				// cập nhật trạng thái thành IN_PROGRESS
				booking.setConfirmation(ConfirmationBooking.IN_PROGRESS);
				updateBooking.add(booking.getBookingId());
			} else if (booking.getConfirmation() == ConfirmationBooking.IN_PROGRESS
					&& checkOutDate.equals(currentDate)) {
				// nếu ngày hiện tại sau ngày checkout
				// lấy trạng thái IN_PROGRESS
				// cập nhật trạng thái booking thành COMPLETED
				booking.setConfirmation(ConfirmationBooking.COMPLETED);
				updateBooking.add(booking.getBookingId());
			}
			bookingResponsitory.save(booking);

		}

		return updateBooking;
	}

	// lấy ra tất cả booking của tour
	@Override
	public List<BookingDTO> findAllBookingsForTourAndUser(UUID tourId, UUID userId) {

		List<Booking> bookings = bookingResponsitory.findAllByTourTourIdAndUserId(tourId, userId);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	// lấy tất cả booking của 1 tour có chung ngày bắt đầu
	@Override
	public List<BookingDTO> findAllBookingsByTourAndCheckIn(UUID tourId, Date checkIn) {

		List<Booking> bookings = bookingResponsitory.findAllByTourAndCheckIn(tourId, checkIn);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	// trả về danh sách các tour booking
	// tổng hợp tất cả các booking của 1 tour có chung ngày bắt đầu thành 1 đối
	// tượng booking mới

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public List<BookingTourDateDTO> returnAllBookingOfUserWithTourIdAndCheckIn(UUID userId) {

		// Lấy ra danh sách tất cả các tour của user
		List<TourDTO> tours = tourService.getAllTourOfUser(userId);

		// lấy ra các tourId của các tours
		List<UUID> tourIds = tours.stream().map(TourDTO::getTourId).toList();

		// danh sách map để tổng hợp các booking theo ngày bắt đầu
		Map<String, BookingTourDateDTO> bookingSummaryMap = new HashMap<>();

		for (UUID tourId : tourIds) {
			// lấy ra tất cả các booking của tour đó
			List<Booking> bookings = bookingResponsitory.findAllByTour_Id(tourId).stream()
					// lọc ds loại bỏ những trạng thái REJECTED
					.filter(booking -> booking.getStatus() && booking.getConfirmation() != ConfirmationBooking.REJECTED
							&& booking.getConfirmation() != ConfirmationBooking.RESERVE
							&& booking.getConfirmation() != ConfirmationBooking.CANCEL)
					.toList();

			for (Booking b : bookings) {

				Date checkInDate = b.getCheckIn();
				String confirmation = b.getConfirmation().name();

				// tạo key kết hợp từ tourId, checkInDate và confirmation
				String key = tourId + "_" + checkInDate + "_" + confirmation;

				// Kiểm tra xem đã có BookingTourDateDTO nào tương ứng với key của booking này
				// chưa
				BookingTourDateDTO bookingTourDateDTO = bookingSummaryMap.get(key);

				if (bookingTourDateDTO == null) {
					// nếu chưa có, tạo mới
					bookingTourDateDTO = new BookingTourDateDTO();
					// thiết lập thông tin từ tour
					bookingTourDateDTO.setTourId(b.getTour().getTourId());
					bookingTourDateDTO.setTourName(b.getTour().getTourName());
					bookingTourDateDTO.setProvince(b.getTour().getProvince());
					bookingTourDateDTO.setDistrict(b.getTour().getDistrict());
					bookingTourDateDTO.setWard(b.getTour().getWard());
					bookingTourDateDTO.setNumGuest(b.getTour().getNumguest());
					bookingTourDateDTO.setTourTime(b.getTour().getTourTime());

					// thiết lập thông tin từ booking
					bookingTourDateDTO.setBookingId(b.getBookingId());
					bookingTourDateDTO.setCheckInDate(b.getCheckIn());
					bookingTourDateDTO.setCheckOutDate(b.getCheckOut());
					bookingTourDateDTO.setConfirmation(confirmation);

					// khởi tạo tổng tiền
					bookingTourDateDTO.setTotalPriceBooked(0.0);

					 // lưu BookingTourDateDTO vào map với key là key vừa tạo
				    bookingSummaryMap.put(key, bookingTourDateDTO);
				}

				// cập nhật số lượng khách và tổng tiền
				bookingTourDateDTO.setNumGuestBooked(bookingTourDateDTO.getNumGuestBooked() + b.getNumGuest());
				bookingTourDateDTO.setTotalPriceBooked(bookingTourDateDTO.getTotalPriceBooked() + b.getTotalPrice());
			}

		}

		return new ArrayList<>(bookingSummaryMap.values());
	}

}
