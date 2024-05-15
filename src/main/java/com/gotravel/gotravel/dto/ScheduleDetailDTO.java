package com.gotravel.gotravel.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailDTO {

	private UUID id;
	private String context;

	public ScheduleDetailDTO(String context) {
		this.context = context;
	}

}
