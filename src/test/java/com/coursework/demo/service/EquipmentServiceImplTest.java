package com.coursework.demo.service;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.exception.BookKeepingException;
import com.coursework.demo.exception.EntityNotFoundException;
import com.coursework.demo.exception.EntityWithQuantityException;
import com.coursework.demo.repository.EquipmentRepository;
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

import static com.coursework.demo.TestData.getEquipment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EquipmentServiceImplTest {

    @MockBean
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentService equipmentService;

    @Test
    public void testGetById() {
        final Equipment equipment = getEquipment(50L);

        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.of(equipment));

        final Equipment result = equipmentService.getById(1L);

        assertEquals(equipment, result);
        verify(equipmentRepository).findById(anyLong());
    }

    @Test
    public void testSaveEquipmentExists() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment dbEquipment = getEquipment(50L, building);
        final Equipment equipment = getEquipment(10L, building);
        final Equipment expectedEquipment = getEquipment(60L, building);

        when(equipmentRepository.countEquipmentWithName(equipment.getName())).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenReturn(Optional.of(dbEquipment));
        when(equipmentRepository.save(expectedEquipment)).thenReturn(expectedEquipment);

        final Equipment result = equipmentService.save(equipment, Bookkeeping.EXPENSES);

        assertEquals(expectedEquipment, result);
        verify(equipmentRepository).countEquipmentWithName(equipment.getName());
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository).save(expectedEquipment);
    }

    @Test
    public void testSaveExpenses() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment equipment = getEquipment(10L, building);
        final Equipment expectedEquipment = getEquipment(10L, building);

        when(equipmentRepository.countEquipmentWithName(equipment.getName())).thenReturn(0L);
        when(equipmentRepository.save(expectedEquipment)).thenReturn(expectedEquipment);

        final Equipment result = equipmentService.save(equipment, Bookkeeping.EXPENSES);

        assertEquals(expectedEquipment, result);
        verify(equipmentRepository).countEquipmentWithName(equipment.getName());
        verify(equipmentRepository, never()).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository).save(expectedEquipment);
    }

    @Test
    public void testSaveExpectedBookKeepingException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment equipment = getEquipment(10L, building);

        when(equipmentRepository.countEquipmentWithName(equipment.getName())).thenReturn(0L);

        assertThrows(BookKeepingException.class, () -> equipmentService.save(equipment, Bookkeeping.INCOME));

        verify(equipmentRepository).countEquipmentWithName(equipment.getName());
        verify(equipmentRepository, never()).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    public void testUpdateExpenses() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment dbEquipment = getEquipment(50L, building);
        final Equipment equipment = getEquipment(10L, building);
        final Equipment expectedEquipment = getEquipment(60L, building);

        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenReturn(Optional.of(dbEquipment));
        when(equipmentRepository.save(expectedEquipment)).thenReturn(expectedEquipment);

        final Equipment result = equipmentService.update(equipment, Bookkeeping.EXPENSES);

        assertEquals(expectedEquipment, result);
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository).save(expectedEquipment);
    }

    @Test
    public void testUpdateIncomeExpectedEntityWithQuantityException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment dbEquipment = getEquipment(10L, building);
        final Equipment equipment = getEquipment(50L, building);

        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenReturn(Optional.of(dbEquipment));

        assertThrows(EntityWithQuantityException.class, () -> equipmentService.update(equipment, Bookkeeping.INCOME));
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    public void testUpdateIncomeDeleteRecord() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment dbEquipment = getEquipment(50L, building);
        final Equipment equipment = getEquipment(50L, building);

        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenReturn(Optional.of(dbEquipment));
        doNothing().when(equipmentRepository).delete(dbEquipment);

        final Equipment result = equipmentService.update(equipment, Bookkeeping.INCOME);

        assertEquals(dbEquipment, result);
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository, never()).save(any(Equipment.class));
        verify(equipmentRepository).delete(dbEquipment);
    }

    @Test
    public void testUpdateIncome() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment dbEquipment = getEquipment(60L, building);
        final Equipment equipment = getEquipment(50L, building);
        final Equipment expectedEquipment = getEquipment(10L, building);

        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenReturn(Optional.of(dbEquipment));
        when(equipmentRepository.save(expectedEquipment)).thenReturn(expectedEquipment);

        final Equipment result = equipmentService.update(equipment, Bookkeeping.INCOME);

        assertEquals(expectedEquipment, result);
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository).save(expectedEquipment);
    }

    @Test
    public void testUpdateExpectedEntityNotFoundException() {
        final Building building = Building.builder()
                .name("Ozzy")
                .build();
        final Equipment equipment = getEquipment(60L, building);

        when(equipmentRepository.findEquipmentByNameAndBuildingName(equipment.getName(), building.getName()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> equipmentService.update(equipment, Bookkeeping.INCOME));
        verify(equipmentRepository).findEquipmentByNameAndBuildingName(equipment.getName(), building.getName());
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }


    @Test
    public void testGetAll() {
        final Equipment equipment = getEquipment(50L);
        final List<Equipment> equipmentList = Collections.singletonList(equipment);
        final Pageable pageable = PageRequest.of(0, 5);
        final Page<Equipment> equipments = new PageImpl<>(equipmentList, pageable, 5);

        when(equipmentRepository.findAll(pageable)).thenReturn(equipments);

        final List<Equipment> result = equipmentService.getAll(pageable);

        assertEquals(equipmentList, result);
        verify(equipmentRepository).findAll(pageable);
    }

    @Test
    public void testDelete() {
        final Equipment equipment = getEquipment(50L);

        doNothing().when(equipmentRepository).delete(equipment);

        final Equipment result = equipmentService.delete(equipment);

        assertEquals(equipment, result);
        verify(equipmentRepository).delete(equipment);
    }
}
