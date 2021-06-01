package com.coursework.demo.service.impl;

import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.exception.BookKeepingException;
import com.coursework.demo.exception.EntityNotFoundException;
import com.coursework.demo.exception.EntityWithQuantityException;
import com.coursework.demo.repository.WarehouseRepository;
import com.coursework.demo.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Transactional
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id).get();
    }

    @Override
    public List<Warehouse> getAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable).getContent();
    }

    /**
     * Method updates information for an existing warehouse in  Repository
     *
     * @param object Warehouse entity with info to be updated
     * @return updated Warehouse entity
     */
    @Override
    public Warehouse update(Warehouse object, Bookkeeping bookkeeping) {
        log.info("In update(entity = [{}]", object);
        Warehouse dbWarehouse = getWarehouseByName(object.getName(), object.getBuilding().getName());

        if (bookkeeping == Bookkeeping.EXPENSES) {
            dbWarehouse = changeWarehouseQuantity(dbWarehouse, dbWarehouse.getQuantity() + object.getQuantity());
        } else if (bookkeeping == Bookkeeping.INCOME) {
            if (dbWarehouse.getQuantity() < object.getQuantity()) {
                throw new EntityWithQuantityException("you are trying to sell more than you have at warehouse");
            } else if (dbWarehouse.getQuantity().equals(object.getQuantity())) {
                dbWarehouse = delete(dbWarehouse);
            } else {
                dbWarehouse = changeWarehouseQuantity(dbWarehouse, dbWarehouse.getQuantity() - object.getQuantity());
            }
        }

        return dbWarehouse;
    }

    @Override
    public Warehouse save(Warehouse object, Bookkeeping bookkeeping) {
        log.info("In save(entity = [{}]", object);
        if (isWarehouseExistsWithTitle(object.getName())) {
            return update(object, bookkeeping);
        } else if (bookkeeping == Bookkeeping.EXPENSES) {
            return warehouseRepository.save(object);
        } else {
            throw new BookKeepingException("There is no such item in warehouse");
        }
    }

    @Override
    public boolean isWarehouseExistsWithTitle(String title) {
        return warehouseRepository.countWarehouseWithName(title) != 0;
    }

    /**
     * Method finds Warehouse by name
     *
     * @param warehouseName
     * @return warehouse
     */
    @Override
    public Warehouse getWarehouseByName(String warehouseName, String buildingName) {
        return warehouseRepository.findWarehouseByNameAndBuildingName(warehouseName, buildingName).orElseThrow(
                () -> new EntityNotFoundException(Warehouse.class, "warehouseName", warehouseName)
        );
    }

    @Override
    public Warehouse delete(Warehouse object) {
        warehouseRepository.delete(object);
        return object;
    }

    private Warehouse changeWarehouseQuantity(Warehouse dbWarehouse, long l) {
        dbWarehouse.setQuantity(l);
        dbWarehouse = warehouseRepository.save(dbWarehouse);
        return dbWarehouse;
    }
}
