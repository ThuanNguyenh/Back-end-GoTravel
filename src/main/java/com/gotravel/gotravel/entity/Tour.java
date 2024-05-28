package com.gotravel.gotravel.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@Table(name = "tour")
public class Tour {

	@Id
	@Column(name = "tour_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID tourId;
	
	public UUID getTourId() {
        return tourId;
    }

	@ManyToOne
	private User user;

	@Column(name = "tour_name", columnDefinition = "NTEXT")
	private String tourName;

	@Column(name = "description", columnDefinition = "NTEXT")
	private String description;

	@Column(name = "thumbnail", length = Integer.MAX_VALUE)
	private String thumbnail;

	@Column(name = "province", columnDefinition = "NVARCHAR(255)")
	private String province;

	@Column(name = "district", columnDefinition = "NVARCHAR(255)")
	private String district;

	@Column(name = "ward", columnDefinition = "NVARCHAR(255)")
	private String ward;

	@Column(name = "detail_address", columnDefinition = "NTEXT")
	private String detailAddress;

	@Column(name = "price_adult")
	private Float priceAdult;
	
	@Column(name = "price_children")
	private Float priceChildren;

	@Column(name = "status")
	private boolean status = false;

	@Column(name = "num_guest")
	private int numguest;

	@Column(name = "discount")
	private int discount;

	@Column(name = "tour_time")
	private int tourTime;

	@Column(name = "create_at")
	private LocalDateTime create_at;

	public Tour() {
		this.discount = 0;
		this.priceAdult = 0.0f;
		this.priceChildren = 0.0f;
		this.numguest = 0;
		this.tourTime = 0;
	}

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TourUtilities> tourUtilities;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TourCategory> tourCategories;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TourRule> tourRules;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Schedule> schedules;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Like> likes;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Booking> bookings;

}
