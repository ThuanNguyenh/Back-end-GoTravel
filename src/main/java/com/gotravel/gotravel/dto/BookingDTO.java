package com.gotravel.gotravel.dto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
	
	// to convert with entity
	private UserDTO user;
	private TourDTO tour;
	private Date checkInDate;
	private Date checkOutDate;
	private String confirmation;
	private UUID userId;
	private UUID tourId;
	private UUID bookingId;
	private int numGuest;
	private Double total;
	private Boolean status;
	private Date dateBooking;
	private Double price;
	private String currency;
	private String method;
	private String intent;
	private String description;
	private LocalDateTime createAt;
	

}
