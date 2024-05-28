package com.gotravel.gotravel.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {
	@Value("${paypal.client.id}")
	private String clientId;

	@Value("${paypal.client.secret}")
	private String clientSecret;

	@Value("${paypal.oauth2.token.url}")
	private String tokenUrl;

	@Autowired
	private APIContext apiContext;

	public Payment createPayment(Double total, String currency, String method, String intent, String description,
			String cancelUrl, String successUrl) throws PayPalRESTException {
		// tạo đối tượng amount và thiết lập loại tiền tệ
		Amount amount = new Amount();
		amount.setCurrency(currency);
		// Chuyển đổi total sang dạng BigDecimal và làm tròn đến 2 chữ số thập phân.
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total));

		// Tạo đối tượng Transaction và thiết lập mô tả và số tiền
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		// Tạo danh sách giao dịch và thêm giao dịch vào danh sách
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		// Tạo đối tượng Payer và thiết lập phương thức thanh toán
		Payer payer = new Payer();
		payer.setPaymentMethod(method);

		// Tạo đối tượng Payment và thiết lập ý định thanh toán, người thanh toán, và
		// danh sách giao dịch

		Payment payment = new Payment();
		payment.setIntent(intent);
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		// Tạo đối tượng RedirectUrls và thiết lập các URL điều hướng
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

//		System.out.println("thong tin thanh toán: " + payment);

		// Tạo thanh toán trên PayPal và trả về đối tượng Payment đã được tạo
		return payment.create(apiContext);
	}

	// thực hiện thanh toán
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		Payment payment = new Payment();
		payment.setId(paymentId);

		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		
		Payment executedPayment = payment.execute(apiContext, paymentExecute);

//		System.out.println("thong tin thanh toán 2: " + executedPayment);

		return executedPayment;
	}

	// phương thức lấy accessToken
	public String getAccessToken() {
		RestTemplate restTemplate = new RestTemplate(); // thực hiện các yêu cầu HTTP
		// Headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // LOẠI NỘI DUNG
		headers.setBasicAuth(clientId, clientSecret); // XÁC THỰC CƠ BẢN
		// Body
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		// Entity
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		// Make the call
		ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);
		String body = response.getBody();
		// Parse the body to extract the access token
		// This is a simplistic example; you might use a JSON parsing library like
		// Jackson or Gson
		if (response.getStatusCode() == HttpStatus.OK && body != null && body.contains("access_token")) {
			String token = body.split("\"access_token\":\"")[1].split("\"")[0];
			return token;
		} else {
			// Handle the error scenario
			throw new RuntimeException("Failed to retrieve access token from PayPal");
		}
	}

}
