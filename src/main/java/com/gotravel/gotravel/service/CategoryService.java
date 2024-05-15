package com.gotravel.gotravel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.CategoryConverter;
import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.entity.Category;
import com.gotravel.gotravel.repository.CategoryRepository;
import com.gotravel.gotravel.service.impl.ICategoryService;

@Service
public class CategoryService implements ICategoryService{
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CategoryConverter categoryConverter;

	@Override
	public List<CategoryDTO> findAll() {
		
		List<Category> allCategory = categoryRepository.findAll();
		
		List<CategoryDTO> categoryDTOs = new ArrayList<>();
		
		for (Category c : allCategory) {
			CategoryDTO categoryDTO = categoryConverter.toDTO(c);
			categoryDTOs.add(categoryDTO);
		}
		
		return categoryDTOs;
	}

	@Override
	public CategoryDTO findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CategoryDTO save(CategoryDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CategoryDTO update(UUID id, CategoryDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub
		
	}

}
