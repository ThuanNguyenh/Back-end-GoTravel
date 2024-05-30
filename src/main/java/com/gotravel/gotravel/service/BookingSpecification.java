package com.gotravel.gotravel.service;

import java.sql.Date;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.entity.Category;
import com.gotravel.gotravel.entity.TourCategory;
import com.gotravel.gotravel.enums.ConfirmationBooking;

import jakarta.persistence.criteria.Join;

public class BookingSpecification {

	public static Specification<Booking> hasCheckInDate(Date checkInDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("checkIn"), checkInDate);
	}

	public static Specification<Booking> hasCheckOutDate(Date checkOutDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("checkOut"), checkOutDate);
	}

	public static Specification<Booking> hasCategory(UUID categoryId) {
		return (root, query, criteriaBuilder) -> {
			Join<Booking, TourCategory> tourCategoryJoin = root.join("tour").join("tourCategories");
			return criteriaBuilder.equal(tourCategoryJoin.get("category").get("categoryId"), categoryId);
		};
	}
	public static Specification<Booking> hasCategory(String categoryName) {
	    return (root, query, criteriaBuilder) -> {
	        Join<Booking, Category> categoryJoin = root.join("categories");
	        return criteriaBuilder.equal(categoryJoin.get("categoryName"), categoryName);
	    };
	}

	public static Specification<Booking> hasConfirmation(ConfirmationBooking confirmation) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("confirmation"), confirmation);
	}
	
	public static Specification<Booking> searchByKeyword(String keyword) {
	    return (root, query, criteriaBuilder) -> {
	        String keywordLike = "%" + keyword + "%";
	        return criteriaBuilder.or(
	            criteriaBuilder.like(root.get("tour").get("tourName"), keywordLike),
	            criteriaBuilder.like(root.get("tour").get("province"), keywordLike)
	        );
	    };
	}


}
