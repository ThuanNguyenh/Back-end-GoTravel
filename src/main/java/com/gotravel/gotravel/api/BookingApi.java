package com.gotravel.gotravel.api;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.BookingTourDateDTO;
import com.gotravel.gotravel.dto.PaymentDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.enums.ConfirmationBooking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.repository.TourRepository;
import com.gotravel.gotravel.service.PaypalService;
import com.gotravel.gotravel.service.impl.IBookingService;
import com.gotravel.gotravel.service.impl.IPaymentBillService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import io.jsonwebtoken.io.IOException;
import jakarta.mail.internet.ParseException;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/booking")
public class BookingApi {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	public static final String SUCCESS_URL = "/paypal/success";
	public static final String CANCEL_URL = "/paypal/cancel";

//	@Autowired
	private final IBookingService bookingService;

	@Autowired
	public BookingApi(IBookingService bookingService, BookingResponsitory bookingResponsitory, PaypalService service,
			IPaymentBillService paymentService, TourRepository tourRepository) {
		this.bookingService = bookingService;
		this.bookingResponsitory = bookingResponsitory;
		this.service = service;
		this.paymentService = paymentService;
		this.tourRepository = tourRepository;
	}

	@Autowired
	private BookingResponsitory bookingResponsitory;

	@Autowired
	private PaypalService service;

	@Autowired
	PaypalService paypalService;

	private UUID bookingId;

	@Autowired
	private IPaymentBillService paymentService;

	@Autowired
	private TourRepository tourRepository;

	@Scheduled(cron = "0 */5 * * * *") // 5 phút chạy 1 lần
	public void scheduleBookingStatusUpdate() throws IOException {
		System.out.println("ok ok ok");
		// lấy danh sách các booking cần cập nhật trạng thái
		try {
			List<UUID> listUpdate = bookingService.updateBookingStatusWithSchedule();
			// duyệt qua danh sách và thực hiện việc cập nhật
			for (UUID id : listUpdate) {
				System.out.println("UPDATE BOOKING: " + id);
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ IOException nếu có
			System.err.println("An error occurred while scheduling booking status update: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllBooking() {
		List<BookingDTO> bookings = bookingService.findAll();

		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Không có dữ liệu.", HttpStatus.NOT_FOUND);
		}

	}

	// get all booking of user
	@GetMapping("/my-booking/{userId}")
	public ResponseEntity<?> getAllMyBookings(@PathVariable("userId") UUID userId) {
		List<BookingDTO> bookings = bookingService.returnAllMyBookings(userId);
		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);
		}
	}

