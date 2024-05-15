package com.gotravel.gotravel.dto;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.entity.ScheduleDetail;
import com.gotravel.gotravel.entity.Tour;

import lombok.Data;

@Data
public class ScheduleDTO {

	private UUID scheduleId;
	private int date;
	private List<ScheduleDetailDTO> activities;
	private UUID tourId;

}
