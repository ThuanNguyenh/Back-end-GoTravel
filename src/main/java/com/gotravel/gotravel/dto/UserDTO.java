package com.gotravel.gotravel.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private UUID userId;

	@NotEmpty(message = "Vui lòng điền tên đăng nhập")
	private String userName;

	@NotEmpty(message = "Vui lòng điền email")
	@Email(message = "Email không hợp lệ")
	private String email;

	private String password;

	@NotEmpty(message = "Vui lòng điền số điện thoại")
	@Pattern(regexp = "^(0[35789])([0-9]{8})$", message = "Số điện thoại không hợp lệ")
	private String phone;

	private String province;
	private String district;
	private String ward;
	private String avatar;
	private List<String> roles;

}
