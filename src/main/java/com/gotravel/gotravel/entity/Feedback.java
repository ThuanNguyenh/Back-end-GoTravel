package com.gotravel.gotravel.entity;

import java.time.LocalDateTime;
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
@Table(name = "feedback")
public class Feedback {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "feedback_id")
	private UUID feedBackId;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "rating")
	private Float rating;
	
	@Column(name = "create_at")
	private LocalDateTime createAt;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Tour tour;

}
