package com.gotravel.gotravel.dto;

import java.util.UUID;


import lombok.Data;

@Data
public class ImageDTO {
	private UUID imageId;
	private String url;
	private UUID tourId;
}
