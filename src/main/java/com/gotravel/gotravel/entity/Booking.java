package com.gotravel.gotravel.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gotravel.gotravel.enums.ConfirmationBooking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "booking")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "booking_id")
	private UUID bookingId;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Tour tour;
	
	@Column(name = "check_in")
	private Date checkIn;
	
	@Column(name = "check_out")
	private Date checkOut;
	
	@Column(name = "total_price")
	private Float totalPrice;
	
	@Enumerated(EnumType.STRING)
	private ConfirmationBooking status;
	
	@Column(name = "num_guest")
	private int numGuest;
	
	@Column(name = "create_at")
	private LocalDateTime createAt;

}
