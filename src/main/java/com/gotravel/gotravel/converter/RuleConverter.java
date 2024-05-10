package com.gotravel.gotravel.converter;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.entity.Rule;

@Component
public class RuleConverter {
	
	public RuleDTO toDTO(Rule rule) {
		RuleDTO ruleDTO = new RuleDTO();
		
		ruleDTO.setRuleId(rule.getRuleID());
		ruleDTO.setRuleName(rule.getRuleName());
		
		return ruleDTO;
	}

}
