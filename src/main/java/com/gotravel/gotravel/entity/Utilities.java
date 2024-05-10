package com.gotravel.gotravel.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "utilities")
public class Utilities {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "utility_id")
	private UUID utilityID;
	
	@Column(name = "utility_name", columnDefinition = "NTEXT")
	private String utilityName;

}
