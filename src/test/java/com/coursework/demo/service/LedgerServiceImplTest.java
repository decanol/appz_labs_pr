package com.coursework.demo.service;

import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.Ledger;
import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.entity.enums.ProcurementType;
import com.coursework.demo.repository.LedgerRepository;
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

import static com.coursework.demo.TestData.getBuilding;
import static com.coursework.demo.TestData.getEquipment;
import static com.coursework.demo.TestData.getLedger;
import static com.coursework.demo.TestData.getWarehouse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LedgerServiceImplTest {

    @MockBean
    private LedgerRepository ledgerRepository;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private EquipmentService equipmentService;

    @Autowired
    private LedgerService ledgerService;

    @Test
    public void testGetById() {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES);

        when(ledgerRepository.findById(anyLong())).thenReturn(Optional.of(ledger));

        final Ledger result = ledgerService.getById(1L);

        assertEquals(ledger, result);
        verify(ledgerRepository).findById(anyLong());
    }

    @Test
    public void testGetAll() {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES);
        final List<Ledger> ledgerList = Collections.singletonList(ledger);
        final Pageable pageable = PageRequest.of(0, 5);
        final Page<Ledger> ledgers = new PageImpl<>(ledgerList, pageable, 5);

        when(ledgerRepository.findAll(pageable)).thenReturn(ledgers);

        final List<Ledger> result = ledgerService.getAll(pageable);

        assertEquals(ledgerList, result);
        verify(ledgerRepository).findAll(pageable);
    }

    @Test
    public void testSaveEquipment() {
        final Ledger ledger = getLedger("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(10L, getBuilding());

        when(equipmentService.save(equipment, ledger.getBookkeeping())).thenReturn(equipment);
        when(ledgerRepository.save(ledger)).thenReturn(ledger);

        final Ledger result = ledgerService.save(ledger);

        assertEquals(ledger, result);
        verify(equipmentService).save(equipment, ledger.getBookkeeping());
        verify(warehouseService, never()).save(any(Warehouse.class), eq(ledger.getBookkeeping()));
        verify(ledgerRepository).save(ledger);
    }

    @Test
    public void testSaveWarehouse() {
        final Ledger ledger = getLedger("apple",Bookkeeping.EXPENSES, ProcurementType.WAREHOUSE);
        final Warehouse warehouse = getWarehouse(10L, getBuilding());

        when(warehouseService.save(warehouse, ledger.getBookkeeping())).thenReturn(warehouse);
        when(ledgerRepository.save(ledger)).thenReturn(ledger);

        final Ledger result = ledgerService.save(ledger);

        assertEquals(ledger, result);
        verify(equipmentService, never()).save(any(Equipment.class), eq(ledger.getBookkeeping()));
        verify(warehouseService).save(warehouse, ledger.getBookkeeping());
        verify(ledgerRepository).save(ledger);
    }

    @Test
    public void testSavePersonal() {
        final Ledger ledger = getLedger("apple",Bookkeeping.EXPENSES, ProcurementType.PERSONAL);
        when(ledgerRepository.save(ledger)).thenReturn(ledger);

        final Ledger result = ledgerService.save(ledger);

        assertEquals(ledger, result);
        verify(equipmentService, never()).save(any(Equipment.class), eq(ledger.getBookkeeping()));
        verify(warehouseService, never()).save(any(Warehouse.class), eq(ledger.getBookkeeping()));
        verify(ledgerRepository).save(ledger);
    }

    @Test
    public void testDelete() {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES);

        doNothing().when(ledgerRepository).delete(ledger);

        final Ledger result = ledgerService.delete(ledger);

        assertEquals(ledger, result);
        verify(ledgerRepository).delete(ledger);
    }

    @Test
    public void testGetExpensesName() {
        final String buildingName = "buildingName";
        final List<ExpensesDTO> expensesDTOList =
                Collections.singletonList(new ExpensesDTO(ProcurementType.WAREHOUSE, 50L));
        when(ledgerRepository.findExpensesName(buildingName)).thenReturn(expensesDTOList);

        final List<ExpensesDTO> result = ledgerService.getExpensesName(buildingName);

        assertEquals(expensesDTOList, result);
        verify(ledgerRepository).findExpensesName(buildingName);
    }
}
