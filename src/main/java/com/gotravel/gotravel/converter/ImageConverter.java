package com.gotravel.gotravel.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.ImageDTO;
import com.gotravel.gotravel.entity.Image;

@Component
public class ImageConverter {
	public ImageDTO toDTO(Image image) {
		
		ImageDTO imageDTO = new ImageDTO();
		imageDTO.setImageId(image.getImageId());
		imageDTO.setUrl(image.getUrl());
		
		if (image.getTour() != null) {
			imageDTO.setTourId(image.getTour().getTourId());
		}
		
		return imageDTO;
		
	}
	
	public Set<ImageDTO> arrayToDTO(Set<Image> imageList) {
		Set<ImageDTO> imageDTOList = new HashSet<>();
		for (Image image : imageList) {
			imageDTOList.add(toDTO(image));
		}
		Set<ImageDTO> imageSet = new HashSet<>(imageDTOList);
		return imageSet;
	}
	
}
