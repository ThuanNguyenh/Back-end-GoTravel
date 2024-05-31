package com.gotravel.gotravel.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.dto.ImageDTO;
import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.dto.ScheduleDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.entity.Feedback;
import com.gotravel.gotravel.entity.Image;
import com.gotravel.gotravel.entity.Schedule;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.entity.TourCategory;
import com.gotravel.gotravel.entity.TourRule;
import com.gotravel.gotravel.entity.TourUtilities;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.UserRepository;

@Component
public class TourConverter {

	@Autowired
	private ImageConverter imageConverter;

	@Autowired
	private UserConverter userConverter;

	@Autowired
	private CategoryConverter categoryConverter;

	@Autowired
	private UtilitiesConverter utilitiesConverter;

	@Autowired
	private RuleConverter ruleConverter;

	@Autowired
	private ScheduleConverter scheduleConverter;

	@Autowired
	private UserRepository userRepository;

	public TourDTO toDTO(Tour tour) {

		TourDTO tourDTO = new TourDTO();
		tourDTO.setTourId(tour.getTourId());
		tourDTO.setTourName(tour.getTourName());
		tourDTO.setProvince(tour.getProvince());
		tourDTO.setDistrict(tour.getDistrict());
		tourDTO.setWard(tour.getWard());
		tourDTO.setDetailAddress(tour.getDetailAddress());
		tourDTO.setDescription(tour.getDescription());
		tourDTO.setThumbnail(tour.getThumbnail());
		tourDTO.setPriceAdult(tour.getPriceAdult());
		tourDTO.setPriceChildren(tour.getPriceChildren());
		tourDTO.setNumGuest(tour.getNumguest());
		tourDTO.setDiscount(tour.getDiscount());
		tourDTO.setCreateAt(tour.getCreate_at());
		tourDTO.setTourTime(tour.getTourTime());
		tourDTO.setStatus(tour.getStatus());

		// thu thập các đánh giá
		List<Float> allRatings = tour.getFeedbacks().stream().map(Feedback::getRating).collect(Collectors.toList());
		tourDTO.setRatings(allRatings);

		// IMAGES
		List<ImageDTO> imageList = new ArrayList<>();
		for (Image i : tour.getImages()) {
			imageList.add(imageConverter.toDTO(i));

		}
		tourDTO.setImages(imageList);

		// Schedule
		List<ScheduleDTO> scheduleList = new ArrayList<>();
		if (!tour.getSchedules().isEmpty()) {
			for (Schedule s : tour.getSchedules()) {
				scheduleList.add(scheduleConverter.toDTO(s));
			}
		}
		tourDTO.setSchedules(scheduleList);

		// CATEGORIES
		List<CategoryDTO> categoryList = new ArrayList<>();
		if (!tour.getTourCategories().isEmpty()) {
			for (TourCategory t : tour.getTourCategories()) {
				categoryList.add(categoryConverter.toDTO(t.getCategory()));
			}
		}
		tourDTO.setCategories(categoryList);

		// UTILITIES
		List<UtilitiesDTO> utilitiesList = new ArrayList<>();
		if (!tour.getTourUtilities().isEmpty()) {
			for (TourUtilities t : tour.getTourUtilities()) {
				utilitiesList.add(utilitiesConverter.toDTO(t.getUtilities()));
			}
		}
		tourDTO.setUtilities(utilitiesList);

		// RULES
		List<RuleDTO> rules = new ArrayList<>();
		if (!tour.getTourRules().isEmpty()) {
			for (TourRule t : tour.getTourRules()) {
				rules.add(ruleConverter.toDTO(t.getRule()));
			}
		}
		tourDTO.setRules(rules);

		// USER
		if (tour.getUser() != null) {
			tourDTO.setOwner(userConverter.toDTO(tour.getUser()));
		}

		return tourDTO;

	}

	// to entity

	public Tour toEntity(TourDTO tourDTO) {

		Tour tour = new Tour();

		if (tourDTO.getTourId() != null) {
			tour.setTourId(tourDTO.getTourId());
		}
		tour.setTourName(tourDTO.getTourName());
		tour.setProvince(tourDTO.getProvince());
		tour.setDistrict(tourDTO.getDistrict());
		tour.setWard(tourDTO.getWard());
		tour.setDetailAddress(tourDTO.getDetailAddress());
		tour.setDescription(tourDTO.getDescription());
		tour.setThumbnail(tourDTO.getThumbnail());
		tour.setPriceAdult(tourDTO.getPriceAdult());
		tour.setPriceChildren(tourDTO.getPriceChildren());
		tour.setNumguest(tourDTO.getNumGuest());
		tour.setDiscount(tourDTO.getDiscount());
		tour.setTourTime(tourDTO.getTourTime());
		tour.setFeedbacks(new ArrayList<>());

		Optional<User> optionalUser = userRepository.findById(tourDTO.getOwner().getUserId());

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			tour.setUser(user);
		} else {
			tour.setUser(null);
		}

		return tour;

	}
}
