package com.coursework.demo.repository;

import com.coursework.demo.entity.Equipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EquipmentRepository extends PagingAndSortingRepository<Equipment, Long> {
    @Query("SELECT e FROM Equipment e " +
           "JOIN e.building b " +
           "WHERE e.name= :equipmentName and b.name = :buildingName and e.employee is null")
    Optional<Equipment> findEquipmentByNameAndBuildingName(String equipmentName, String buildingName);

    @Query("SELECT count (e) FROM Equipment e WHERE e.name = :name")
    Long countEquipmentWithName(String name);
}
