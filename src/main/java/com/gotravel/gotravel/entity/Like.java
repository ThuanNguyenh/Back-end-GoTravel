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
@Table(name = "likes")
public class Like {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "like_id")
	private UUID likeId;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Tour tour;

}
