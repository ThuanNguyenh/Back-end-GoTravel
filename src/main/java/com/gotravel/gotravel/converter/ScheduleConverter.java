package com.gotravel.gotravel.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.ScheduleDTO;
import com.gotravel.gotravel.dto.ScheduleDetailDTO;
import com.gotravel.gotravel.entity.Schedule;
import com.gotravel.gotravel.entity.ScheduleDetail;

@Component
public class ScheduleConverter {

	@Autowired
	private ScheduleDetailConverter scheduleDetailConverter;

	public ScheduleDTO toDTO(Schedule schedule) {
		ScheduleDTO scheduleDTO = new ScheduleDTO();

		scheduleDTO.setScheduleId(schedule.getScheduleId());
		scheduleDTO.setDate(schedule.getDate());

		List<ScheduleDetailDTO> details = new ArrayList<>();

		for (ScheduleDetail s : schedule.getActivities()) {
			details.add(scheduleDetailConverter.toDTO(s));
		}
		scheduleDTO.setActivities(details);

		if (!schedule.getTour().isNull()) {
			scheduleDTO.setTourId(schedule.getTour().getTourId());
		}

		return scheduleDTO;
	}

}
