package com.gotravel.gotravel.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotravel.gotravel.converter.BookingConverter;
import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.PaymentDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.service.PaypalService;
import com.gotravel.gotravel.service.impl.IBookingService;
import com.gotravel.gotravel.service.impl.IPaymentBillService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/booking")
public class BookingApi {

	public static final String SUCCESS_URL = "/paypal/success";
	public static final String CANCEL_URL = "/paypal/cancel";

	@Autowired
	private IBookingService bookingService;

	@Autowired
	private BookingResponsitory bookingResponsitory;

	@Autowired
	PaypalService service;

	@Autowired
	PaypalService paypalService;
	private UUID bookingId;

	@Autowired
	IPaymentBillService paymentService;

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
	public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingParam) throws JsonProcessingException {

		BookingDTO saveBooking = bookingService.save(bookingParam);

		if (saveBooking != null) {

			bookingId = saveBooking.getBookingId();

			// chuyển hướng đến trang Paypal để thanh toán
			String approvalUrl = payment(bookingParam);

			Map<String, String> response = new HashMap<>();
			response.put("approvalUrl", approvalUrl);

			return new ResponseEntity<>(new ObjectMapper().writeValueAsString(response), HttpStatus.OK);

		} else {
			return new ResponseEntity<>("Đặt tour không thành công", HttpStatus.INTERNAL_SERVER_ERROR);
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
	@DeleteMapping("/deleteAll")
	private ResponseEntity<?> removeAllBooking() {

		bookingService.removeAllBooking();

		return ResponseEntity.ok("xóa thành công tất cả các booking.");

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
	private ResponseEntity<?> updateBookingConfirm(@PathVariable("bookingId") UUID bookingId,
			@PathVariable String status) {

		bookingService.updateBookingConfirm(bookingId, status);

		// khi trạng thái thay đổi thì gưir thông báo hoặc email
		return new ResponseEntity<>("Cập nhật trạng thái booking thành công.", HttpStatus.OK);

	}

}
