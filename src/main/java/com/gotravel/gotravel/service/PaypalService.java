package com.gotravel.gotravel.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {

	@Autowired
	private APIContext apiContext;

	public Payment createPayment(Double total, String currency, String method, String intent, String description,
			String cancelUrl, String successUrl) throws PayPalRESTException {

		// Kiểm tra các giá trị đầu vào
		if (total == null || total <= 0) {
			throw new IllegalArgumentException("Total must be greater than zero");
		}
		if (currency == null || currency.isEmpty()) {
			throw new IllegalArgumentException("Currency must not be empty");
		}
		if (method == null || method.isEmpty()) {
			throw new IllegalArgumentException("Method must not be empty");
		}
		if (intent == null || intent.isEmpty()) {
			throw new IllegalArgumentException("Intent must not be empty");
		}
		if (description == null || description.isEmpty()) {
			throw new IllegalArgumentException("Description must not be empty");
		}
		if (cancelUrl == null || cancelUrl.isEmpty()) {
			throw new IllegalArgumentException("Cancel URL must not be empty");
		}
		if (successUrl == null || successUrl.isEmpty()) {
			throw new IllegalArgumentException("Success URL must not be empty");
		}

		// định dạng giá trị tiền tệ
		Amount amount = new Amount();
		amount.setCurrency(currency);
		amount.setTotal(String.format("%.2f", total));

		// tạo giao dịch
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		// tạo người thanh toán
		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());

		// tạo thanh toán
		Payment payment = new Payment();
		payment.setIntent(intent.toString());
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		// thực hiện tạo thanh toán
		return payment.create(apiContext);
	}

	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

		if (paymentId == null || paymentId.isEmpty()) {
			throw new IllegalArgumentException("Payment ID must not be empty");
		}
		if (payerId == null || payerId.isEmpty()) {
			throw new IllegalArgumentException("Payer ID must not be empty");
		}

		// thực hiện thanh toán
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}
}
