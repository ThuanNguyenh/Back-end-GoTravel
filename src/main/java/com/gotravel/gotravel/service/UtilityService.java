package com.gotravel.gotravel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.UtilitiesConverter;
import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.entity.Utilities;
import com.gotravel.gotravel.repository.UtilitiesRepository;
import com.gotravel.gotravel.service.impl.IUtilityService;

@Service
public class UtilityService implements IUtilityService {
	
	@Autowired
	private UtilitiesRepository utilitiesRepository;
	
	@Autowired
	private UtilitiesConverter utilitiesConverter;

	@Override
	public List<UtilitiesDTO> findAll() {
		
		List<Utilities> allUtility = utilitiesRepository.findAll();
		
		List<UtilitiesDTO> utilityDTOS = new ArrayList<>();
		
		for (Utilities u : allUtility) {
			UtilitiesDTO utilityDTO = utilitiesConverter.toDTO(u);
			utilityDTOS.add(utilityDTO);
		}

		return utilityDTOS;
	}

	@Override
	public UtilitiesDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UtilitiesDTO save(UtilitiesDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UtilitiesDTO update(UUID id, UtilitiesDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub

	}

}
