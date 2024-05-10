package com.gotravel.gotravel.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tour_rule")
public class TourRule {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tour_rule_id")
	private UUID tourRuleId;
	
	@ManyToOne()
	private Tour tour;
	
	@ManyToOne()
	private Rule rule;
	
	
}
