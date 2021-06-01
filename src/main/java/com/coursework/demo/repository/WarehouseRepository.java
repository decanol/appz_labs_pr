package com.coursework.demo.repository;

import com.coursework.demo.entity.Warehouse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface WarehouseRepository extends PagingAndSortingRepository<Warehouse, Long> {
    @Query("SELECT w FROM Warehouse w " +
           "JOIN w.building b " +
           "WHERE w.name = :warehouseName AND b.name = :buildingName")
    Optional<Warehouse> findWarehouseByNameAndBuildingName(String warehouseName, String buildingName);

    @Query("SELECT COUNT (w) FROM Warehouse w WHERE w.name = :name")
    Long countWarehouseWithName(String name);
}
