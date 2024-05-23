package com.gotravel.gotravel.converter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.FeedbackDTO;
import com.gotravel.gotravel.entity.Feedback;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.TourRepository;
import com.gotravel.gotravel.repository.UserRepository;

@Component
public class FeedbackConverter {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TourRepository tourRepository;
	
	public FeedbackDTO toDTO(Feedback feedback) {
		
		FeedbackDTO feedbackDTO = new FeedbackDTO();
		
		feedbackDTO.setFeedbackId(feedback.getFeedBackId());
		feedbackDTO.setComment(feedback.getComment());
		feedbackDTO.setRating(feedback.getRating());
		feedbackDTO.setCreateAt(feedback.getCreateAt());
		feedbackDTO.setUserId(feedback.getUser().getId());
		feedbackDTO.setUserName(feedback.getUser().getUsername());
		feedbackDTO.setTourId(feedback.getTour().getTourId());
		feedbackDTO.setTourName(feedback.getTour().getTourName());
		
		return feedbackDTO;
		
	}
	
	public Feedback toEntity(FeedbackDTO feedbackDTO) {
		
		Feedback feedback = new Feedback();
		
		feedback.setComment(feedbackDTO.getComment());
		feedback.setCreateAt(feedbackDTO.getCreateAt());
		feedback.setRating(feedbackDTO.getRating());
		
		Optional<Tour> tourOp = tourRepository.findById(feedbackDTO.getTourId());
		
		if (tourOp.isPresent()) {
			Tour tour = tourOp.get();
			feedback.setTour(tour);
		}
		
		Optional<User> userOp = userRepository.findById(feedbackDTO.getUserId());
		
		if (userOp.isPresent()) {
			User user = userOp.get();
			feedback.setUser(user);
		}
		
		return feedback;
		
	}

}
