package com.gotravel.gotravel.auth;

import com.gotravel.gotravel.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

	@NotEmpty(message = "Vui lòng điền tên đăng nhập")
	private String username;

	@NotEmpty(message = "Vui lòng điền email")
	@Email(message = "Email không hợp lệ")
	private String email;

	@NotEmpty(message = "Vui lòng nhập password")
	@Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 ký tự")
	private String password;

	private Role role;

}
