package com.coursework.demo.service.impl;

import com.coursework.demo.entity.Building;
import com.coursework.demo.repository.BuildingRepository;
import com.coursework.demo.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building getById(Long id) {
        return buildingRepository.findById(id).get();
    }

    @Override
    public List<Building> getAll(Pageable pageable) {
        return buildingRepository.findAll(pageable).getContent();
    }

    @Override
    public Building save(Building object) {
        return buildingRepository.save(object);
    }

    @Override
    public Building delete(Building object) {
        buildingRepository.delete(object);
        return object;
    }
}
