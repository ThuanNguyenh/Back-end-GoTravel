package com.gotravel.gotravel.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.TourConverter;
import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.dto.ImageDTO;
import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.dto.ScheduleDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.entity.Category;
import com.gotravel.gotravel.entity.Image;
import com.gotravel.gotravel.entity.Rule;
import com.gotravel.gotravel.entity.Schedule;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.entity.TourCategory;
import com.gotravel.gotravel.entity.TourRule;
import com.gotravel.gotravel.entity.TourUtilities;
import com.gotravel.gotravel.entity.Utilities;
import com.gotravel.gotravel.repository.CategoryRepository;
import com.gotravel.gotravel.repository.ImageRepository;
import com.gotravel.gotravel.repository.RuleRepository;
import com.gotravel.gotravel.repository.ScheduleRepository;
import com.gotravel.gotravel.repository.TourCategoryRepository;
import com.gotravel.gotravel.repository.TourRepository;
import com.gotravel.gotravel.repository.TourRuleReposiitory;
import com.gotravel.gotravel.repository.TourUtilitiesRepository;
import com.gotravel.gotravel.repository.UtilitiesRepository;
import com.gotravel.gotravel.service.impl.ITourService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TourService implements ITourService {

	@Autowired
	private TourConverter tourConverter;

	@Autowired
	private TourRepository tourRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UtilitiesRepository utilitiesRepository;

	@Autowired
	private RuleRepository ruleRepository;

	@Autowired
	private TourCategoryRepository tourCategoryRepository;

	@Autowired
	private TourUtilitiesRepository tourUtilitiesRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private TourRuleReposiitory tourRuleReposiitory;

	@Override
	public List<TourDTO> findAll() {

		List<Tour> tours = tourRepository.findAll();

		List<TourDTO> tourDTOs = new ArrayList<>();

		for (Tour t : tours) {
			TourDTO tourDTO = tourConverter.toDTO(t);
			tourDTOs.add(tourDTO);
		}

		return tourDTOs;
	}

	@Override
	public TourDTO findById(UUID id) {

		Tour tour = tourRepository.findById(id).get();

		TourDTO tourDTO = tourConverter.toDTO(tour);

		return tourDTO;
	}

	@Override
	public TourDTO save(TourDTO tourDTO) {

		Tour tour = new Tour();
		tour = tourConverter.toEntity(tourDTO);

		// HÌNH ẢNH
		List<Image> imageList = new ArrayList<>();

		for (ImageDTO i : tourDTO.getImages()) {
			Image image = new Image(i.getImageId(), tour, i.getUrl());
			imageList.add(image);
		}

		// LỊCH TRÌNH
		List<Schedule> schedules = new ArrayList<>();
		for (ScheduleDTO s : tourDTO.getSchedules()) {
			schedules.add(new Schedule(s.getScheduleId(), s.getActivity(), s.getDate(), tour));
		}

		// LOAI TOUR
		List<TourCategory> tourCategories = new ArrayList<>();
		for (CategoryDTO c : tourDTO.getCategories()) {
			TourCategory tc = new TourCategory();
			tc.setTour(tour);
			Optional<Category> co = categoryRepository.findById(c.getCategoryId());

			if (co.isPresent()) {
				Category category = co.get();
				tc.setCategory(category);
			}
			tourCategories.add(tc);
		}

		// TIỆN ÍCH
		List<TourUtilities> tourUtilities = new ArrayList<>();
		for (UtilitiesDTO u : tourDTO.getUtilities()) {
			TourUtilities tu = new TourUtilities();
			tu.setTour(tour);
			Optional<Utilities> uo = utilitiesRepository.findById(u.getUtilityId());
			if (uo.isPresent()) {
				Utilities utility = uo.get();
				tu.setUtilities(utility);
			}
			tourUtilities.add(tu);
		}

		// QUY ĐỊNH
		List<TourRule> tourRules = new ArrayList<>();
		for (RuleDTO r : tourDTO.getRules()) {
			TourRule listRule = new TourRule();
			listRule.setTour(tour);
			Optional<Rule> ro = ruleRepository.findById(r.getRuleId());
			if (ro.isPresent()) {
				Rule rule = ro.get();
				listRule.setRule(rule);
			}
			tourRules.add(listRule);
		}

		LocalDate today = LocalDate.now();
		tour.setCreate_at(Date.valueOf(today));
		tour.setImages(imageList);
		tour.setSchedules(schedules);
		tour.setTourCategories(tourCategories);
		tour.setTourUtilities(tourUtilities);
		tour.setTourRules(tourRules);
		tourRepository.save(tour);
		return tourConverter.toDTO(tour);
	}

	@Override
	public TourDTO update(UUID tourId, TourDTO updateTourDTO) {

		Optional<Tour> tourOp = tourRepository.findById(tourId);

		if (tourOp.isPresent()) {
			Tour existingTour = tourOp.get();
			Tour tourUpdate = tourConverter.toEntity(updateTourDTO);

			existingTour.setTourId(existingTour.getTourId());
			existingTour.setTourName(tourUpdate.getTourName());
			existingTour.setDescription(tourUpdate.getDescription());
			existingTour.setThumbnail(tourUpdate.getThumbnail());
			existingTour.setProvince(tourUpdate.getProvince());
			existingTour.setDistrict(tourUpdate.getDistrict());
			existingTour.setWard(tourUpdate.getWard());
			existingTour.setDetailAddress(tourUpdate.getDetailAddress());
			existingTour.setPrice(tourUpdate.getPrice());
			existingTour.setNumguest(tourUpdate.getNumguest());
			existingTour.setDiscount(tourUpdate.getDiscount());
			existingTour.setStartDate(tourUpdate.getStartDate());
			existingTour.setEndDate(tourUpdate.getEndDate());

			checkUpdate(existingTour, updateTourDTO);

			Tour result = tourRepository.save(existingTour);
			return tourConverter.toDTO(result);

		} else {
			throw new EntityNotFoundException("Dữ liệu không tồn tại.");
		}
	}

	// check update
	public void checkUpdate(Tour existingTour, TourDTO updateTourDTO) {
		List<TourCategory> categoryToRemove = new ArrayList<>();
		List<TourRule> rulesToMove = new ArrayList<>();
		List<TourUtilities> utilitiesToMove = new ArrayList<>();
		List<Image> imagesToRemove = new ArrayList<>();
		List<Schedule> scheduleRemove = new ArrayList<>();

		// CHECK IMAGES
		for (Image existingImage : existingTour.getImages()) {
			boolean exitsUpdate = false;

			// nếu image update trùng với image cũ thì bỏ qua
			for (ImageDTO imageDTO : updateTourDTO.getImages()) {
				if (imageDTO.getUrl().equals(existingImage.getUrl())) {
					exitsUpdate = true;
					break;
				}
			}

			if (!exitsUpdate) {
				imageRepository.delete(existingImage);
				imagesToRemove.add(existingImage);
			}

		}
		// loại bỏ tất cả những hình ảnh muốn bỏ khỏi ds hình ảnh của tour hiện tại
		existingTour.getImages().removeAll(imagesToRemove);

		// check neu chua co image thi add
		for (ImageDTO imageDTO : updateTourDTO.getImages()) {
			boolean exist = false;
			for (Image existImage : existingTour.getImages()) {
				if (imageDTO.getUrl().equals(existImage.getUrl())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				Image newImage = new Image();
				newImage.setTour(existingTour);
				newImage.setUrl(imageDTO.getUrl());
				existingTour.getImages().add(newImage);
			}
		}

		// CHECK schedule
		for (Schedule existingSchedule : existingTour.getSchedules()) {
			boolean existingUpdate = false;
			for (ScheduleDTO scheduleDTO : updateTourDTO.getSchedules()) {
				if (scheduleDTO.getActivity().equals(existingSchedule.getActivity())) {
					existingUpdate = true;
					break;
				}
			}
			if (!existingUpdate) {
				scheduleRepository.delete(existingSchedule);
				scheduleRemove.add(existingSchedule);
			}
		}
		existingTour.getSchedules().removeAll(scheduleRemove);

		for (ScheduleDTO scheduleDTO : updateTourDTO.getSchedules()) {
			boolean exist = false;
			for (Schedule existingSchedule : existingTour.getSchedules()) {
				if (scheduleDTO.getActivity().equals(existingSchedule.getActivity())) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				Schedule newShedule = new Schedule();
				newShedule.setTour(existingTour);
				newShedule.setActivity(scheduleDTO.getActivity());
				newShedule.setDate(scheduleDTO.getDate());
				existingTour.getSchedules().add(newShedule);
			}
		}

		// Check category
		for (TourCategory existingCategory : existingTour.getTourCategories()) {

			boolean existsInUpdate = false;

			for (CategoryDTO categoryDTO : updateTourDTO.getCategories()) {
				if (categoryDTO.getCategoryId().equals(existingCategory.getCategory().getCategoryId())) {
					existsInUpdate = true;
					break;
				}
			}

			if (!existsInUpdate) {
				categoryToRemove.add(existingCategory);
			}

		}

		// xóa những đối tượng category muốn bỏ
		existingTour.getTourCategories().removeAll(categoryToRemove);
		for (TourCategory tourCategory : categoryToRemove) {
			tourCategoryRepository.delete(tourCategory);
		}

		// cap nhat ds category
		for (CategoryDTO categoryDTO : updateTourDTO.getCategories()) {
			boolean exist = false;
			for (TourCategory existingCategory : existingTour.getTourCategories()) {
				if (categoryDTO.getCategoryId().equals(existingCategory.getCategory().getCategoryId())) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				TourCategory newCategory = new TourCategory();
				newCategory.setTour(existingTour);
				Optional<Category> cate = categoryRepository.findById(categoryDTO.getCategoryId());
				newCategory.setCategory(cate.get());
				existingTour.getTourCategories().add(newCategory);
			}
		}

		// Check utilities
		for (TourUtilities existingUtilities : existingTour.getTourUtilities()) {

			boolean existsInUpdate = false;

			for (UtilitiesDTO utilityDTO : updateTourDTO.getUtilities()) {
				if (utilityDTO.getUtilityId().equals(existingUtilities.getUtilities().getUtilityID())) {
					existsInUpdate = true;
					break;
				}
			}

			if (!existsInUpdate) {
				utilitiesToMove.add(existingUtilities);
			}

		}

		// xóa những đối tượng utilities muốn bỏ
		existingTour.getTourUtilities().removeAll(utilitiesToMove);
		for (TourUtilities tourUtilities : utilitiesToMove) {
			tourUtilitiesRepository.delete(tourUtilities);
		}

		// cap nhat ds utilities
		for (UtilitiesDTO utilitiesDTO : updateTourDTO.getUtilities()) {
			boolean exist = false;
			for (TourUtilities existingUtility : existingTour.getTourUtilities()) {
				if (utilitiesDTO.getUtilityId().equals(existingUtility.getUtilities().getUtilityID())) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				TourUtilities newUtilities = new TourUtilities();
				newUtilities.setTour(existingTour);
				Optional<Utilities> uti = utilitiesRepository.findById(utilitiesDTO.getUtilityId());
				newUtilities.setUtilities(uti.get());
				existingTour.getTourUtilities().add(newUtilities);
			}
		}

		//
		// Check rule
		for (TourRule existingRule : existingTour.getTourRules()) {

			boolean existsInUpdate = false;

			for (RuleDTO ruleDTO : updateTourDTO.getRules()) {
				if (ruleDTO.getRuleId().equals(existingRule.getRule().getRuleID())) {
					existsInUpdate = true;
					break;
				}
			}

			if (!existsInUpdate) {
				rulesToMove.add(existingRule);
			}

		}

		// xóa những đối tượng rule muốn bỏ
		existingTour.getTourRules().removeAll(rulesToMove);
		for (TourRule tourRule : rulesToMove) {
			tourRuleReposiitory.delete(tourRule);
		}

		// cap nhat ds rule
		for (RuleDTO ruleDTO : updateTourDTO.getRules()) {
			boolean exist = false;
			for (TourRule existingRule : existingTour.getTourRules()) {
				if (ruleDTO.getRuleId().equals(existingRule.getRule().getRuleID())) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				TourRule newTourRule = new TourRule();
				newTourRule.setTour(existingTour);
				Optional<Rule> rule = ruleRepository.findById(ruleDTO.getRuleId());
				newTourRule.setRule(rule.get());
				existingTour.getTourRules().add(newTourRule);
			}
		}

	}

	// end check update

	@Override
	public void remove(UUID id) {

		if (tourRepository.existsById(id)) {
			tourRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Không tìm thấy tour với id: " + id);
		}

	}

}
