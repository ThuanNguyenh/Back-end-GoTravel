package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.entity.Tour;

public interface ITourService extends IGeneralService<TourDTO> {

	public List<TourDTO> getAllTourOfUser(UUID userId);
	
	public List<TourDTO> getAllTourOfUserFilter(UUID userId, Boolean status);

	long countToursByUserId(UUID userId);
	
	public void updateTourStatus(UUID tourId, boolean status);

}
