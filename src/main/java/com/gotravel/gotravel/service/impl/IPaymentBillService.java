package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.PaymentDTO;
import com.paypal.api.payments.Payment;

public interface IPaymentBillService extends IGeneralService<PaymentDTO>{

	public PaymentDTO savePayment(Payment paymentParams, UUID bookingId);
	
	public List<PaymentDTO> findByPaymentId(String paymentId);
	
	public boolean isPaymentExists(String paymentId);
	
}
