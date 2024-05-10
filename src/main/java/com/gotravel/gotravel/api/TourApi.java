package com.gotravel.gotravel.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.repository.TourRepository;
import com.gotravel.gotravel.service.impl.ITourService;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping(value = "/api/v1/tour", produces = "application/json")
public class TourApi {
	@Autowired
	private ITourService tourService;

	@Autowired
	private TourRepository tourRepository;

	@GetMapping
	public ResponseEntity<?> getAllTour() {
		List<TourDTO> tours = tourService.findAll();

		if (!tours.isEmpty()) {
			return new ResponseEntity<>(tours, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTourById(@PathVariable("id") UUID id) {
		TourDTO tour = tourService.findById(id);
		if (!tour.isNull()) {
			return new ResponseEntity<>(tour, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Tour không tồn tại.", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/add")
	public ResponseEntity<?> addTour(@Validated @RequestBody TourDTO tourDTO) {
		TourDTO tourSave = tourService.save(tourDTO);
		System.out.println("du lieu nhap vao: " + tourDTO);
		if (tourSave != null) {
			return new ResponseEntity<>(tourSave, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Thêm không thành công!", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateTour(@PathVariable("id") UUID tourId,
			@Validated @RequestBody TourDTO updateTourDTO) {

		Optional<Tour> tourOp = tourRepository.findById(tourId);
		return tourOp.map(tour -> {
//			updateTourDTO.setTourId(tour.getTourId());
			tourService.update(tourId, updateTourDTO);
			return new ResponseEntity<>("Cập nhật thành công!", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Tour không tồn tại!", HttpStatus.NOT_FOUND));

	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTour(@PathVariable("id") UUID tourId) {
		Optional<Tour> tourOp = tourRepository.findById(tourId);
		return tourOp.map(tour -> {
			tourService.remove(tourId);
			return new ResponseEntity<>("Xóa thành công!", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Thất bại: không tìm thất tour!", HttpStatus.NOT_FOUND));
	}

}
