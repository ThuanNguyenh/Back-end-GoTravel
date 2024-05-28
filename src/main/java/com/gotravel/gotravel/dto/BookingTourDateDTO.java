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
public class BookingTourDateDTO {

	private UUID bookingId;
	private UUID tourId;
	private String tourName;
	private String province;
	private String district;
	private String ward;
	private int tourTime;
	private int numGuest;
	private int numGuestBooked;
	private Date checkInDate;
	private Date checkOutDate;
	private Double totalPriceBooked;
	private String confirmation;

}
