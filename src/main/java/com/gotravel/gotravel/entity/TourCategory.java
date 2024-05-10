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
@Table(name = "tour_category")
public class TourCategory {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tour_category_id")
	private UUID tourCategoryId;
	
	@ManyToOne
	private Tour tour;
	
	@ManyToOne
	private Category category;

}
