package com.gotravel.gotravel.auth;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	
	private String status;
	private String message;
	private Map<String, String> errors;

}
