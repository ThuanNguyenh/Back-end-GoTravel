package com.gotravel.gotravel.entity;

import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "booking")
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
	
	@Column(name = "status")
	private boolean status;

}
