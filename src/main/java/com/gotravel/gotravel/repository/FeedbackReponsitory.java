package com.gotravel.gotravel.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.Feedback;

@Repository
public interface FeedbackReponsitory extends JpaRepository<Feedback, UUID> {
	@Query("SELECT f FROM Feedback f WHERE f.tour.id = :tourId")
	List<Feedback> getAllFeedbackByTourId(@Param("tourId") UUID tourId);
}
