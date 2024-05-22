package com.gotravel.gotravel.dto;

import java.sql.Date;
import java.util.UUID;

import com.gotravel.gotravel.enums.ConfirmationBooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

	private UUID bookingId;
	private int numGuest;
	private Float totalPrice;
	private UserDTO user;
	private TourDTO tour;
	private Date checkInDate;
	private Date checkOutDate;
	private String status;

}
