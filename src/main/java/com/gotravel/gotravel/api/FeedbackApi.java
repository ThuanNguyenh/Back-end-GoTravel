package com.gotravel.gotravel.api;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.FeedbackDTO;
import com.gotravel.gotravel.service.impl.IFeedbackService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/feedback", produces = "application/json")
public class FeedbackApi {

	@Autowired
	private IFeedbackService feedbackService;

	@PostMapping("/create")
	private ResponseEntity<?> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {

		FeedbackDTO newFeedbackDTO = feedbackService.save(feedbackDTO);

		if (newFeedbackDTO != null) {
			return new ResponseEntity<>(newFeedbackDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Thêm đánh giá không thành công.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/allOfTour/{tourId}")
	public ResponseEntity<?> getAllFeedbackOfTour(@PathVariable("tourId") UUID tourId) {
		
		List<FeedbackDTO> allFeedback = feedbackService.getAllFeedbackByTourId(tourId);
		
		if (!allFeedback.isEmpty()) {
			return new ResponseEntity<>(allFeedback, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);
		}
	}

}