	// get all booking of host
	@GetMapping("/{userId}&{confirmation}")
	public ResponseEntity<?> getAllBookingOfUser(@PathVariable("userId") UUID userId,
			@PathVariable("confirmation") String confirmation) {
		List<BookingDTO> bookings = bookingService.returnAllBookingOfHost(userId, confirmation);

		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);
		}
	}

	// get all booking of tour tourId and user userId
	@GetMapping("/tour-user/{tourId}&{userId}")
	public ResponseEntity<?> getAllBookingsForTourAndUser(@PathVariable("tourId") UUID tourId,
			@PathVariable("userId") UUID userId) {
		List<BookingDTO> bookings = bookingService.findAllBookingsForTourAndUser(tourId, userId);
		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Không có dữ liệu.", HttpStatus.NOT_FOUND);
		}
	}

	// get all booking of tour with checkIn
	@GetMapping("/tour/{tourId}/checkin/{checkIn}")
	public ResponseEntity<?> getBookingsByTourAndCheckIn(@PathVariable UUID tourId, @PathVariable Date checkIn) {

		List<BookingDTO> bookings = bookingService.findAllBookingsByTourAndCheckIn(tourId, checkIn);

		if (!bookings.isEmpty()) {
			return new ResponseEntity<>(bookings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Không có dữ liệu.", HttpStatus.NOT_FOUND);
		}

	}

	// LẤY RA TẤT CẢ BOOKING CỦA CÁC TOUR CỦA USER
	// lấy ra danh sách chuyển đổi nhiều booking cùng 1 tour và cùng ngày bắt đầu
	// thành 1 đối tượng mới
	@GetMapping("/booking-tour-date/{userId}/filter")
	public Page<BookingTourDateDTO> returnAllBookingOfUserWithTourIdAndCheckIn(@PathVariable UUID userId,
			@RequestParam(required = false) String checkInDate, @RequestParam(required = false) String checkOutDate,
			@RequestParam(required = false) ConfirmationBooking confirmation,
			@RequestParam(required = false) String categoryName, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) UUID categoryId,
			Pageable pageable) throws java.text.ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date checkInDateFilter = null;
		Date checkOutDateFilter = null;

		if (checkInDate != null && !checkInDate.isEmpty()) {
			checkInDateFilter = new Date(dateFormat.parse(checkInDate).getTime());
		}
		if (checkOutDate != null && !checkOutDate.isEmpty()) {
			checkOutDateFilter = new Date(dateFormat.parse(checkOutDate).getTime());
		}

		// Gọi phương thức service để lấy danh sách BookingTourDateDTO của userId
		return bookingService.returnAllBookingOfUserWithTourIdAndCheckIn(userId, checkInDateFilter, checkOutDateFilter,
				confirmation, categoryName, keyword, categoryId, pageable);

	}

	// lấy ra tất cả booking theo tourid và confirmation và đếm số
	@GetMapping("/count-tour-confirm/{userId}&{confirmation}")
	public int countTourWithConfirm(@PathVariable UUID userId, @PathVariable String confirmation) {
		return bookingService.countToursByTourIdAndConfirmation(userId, confirmation);
	}

	// create booking
	@PostMapping("/create")
	public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingParam) throws JsonProcessingException {

		Map<String, String> message = new HashMap<>();

		// check thời gian đặt với thời gian bắt đầu
		Date now = new Date(System.currentTimeMillis());
		Date checkInDate = bookingParam.getCheckInDate();

		if (checkInDate.before(now)) {
			message.put("message", "Không thể đặt tour trong thời gian này");
			return new ResponseEntity<>(new ObjectMapper().writeValueAsString(message), HttpStatus.BAD_REQUEST);
		}

		// check số lượng khách
		Optional<Tour> tourOp = tourRepository.findById(bookingParam.getTour().getTourId());

		if (tourOp.isPresent()) {
			Tour tour = tourOp.get();
			if (bookingParam.getNumGuest() > tour.getNumguest()) {
				message.put("message", "Số lượng khách đặt quá giới hạn của tour.");
				return new ResponseEntity<>(new ObjectMapper().writeValueAsString(message), HttpStatus.BAD_REQUEST);
			}
		}

		BookingDTO saveBooking = bookingService.save(bookingParam);

		if (saveBooking != null) {

			bookingId = saveBooking.getBookingId();

			// chuyển hướng đến trang Paypal để thanh toán
			String approvalUrl = payment(bookingParam);

			Map<String, String> response = new HashMap<>();
			response.put("approvalUrl", approvalUrl);

			return new ResponseEntity<>(new ObjectMapper().writeValueAsString(response), HttpStatus.OK);

		} else {
			message.put("message", "Đặt tour không thành công.");
			return new ResponseEntity<>(new ObjectMapper().writeValueAsString(message),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// payment with paypal
	public String payment(BookingDTO bookingDTO) {

		try {
			Payment payment = service.createPayment(bookingDTO.getTotal(), bookingDTO.getCurrency(),
					bookingDTO.getIntent(), bookingDTO.getMethod(), bookingDTO.getDescription(),
					"http://localhost:5173/payment" + CANCEL_URL, "http://localhost:5173/payment" + SUCCESS_URL);

			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return link.getHref();
				}
			}

		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return "";
	}

	// retrun success payment
	@GetMapping(value = SUCCESS_URL)
	@Transactional
	public ResponseEntity<List<PaymentDTO>> successPay(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId) {

		try {

			Payment payment = service.executePayment(paymentId, payerId);
			String data = payment.toJSON();

			if ("approved".equals(payment.getState())) {

				if (!paymentService.isPaymentExists(paymentId)) {

					bookingService.updateBookingStatus(bookingId, true);
					paymentService.savePayment(payment, bookingId);
					System.out.println("DATA: " + data);
				}

			}

			return ResponseEntity.ok().body(paymentService.findByPaymentId(paymentId));

		} catch (PayPalRESTException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// return when cancel payment
	@GetMapping(value = CANCEL_URL)
	public ResponseEntity<Map<String, Object>> cancelPay() {
		Map<String, Object> response = new HashMap<>();

		String message = "Thanh toán không thành công.";
		BookingDTO bookingDTO = bookingService.findById(bookingId);
		String paymentLinkTemplate = "http://localhost:5173/booking/%s?checkin=%s&checkout=%s&numGuest=%d";
		String paymentLink = String.format(paymentLinkTemplate, bookingDTO.getTour().getTourId(),
				bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate(), bookingDTO.getNumGuest());

		response.put("message", message);
		response.put("bookingDTO", bookingDTO);
		response.put("paymentLink", paymentLink);

		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	// delete all booking
	@DeleteMapping("/remove-all")
	public ResponseEntity<?> removeAllBooking() {
		bookingService.removeAllBooking();
		return ResponseEntity.ok("XÓA THÀNH CÔNG.");
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

	// update booking confirm
	@PutMapping("/update/{bookingId}&{confirm}")
	public ResponseEntity<?> updateBookingConfirm(@PathVariable("bookingId") UUID bookingId,
			@PathVariable("confirm") String confirm) {

		bookingService.updateBookingConfirm(bookingId, confirm);

		return ResponseEntity.ok("Cập nhật thành công." + confirm);
	}

	// FILTER
	@GetMapping("/filter")
	public List<BookingDTO> filterBookings(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String checkInDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String checkOutDate,
//			@RequestParam(required = false) String category,
			@RequestParam(required = false) ConfirmationBooking confirmation)
			throws ParseException, java.text.ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date checkIn = null;
		Date checkOut = null;

		if (checkInDate != null && !checkInDate.isEmpty()) {
			checkIn = new Date(dateFormat.parse(checkInDate).getTime());
		}

		if (checkOutDate != null && !checkOutDate.isEmpty()) {
			checkOut = new Date(dateFormat.parse(checkOutDate).getTime());
		}

		return bookingService.filterBookings(checkIn, checkOut, confirmation);
	}

}
