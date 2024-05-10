package com.gotravel.gotravel.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.TourCategory;

@Repository
public interface TourCategoryRepository extends JpaRepository<TourCategory, UUID>{

}
