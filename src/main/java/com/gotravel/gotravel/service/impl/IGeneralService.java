package com.gotravel.gotravel.service.impl;

import java.util.List;
import java.util.UUID;

public interface IGeneralService<T> {
	
	List<T> findAll();
	
	T findById(UUID id);
	
	T save(T t);
	
	T update(UUID id, T t);
	
	void remove(UUID id);

}
