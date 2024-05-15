package com.gotravel.gotravel.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.dto.RuleDTO;
import com.gotravel.gotravel.dto.UtilitiesDTO;
import com.gotravel.gotravel.service.impl.ICategoryService;
import com.gotravel.gotravel.service.impl.IRuleService;
import com.gotravel.gotravel.service.impl.IUtilityService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/directory", produces = "application/json")
public class DirectoryApi {

	@Autowired
	private ICategoryService categoryService;
	
	@Autowired
	private IUtilityService utilityService;
	
	@Autowired
	private IRuleService ruleService;

	@GetMapping("/categories")
	public ResponseEntity<?> getAllCategory() {
		List<CategoryDTO> categories = categoryService.findAll();

		if (!categories.isEmpty()) {
			return new ResponseEntity<>(categories, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);

		}
	}
	
	@GetMapping("/utilities")
	public ResponseEntity<?> getAllUtility() {
		List<UtilitiesDTO> utilities = utilityService.findAll();

		if (!utilities.isEmpty()) {
			return new ResponseEntity<>(utilities, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("/rules")
	public ResponseEntity<?> getAllRule() {
		List<RuleDTO> rules = ruleService.findAll();

		if (!rules.isEmpty()) {
			return new ResponseEntity<>(rules, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Dữ liệu không tồn tại.", HttpStatus.NOT_FOUND);

		}
	}
	
}
