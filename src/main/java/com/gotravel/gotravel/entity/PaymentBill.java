package com.gotravel.gotravel.entity;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payment-bill")
public class PaymentBill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID paymentBillId;
	
	@Column(name = "payment-id")
    private String paymentId;
    @Column(name = "amount", nullable = false)
    private Float amount;
    @Column(name = "method", nullable = false)
    private String method;
    @Column(name = "status")
    private String status;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "account_type")
    private String accountType;
    @Column (name = "captures")
    private String captures;
    @Column (name = "refund_status")
    private String refundStatus;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
	
}
