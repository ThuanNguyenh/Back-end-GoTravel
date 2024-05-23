package com.gotravel.gotravel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.FeedbackConverter;
import com.gotravel.gotravel.dto.FeedbackDTO;
import com.gotravel.gotravel.entity.Feedback;
import com.gotravel.gotravel.repository.FeedbackReponsitory;
import com.gotravel.gotravel.service.impl.IFeedbackService;

@Service
public class FeedbackService implements IFeedbackService{
	
	@Autowired
	private FeedbackReponsitory feedbackReponsitory;
	
	@Autowired
	private FeedbackConverter feedbackConverter;

	@Override
	public List<FeedbackDTO> findAll() {
		
		List<Feedback> feedbacks = feedbackReponsitory.findAll();
		
		List<FeedbackDTO> feedbackDTOs = new ArrayList<>();
		
		for (Feedback f : feedbacks) {
			feedbackDTOs.add(feedbackConverter.toDTO(f));
		}
		
		return feedbackDTOs;
	}

	@Override
	public FeedbackDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeedbackDTO save(FeedbackDTO feedbackDTO) {
		
		Feedback feedback = new Feedback();
		feedback = feedbackConverter.toEntity(feedbackDTO);
		
		Feedback result = feedbackReponsitory.save(feedback);
		
		return feedbackConverter.toDTO(result);
	}
	
	@Override
	public List<FeedbackDTO> getAllFeedbackByTourId(UUID tourId) {
		
		List<Feedback> feedbackByTourId = feedbackReponsitory.getAllFeedbackByTourId(tourId);
		
		List<FeedbackDTO> feedbackDTOs = new ArrayList<>();
		
		for (Feedback f : feedbackByTourId) {
			feedbackDTOs.add(feedbackConverter.toDTO(f));
		}
		
		return feedbackDTOs;

	}

	@Override
	public FeedbackDTO update(UUID id, FeedbackDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub
		
	}
	

}
