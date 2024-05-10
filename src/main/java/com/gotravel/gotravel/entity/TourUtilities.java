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

@Entity
@Data
@Table(name = "tour_utilities")
public class TourUtilities {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tour_utility_id")
	private UUID tourUtilityId;
	
	@ManyToOne
	private Tour tour;
	
	@ManyToOne
	private Utilities utilities;

}
