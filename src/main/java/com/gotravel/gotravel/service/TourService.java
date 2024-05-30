package com.gotravel.gotravel.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.ScheduleConverter;
import com.gotravel.gotravel.converter.ScheduleDetailConverter;
import com.gotravel.gotravel.converter.TourConverter;
import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.dto.ImageDTO;
import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.dto.ScheduleDTO;
import com.gotravel.gotravel.dto.ScheduleDetailDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.entity.Category;
import com.gotravel.gotravel.entity.Image;
import com.gotravel.gotravel.entity.Rule;
import com.gotravel.gotravel.entity.Schedule;
import com.gotravel.gotravel.entity.ScheduleDetail;
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

	// lấy ra danh sách tất cả các tour của userId
	@Override
	public List<TourDTO> getAllTourOfUser(UUID userId) {

		List<Tour> tours = tourRepository.findAllTourByUserId(userId);

		return tours.stream().map(tourConverter::toDTO).collect(Collectors.toList());

	}
	
	@Override
	public long countToursByUserId(UUID userId) {
	    // Sử dụng phương thức đã có để lấy tất cả tour của user
	    List<Tour> tours = tourRepository.findAllTourByUserId(userId);
	    // Trả về số lượng tour
	    return tours.size();
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

			Schedule schedule = new Schedule();
			schedule.setDate(s.getDate());
			schedule.setTour(tour);

			List<ScheduleDetail> activities = new ArrayList<>();
			for (ScheduleDetailDTO ac : s.getActivities()) {
				ScheduleDetail activity = new ScheduleDetail();
				activity.setContext(ac.getContext());
				activity.setSchedule(schedule);
				activities.add(activity);
			}
			schedule.setActivities(activities);
			schedules.add(schedule);

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

		LocalDateTime today = LocalDateTime.now();
		tour.setCreate_at(today);
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

			existingTour.setTourName(tourUpdate.getTourName());
			existingTour.setDescription(tourUpdate.getDescription());
			existingTour.setThumbnail(tourUpdate.getThumbnail());
			existingTour.setProvince(tourUpdate.getProvince());
			existingTour.setDistrict(tourUpdate.getDistrict());
			existingTour.setWard(tourUpdate.getWard());
			existingTour.setDetailAddress(tourUpdate.getDetailAddress());
			existingTour.setPriceAdult(tourUpdate.getPriceAdult());
			existingTour.setPriceChildren(tourUpdate.getPriceChildren());
			existingTour.setNumguest(tourUpdate.getNumguest());
			existingTour.setDiscount(tourUpdate.getDiscount());
			existingTour.setTourTime(tourUpdate.getTourTime());

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
		List<Schedule> schedulesToRemove = new ArrayList<>();

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

		// check update

		for (Schedule existingSchedule : existingTour.getSchedules()) {
			boolean existsInUpdate = updateTourDTO.getSchedules().stream()
					.anyMatch(scheduleDTO -> scheduleDTO.getDate() == existingSchedule.getDate()
							&& scheduleDTO.getActivities().size() == existingSchedule.getActivities().size()
							&& scheduleDTO.getActivities().stream()
									.allMatch(activityDTO -> existingSchedule.getActivities().stream().anyMatch(
											activity -> activity.getContext().equals(activityDTO.getContext()))));

			if (!existsInUpdate) {
				scheduleRepository.delete(existingSchedule);
				schedulesToRemove.add(existingSchedule);
			}
		}
		existingTour.getSchedules().removeAll(schedulesToRemove);

		for (ScheduleDTO scheduleDTO : updateTourDTO.getSchedules()) {
			boolean exist = existingTour.getSchedules().stream()
					.anyMatch(existingSchedule -> scheduleDTO.getDate() == existingSchedule.getDate()
							&& scheduleDTO.getActivities().size() == existingSchedule.getActivities().size()
							&& scheduleDTO.getActivities().stream()
									.allMatch(activityDTO -> existingSchedule.getActivities().stream().anyMatch(
											activity -> activity.getContext().equals(activityDTO.getContext()))));

			if (!exist) {
				Schedule newSchedule = new Schedule();
				newSchedule.setTour(existingTour);
				newSchedule.setDate(scheduleDTO.getDate());

				List<ScheduleDetail> scheduleDetails = scheduleDTO.getActivities().stream().map(activityDTO -> {
					ScheduleDetail scheduleDetail = new ScheduleDetail();
					scheduleDetail.setContext(activityDTO.getContext());
					scheduleDetail.setSchedule(newSchedule);
					return scheduleDetail;
				}).collect(Collectors.toList());

				newSchedule.setActivities(scheduleDetails);
				existingTour.getSchedules().add(newSchedule);
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
