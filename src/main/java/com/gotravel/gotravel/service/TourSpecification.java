package com.gotravel.gotravel.service;

import org.springframework.data.jpa.domain.Specification;
import com.gotravel.gotravel.entity.Tour;

public class TourSpecification {

    public static Specification<Tour> hasStatus(boolean status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    // Các phương thức lọc khác có thể thêm vào đây
}
