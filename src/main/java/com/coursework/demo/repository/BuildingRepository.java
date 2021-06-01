package com.coursework.demo.repository;

import com.coursework.demo.entity.Building;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BuildingRepository extends PagingAndSortingRepository<Building, Long> {
}
