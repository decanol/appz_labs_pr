package com.coursework.demo.service;

import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.enums.Bookkeeping;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentService {
    List<Equipment> getAll(Pageable pageable);

    boolean isEquipmentExistsWithTitle(String title);

    Equipment save(Equipment object, Bookkeeping bookkeeping);

    Equipment update(Equipment object, Bookkeeping bookkeeping);

    Equipment getEquipmentByName(String title, String buildingName);

    Equipment delete(Equipment object);

    Equipment getById(Long id);
}