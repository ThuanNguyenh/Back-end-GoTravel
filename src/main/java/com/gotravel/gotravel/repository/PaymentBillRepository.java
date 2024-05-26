package com.gotravel.gotravel.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.PaymentBill;

@Repository
public interface PaymentBillRepository extends  JpaRepository<PaymentBill, UUID>{

	List<PaymentBill> findByPaymentId(String paymentId);
	
	boolean existsByPaymentId(String paymentId);
	
}
