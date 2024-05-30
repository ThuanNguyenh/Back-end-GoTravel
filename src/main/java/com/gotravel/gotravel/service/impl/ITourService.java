package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.TourDTO;

public interface ITourService extends IGeneralService<TourDTO> {

	public List<TourDTO> getAllTourOfUser(UUID userId);

	long countToursByUserId(UUID userId);

}
