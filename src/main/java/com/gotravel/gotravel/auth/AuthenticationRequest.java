package com.gotravel.gotravel.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
	
	@NotBlank(message = "Vui lòng nhập email")
	private String email;
	
	@NotBlank(message = "Vui lòng nhập mật khẩu")
	private String password;

}
