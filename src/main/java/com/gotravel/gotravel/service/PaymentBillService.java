package com.gotravel.gotravel.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.PaymentConverter;
import com.gotravel.gotravel.dto.PaymentDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.entity.PaymentBill;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.repository.PaymentBillRepository;
import com.gotravel.gotravel.service.impl.IPaymentBillService;
import com.paypal.api.payments.Payment;

@Service
public class PaymentBillService implements IPaymentBillService{
	
	@Autowired
	private PaymentBillRepository paymentBillRepository;
	
	@Autowired
	private PaymentConverter paymentConverter;
	
	@Autowired
	private BookingResponsitory bookingResponsitory;

	@Override
	public List<PaymentDTO> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<PaymentDTO> findByPaymentId(String paymentId) {
		
		return paymentBillRepository.findByPaymentId(paymentId).stream().map(paymentConverter::toDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean isPaymentExists(String paymentId) {
		return paymentBillRepository.existsByPaymentId(paymentId);

	}

	@Override
	public PaymentDTO savePayment(Payment paymentParams, UUID bookingId) {
		// Nhận json từ payment paypal sau đó set dữ liêụ trong database
		Optional<Booking> booking;
		booking = bookingResponsitory.findById(bookingId);
		
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setMethod(paymentParams.getPayer().getPaymentMethod());
		paymentDTO.setStatus(paymentParams.getState());
		paymentDTO.setCreateTime(paymentParams.getCreateTime());
		paymentDTO.setAccountType(paymentParams.getPayer().getPayerInfo().getPayerId());
		paymentDTO.setAmount(Float.parseFloat(paymentParams.getTransactions().get(0).getAmount().getTotal()));
		
		paymentDTO.setPaymentId(paymentParams.getId());
		paymentDTO.setCapturesId(paymentParams.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
		paymentDTO.setRefundStatus("NO");
		paymentDTO.setBookingId(booking.get().getBookingId());
		
		PaymentBill paymentBill = paymentConverter.toEntity(paymentDTO);
		paymentBillRepository.save(paymentBill);
		
		System.out.println("PAYMENT PARAMS: " + paymentParams);
		System.out.println("BOOKING ID: " + bookingId);
		
		return paymentConverter.toDTO(paymentBill);
	}

	@Override
	public PaymentDTO update(UUID id, PaymentDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PaymentDTO save(PaymentDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

}
