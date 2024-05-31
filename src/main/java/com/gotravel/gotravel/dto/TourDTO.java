package com.gotravel.gotravel.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TourDTO {

	private UUID tourId;

	@NotNull(message = "vui lòng điền tên tour")
	private String tourName;

	private String description;

	private String thumbnail;

	@NotNull(message = "vui lòng điền vị trí tỉnh/thành phố")
	private String province;

	@NotNull(message = "vui lòng điền vị trí quận/huyện")
	private String district;

	@NotNull(message = "vui lòng điền vị trí phường/xã")
	private String ward;

	@NotNull(message = "vui lòng điền vị trí cụ thể")
	private String detailAddress;

	@NotNull(message = "vui lòng nhập giá dịch vụ")
	private Float priceAdult;
	
	@NotNull(message = "vui lòng nhập giá dịch vụ")
	private Float priceChildren;

	private Boolean status;

	@NotNull(message = "vui lòng nhập số lượng khách")
	private int numGuest;

	private int discount;

	@NotNull(message = "Vui lòng nhập thời gian của tour")
	private int tourTime;

	private LocalDateTime createAt;

	@NotNull(message = "Không có chủ sở hữu ")
	private UserDTO owner;

	@NotNull(message = "Thiếu hình ảnh")
	private List<ImageDTO> images;

	@NotNull(message = "vui lòng chọn thể loại")
	private List<CategoryDTO> categories;

	private List<UtilitiesDTO> utilities;

	private List<RuleDTO> rules;

	private List<ScheduleDTO> schedules;

	private List<Float> ratings;
}
