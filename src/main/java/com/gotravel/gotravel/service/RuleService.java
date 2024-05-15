package com.gotravel.gotravel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.RuleConverter;
import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.entity.Rule;
import com.gotravel.gotravel.repository.RuleRepository;
import com.gotravel.gotravel.service.impl.IRuleService;

@Service
public class RuleService implements IRuleService{
	
	@Autowired
	private RuleRepository ruleRepository;
	
	@Autowired
	private RuleConverter ruleConverter;

	@Override
	public List<RuleDTO> findAll() {
		
		List<Rule> rules = ruleRepository.findAll();
		
		List<RuleDTO> ruleDTOS = new ArrayList<>();
		
		for (Rule r : rules) {
			RuleDTO ruleDTO = ruleConverter.toDTO(r);
			ruleDTOS.add(ruleDTO);
		}
 		
		return ruleDTOS;
	}

	@Override
	public RuleDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleDTO save(RuleDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleDTO update(UUID id, RuleDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub
		
	}

}
