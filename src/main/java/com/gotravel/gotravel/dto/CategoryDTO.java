package com.gotravel.gotravel.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CategoryDTO {
	private UUID categoryId;
	private String categoryName;
}
