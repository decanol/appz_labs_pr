package com.coursework.demo.service;

import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    Warehouse getById(Long id);

    List<Warehouse> getAll(Pageable pageable);

    Warehouse save(Warehouse object, Bookkeeping bookkeeping);

    boolean isWarehouseExistsWithTitle(String title);

    Warehouse getWarehouseByName(String warehouseName, String buildingName);

    Warehouse update(Warehouse object, Bookkeeping bookkeeping);

    Warehouse delete(Warehouse object);
}
