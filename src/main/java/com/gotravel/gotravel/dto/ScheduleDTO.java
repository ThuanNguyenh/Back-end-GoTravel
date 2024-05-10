package com.gotravel.gotravel.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ScheduleDTO {
	
	private UUID scheduleId;
	private int date;
	private String activity;
	private UUID tourId;

}
