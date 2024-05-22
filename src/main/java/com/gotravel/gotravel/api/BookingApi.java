package com.gotravel.gotravel.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.service.impl.IBookingService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping(value = "/api/v1/booking", produces = "application/json")
public class BookingApi {

	@Autowired
	private IBookingService bookingService;

	@Autowired
	private BookingResponsitory bookingResponsitory;

	@GetMapping("/all")
	public ResponseEntity<?> getAllBooking() {
		List<BookingDTO> bookings = bookingService.findAll();

		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Không có dữ liệu.", HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/{userId}&{filter}")
	private ResponseEntity<?> getAllBookingOfUser(@PathVariable("userId") UUID userId,
			@PathVariable("filter") String Filter) {

		List<BookingDTO> bookings = bookingService.returnAllBookingOfHost(userId, Filter);

		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);
		}

	}

	// create booking
	@PostMapping("/create")
	public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {

		BookingDTO saveBooking = bookingService.save(bookingDTO);

		if (saveBooking != null) {
			return new ResponseEntity<>(saveBooking, HttpStatus.OK);

		} else {
			return new ResponseEntity<>("Đặt tour không thành công", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// delete booking tour
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteBookingTour(@PathVariable("id") UUID id) {
		Optional<Booking> bookingOp = bookingResponsitory.findById(id);
		return bookingOp.map(booking -> {
			bookingService.remove(id);
			return new ResponseEntity<>("Xóa thành công!", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Không tìm thấy thông tin booking!", HttpStatus.NOT_FOUND));
	}

	// update booking status
	@PutMapping("/update/{bookingId}&{status}")
	private ResponseEntity<?> updateBookingStatus(@PathVariable("bookingId") UUID bookingId,
			@PathVariable String status) {

		bookingService.updateBookingStatus(bookingId, status);

		// khi trạng thái thay đổi thì gưir thông báo hoặc email
		return new ResponseEntity<>("Cập nhật trạng thái booking thành công.", HttpStatus.OK);

	}

}
