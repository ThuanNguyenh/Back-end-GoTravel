package com.gotravel.gotravel.dto;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gotravel.gotravel.entity.TourRule;

import lombok.Data;

@Data
public class TourDTO {
	private UUID tourId;
	private String tourName;
	private String description;
	private String thumbnail;
	private String province;
	private String district;
	private String ward;
	private String detailAddress;
	private Float price;
	private boolean isNull;
	private int numGuest;
	private int discount;
	private Date startDate;
	private Date endDate;
	private Date createAt;
	private UserDTO owner;
	private List<ImageDTO> images;
	private List<CategoryDTO> categories;
	private List<UtilitiesDTO> utilities;
	private List<RuleDTO> rules;
	private List<ScheduleDTO> schedules;
}
