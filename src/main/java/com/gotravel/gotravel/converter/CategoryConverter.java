package com.gotravel.gotravel.converter;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.entity.Category;

@Component
public class CategoryConverter {
	public CategoryDTO toDTO(Category category) {
		
		CategoryDTO categoryDTO = new CategoryDTO();
		
		categoryDTO.setCategoryId(category.getCategoryId());
		categoryDTO.setCategoryName(category.getCategoryName());
		
		return categoryDTO;
		
	}
}
