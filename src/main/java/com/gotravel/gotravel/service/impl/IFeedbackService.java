package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

import com.gotravel.gotravel.dto.FeedbackDTO;

public interface IFeedbackService extends IGeneralService<FeedbackDTO>{

	List<FeedbackDTO> getAllFeedbackByTourId(UUID tourId);
	
}
