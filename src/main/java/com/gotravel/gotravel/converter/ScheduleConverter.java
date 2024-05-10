package com.gotravel.gotravel.converter;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.ScheduleDTO;
import com.gotravel.gotravel.entity.Schedule;

@Component
public class ScheduleConverter {

	public ScheduleDTO toDTO(Schedule schedule) {
		ScheduleDTO scheduleDTO = new ScheduleDTO();
		
		scheduleDTO.setScheduleId(schedule.getScheduleId());
		scheduleDTO.setDate(schedule.getDate());
		scheduleDTO.setActivity(schedule.getActivity());
		
		if (!schedule.getTour().isNull()) {
			scheduleDTO.setTourId(schedule.getTour().getTourId());
		}
		
		return scheduleDTO;
	}
	
}
