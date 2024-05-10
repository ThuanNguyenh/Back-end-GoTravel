package com.gotravel.gotravel.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rules")
public class Rule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rule_id")
	private UUID ruleID;
	
	@Column(name = "rule_name", columnDefinition = "NTEXT")
	private String ruleName;
	
	@OneToMany(mappedBy = "rule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TourRule> tourRules;

}
