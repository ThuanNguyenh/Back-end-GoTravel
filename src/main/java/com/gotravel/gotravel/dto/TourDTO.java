package com.gotravel.gotravel.dto;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gotravel.gotravel.entity.TourRule;

import jakarta.validation.constraints.NotBlank;
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
	private Float price;

	private boolean isNull;

	@NotNull(message = "vui lòng nhập số lượng khách")
	private int numGuest;

	private int discount;

	@NotNull(message = "vui lòng nhập ngày bắt đầu")
	private Date startDate;

	@NotNull(message = "vui lòng nhập ngày kết thúc")
	private Date endDate;

	private Date createAt;

	@NotNull(message = "Không có chủ sở hữu ")
	private UserDTO owner;

	@NotNull(message = "Thiếu hình ảnh")
	private List<ImageDTO> images;

	@NotNull(message = "vui lòng chọn thể loại")
	private List<CategoryDTO> categories;

	private List<UtilitiesDTO> utilities;

	private List<RuleDTO> rules;

	private List<ScheduleDTO> schedules;
}
