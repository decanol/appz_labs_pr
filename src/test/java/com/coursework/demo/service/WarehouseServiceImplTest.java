package com.coursework.demo.service;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.exception.BookKeepingException;
import com.coursework.demo.exception.EntityNotFoundException;
import com.coursework.demo.exception.EntityWithQuantityException;
import com.coursework.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getWarehouse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WarehouseServiceImplTest {

    @MockBean
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseService warehouseService;

    @Test
    public void testGetById() {
        final Warehouse warehouse = getWarehouse(50L);

        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(warehouse));

        final Warehouse result = warehouseService.getById(1L);

        assertEquals(warehouse, result);
        verify(warehouseRepository).findById(anyLong());
    }

    @Test
    public void testSaveWarehouseExists() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse dbWarehouse = getWarehouse(50L, building);
        final Warehouse warehouse = getWarehouse(10L, building);
        final Warehouse expectedWarehouse = getWarehouse(60L, building);

        when(warehouseRepository.countWarehouseWithName(warehouse.getName())).thenReturn(1L);
        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenReturn(Optional.of(dbWarehouse));
        when(warehouseRepository.save(expectedWarehouse)).thenReturn(expectedWarehouse);

        final Warehouse result = warehouseService.save(warehouse, Bookkeeping.EXPENSES);

        assertEquals(expectedWarehouse, result);
        verify(warehouseRepository).countWarehouseWithName(warehouse.getName());
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository).save(expectedWarehouse);
    }

    @Test
    public void testSaveExpenses() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse warehouse = getWarehouse(10L, building);
        final Warehouse expectedWarehouse = getWarehouse(10L, building);

        when(warehouseRepository.countWarehouseWithName(warehouse.getName())).thenReturn(0L);
        when(warehouseRepository.save(expectedWarehouse)).thenReturn(expectedWarehouse);

        final Warehouse result = warehouseService.save(warehouse, Bookkeeping.EXPENSES);

        assertEquals(expectedWarehouse, result);
        verify(warehouseRepository).countWarehouseWithName(warehouse.getName());
        verify(warehouseRepository, never()).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository).save(expectedWarehouse);
    }

    @Test
    public void testSaveExpectedBookKeepingException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse warehouse = getWarehouse(10L, building);

        when(warehouseRepository.countWarehouseWithName(warehouse.getName())).thenReturn(0L);

        assertThrows(BookKeepingException.class, () -> warehouseService.save(warehouse, Bookkeeping.INCOME));

        verify(warehouseRepository).countWarehouseWithName(warehouse.getName());
        verify(warehouseRepository, never()).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    public void testUpdateExpenses() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse dbWarehouse = getWarehouse(50L, building);
        final Warehouse warehouse = getWarehouse(10L, building);
        final Warehouse expectedWarehouse = getWarehouse(60L, building);

        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenReturn(Optional.of(dbWarehouse));
        when(warehouseRepository.save(expectedWarehouse)).thenReturn(expectedWarehouse);

        final Warehouse result = warehouseService.update(warehouse, Bookkeeping.EXPENSES);

        assertEquals(expectedWarehouse, result);
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository).save(expectedWarehouse);
    }

    @Test
    public void testUpdateIncomeExpectedEntityWithQuantityException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse dbWarehouse = getWarehouse(10L, building);
        final Warehouse warehouse = getWarehouse(50L, building);

        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenReturn(Optional.of(dbWarehouse));

        assertThrows(EntityWithQuantityException.class, () -> warehouseService.update(warehouse, Bookkeeping.INCOME));
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    public void testUpdateIncomeDeleteRecord() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse dbWarehouse = getWarehouse(50L, building);
        final Warehouse warehouse = getWarehouse(50L, building);

        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenReturn(Optional.of(dbWarehouse));
        doNothing().when(warehouseRepository).delete(dbWarehouse);

        final Warehouse result = warehouseService.update(warehouse, Bookkeeping.INCOME);

        assertEquals(dbWarehouse, result);
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
        verify(warehouseRepository).delete(dbWarehouse);
    }

    @Test
    public void testUpdateIncome() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse dbWarehouse = getWarehouse(60L, building);
        final Warehouse warehouse = getWarehouse(50L, building);
        final Warehouse expectedWarehouse = getWarehouse(10L, building);

        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenReturn(Optional.of(dbWarehouse));
        when(warehouseRepository.save(expectedWarehouse)).thenReturn(expectedWarehouse);

        final Warehouse result = warehouseService.update(warehouse, Bookkeeping.INCOME);

        assertEquals(expectedWarehouse, result);
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository).save(expectedWarehouse);
    }

    @Test
    public void testUpdateExpectedEntityNotFoundException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Warehouse warehouse = getWarehouse(60L, building);

        when(warehouseRepository.findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> warehouseService.update(warehouse, Bookkeeping.INCOME));
        verify(warehouseRepository).findWarehouseByNameAndBuildingName(warehouse.getName(), building.getName());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }


    @Test
    public void testGetAll() {
        final Warehouse warehouse = getWarehouse(50L);
        final List<Warehouse> warehouseList = Collections.singletonList(warehouse);
        final Pageable pageable = PageRequest.of(0, 5);
        final Page<Warehouse> warehouses = new PageImpl<>(warehouseList, pageable, 5);

        when(warehouseRepository.findAll(pageable)).thenReturn(warehouses);

        final List<Warehouse> result = warehouseService.getAll(pageable);

        assertEquals(warehouseList, result);
        verify(warehouseRepository).findAll(pageable);
    }

    @Test
    public void testDelete() {
        final Warehouse warehouse = getWarehouse(50L);

        doNothing().when(warehouseRepository).delete(warehouse);

        final Warehouse result = warehouseService.delete(warehouse);

        assertEquals(warehouse, result);
        verify(warehouseRepository).delete(warehouse);
    }
}
