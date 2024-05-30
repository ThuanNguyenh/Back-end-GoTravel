package com.gotravel.gotravel.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gotravel.gotravel.enums.ConfirmationBooking;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "booking")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "booking_id")
	private UUID bookingId;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Tour tour;
	
	@Column(name = "check_in")
	private Date checkIn;
	
	@Column(name = "check_out")
	private Date checkOut;
	
	@Column(name = "total_price")
	private Double totalPrice;
	
	@Enumerated(EnumType.STRING)
	private ConfirmationBooking confirmation = ConfirmationBooking.RESERVE;
	
	@Column(name = "status")
	private Boolean status;
	
	@Column(name = "num_guest")
	private int numGuest;
	
	@Column(name = "create_at")
	private LocalDateTime createAt;
	
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<PaymentBill> paymentBills;
	
	@ManyToMany
    @JoinTable(
    		name = "booking_category",
    		joinColumns = @JoinColumn(name = "booking_id"),
    		inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

}
