package com.gotravel.gotravel.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.PaymentDTO;
import com.gotravel.gotravel.entity.PaymentBill;
import com.gotravel.gotravel.repository.BookingResponsitory;

@Component
public class PaymentConverter {

	@Autowired
	private BookingConverter bookingConverter;

	@Autowired
	private BookingResponsitory bookingResponsitory;

	public PaymentDTO toDTO(PaymentBill paymentBill) {
		PaymentDTO paymentDTO = new PaymentDTO();

		paymentDTO.setPaymentBillId(paymentBill.getPaymentBillId());
		paymentDTO.setPaymentId(paymentBill.getPaymentId());
		paymentDTO.setAmount(paymentBill.getAmount());
		paymentDTO.setAccountType(paymentBill.getAccountType());
		paymentDTO.setMethod(paymentBill.getMethod());
		paymentDTO.setCapturesId(paymentBill.getCaptures());
		paymentDTO.setRefundStatus(paymentBill.getRefundStatus());
		paymentDTO.setCreateTime(paymentBill.getCreateTime());
		paymentDTO.setStatus(paymentBill.getStatus());

		paymentDTO.setBookingDTO(
				bookingConverter.toDTO(bookingResponsitory.findById(paymentBill.getBooking().getBookingId()).get()));

		return paymentDTO;
	}
	
	public PaymentBill toEntity(PaymentDTO paymentDTO) {
		PaymentBill paymentBill = new PaymentBill();
		paymentBill.setPaymentBillId(paymentDTO.getPaymentBillId());
		paymentBill.setAmount(paymentDTO.getAmount());
		paymentBill.setMethod(paymentDTO.getMethod());
		paymentBill.setAccountType(paymentDTO.getAccountType());
		paymentBill.setCreateTime(paymentDTO.getCreateTime());
		paymentBill.setPaymentId(paymentDTO.getPaymentId());
		paymentBill.setStatus(paymentDTO.getStatus());
		paymentBill.setCaptures(paymentDTO.getCapturesId());
		paymentBill.setRefundStatus(paymentDTO.getRefundStatus());
		paymentBill.setBooking(bookingResponsitory.findById(paymentDTO.getBookingId()).get());
		return paymentBill;
	}

}
