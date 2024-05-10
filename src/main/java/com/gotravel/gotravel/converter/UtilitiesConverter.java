package com.gotravel.gotravel.converter;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.entity.Utilities;

@Component
public class UtilitiesConverter {
	public UtilitiesDTO toDTO(Utilities utilities) {
		UtilitiesDTO utilitiesDTO = new UtilitiesDTO();
		utilitiesDTO.setUtilityId(utilities.getUtilityID());
		utilitiesDTO.setUtilityName(utilities.getUtilityName());
		
		return utilitiesDTO;
	}
}
