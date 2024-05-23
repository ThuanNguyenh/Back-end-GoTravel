package com.gotravel.gotravel.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {

	private UUID feedbackId;
	
	private String comment;
	
	private Float rating;
	
	private LocalDateTime createAt;
	
	private UUID userId;
	
	private String userName;
	
	private UUID tourId;
	
	private String tourName;
	
}
