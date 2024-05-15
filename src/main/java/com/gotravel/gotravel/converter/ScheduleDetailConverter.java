package com.gotravel.gotravel.converter;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.ScheduleDetailDTO;
import com.gotravel.gotravel.entity.ScheduleDetail;

@Component
public class ScheduleDetailConverter {
	
	public ScheduleDetailDTO toDTO(ScheduleDetail scheduleDetail) {
		
		ScheduleDetailDTO scDTO = new ScheduleDetailDTO();
		
		scDTO.setId(scheduleDetail.getId());
		scDTO.setContext(scheduleDetail.getContext());
		
		return scDTO;
		
	}
	
	public ScheduleDetail toEntity(ScheduleDetailDTO scheduleDetailDTO) {
		
		ScheduleDetail sc = new ScheduleDetail();
		sc.setId(scheduleDetailDTO.getId());
		sc.setContext(scheduleDetailDTO.getContext());
		
		return sc;
	}

}
