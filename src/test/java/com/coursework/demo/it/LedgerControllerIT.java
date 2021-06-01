package com.coursework.demo.it;

import com.coursework.demo.dto.LedgerDTO;
import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.Ledger;
import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.entity.enums.ProcurementType;
import com.coursework.demo.exception.BookKeepingException;
import com.coursework.demo.exception.EntityWithQuantityException;
import com.coursework.demo.repository.EquipmentRepository;
import com.coursework.demo.repository.LedgerRepository;
import com.coursework.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getBuilding;
import static com.coursework.demo.TestData.getEquipment;
import static com.coursework.demo.TestData.getExpectedExpensesList;
import static com.coursework.demo.TestData.getLedger;
import static com.coursework.demo.TestData.getLedgerRequest;
import static com.coursework.demo.TestData.getWarehouse;
import static com.coursework.demo.it.TestUtils.asJsonString;
import static com.coursework.demo.it.TestUtils.deleteRequest;
import static com.coursework.demo.it.TestUtils.getRequest;
import static com.coursework.demo.it.TestUtils.postRequest;
import static com.coursework.demo.it.TestUtils.putRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LedgerControllerIT {

    private static final String LEDGER_CONTROLLER_PATH = "/v1/ledgers/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerRepository ledgerRepository;

    @MockBean
    private EquipmentRepository equipmentRepository;

    @MockBean
    private WarehouseRepository warehouseRepository;

    @Test
    @WithMockUser(roles = "OWNER")
    public void testRetrieveLedgerById() throws Exception {
        when(ledgerRepository.findById(anyLong())).thenReturn(Optional.of(getLedger("apple", Bookkeeping.EXPENSES)));

        mockMvc.perform(getRequest(LEDGER_CONTROLLER_PATH + "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getLedgerRequest("apple", Bookkeeping.EXPENSES))));
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testRetrieveExpenses() throws Exception {
        when(ledgerRepository.findExpensesName("Ozzy")).thenReturn(getExpectedExpensesList());

        mockMvc.perform(getRequest(LEDGER_CONTROLLER_PATH + "expenses").param("name", "Ozzy"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getExpectedExpensesList())));
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testRetrieveLedgerList() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES);
        final List<Ledger> ledgers = Collections.singletonList(ledger);
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        final Page<Ledger> ledgerPage = new PageImpl<>(ledgers, pageable, 10);

        when(ledgerRepository.findAll(pageable)).thenReturn(ledgerPage);

        mockMvc.perform(getRequest(LEDGER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Collections.singletonList(getLedgerRequest("apple", Bookkeeping.EXPENSES)))));
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedStoreNewEquipmentInDatabase() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(0L);
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(getEquipment(10L));

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedStoreNewWarehouseInDatabase() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES, ProcurementType.WAREHOUSE);
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.EXPENSES, ProcurementType.WAREHOUSE);

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(0L);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(getWarehouse(10L));

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository).save(any(Warehouse.class));
        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedWarehouseServiceThrowBookKeepingException() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(0L);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookKeepingException))
                .andExpect(result -> assertEquals("There is no such item in warehouse", result.getResolvedException().getMessage()));

        verifyNoInteractions(ledgerRepository);
        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository, never()).save(any(Warehouse.class));
        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedEquipmentServiceThrowBookKeepingException() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(0L);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookKeepingException))
                .andExpect(result -> assertEquals("There is no such item in equipment", result.getResolvedException().getMessage()));

        verifyNoInteractions(ledgerRepository);
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository, never()).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedNothingElseStoreInDatabase() throws Exception {
        final Ledger ledger = getLedger("mansion", Bookkeeping.EXPENSES, ProcurementType.PERSONAL);
        final LedgerDTO request = getLedgerRequest("mansion", Bookkeeping.EXPENSES, ProcurementType.PERSONAL);

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verifyNoInteractions(warehouseRepository);
        verifyNoInteractions(equipmentRepository);
        verify(ledgerRepository).save(any(Ledger.class));
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedExpensesChangeWarehouseQuantity() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES, ProcurementType.WAREHOUSE);
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.EXPENSES, ProcurementType.WAREHOUSE);
        final Warehouse warehouse = getWarehouse(10L, getBuilding());
        final Warehouse storedWarehouse = getWarehouse(20L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(1L);
        when(warehouseRepository.findWarehouseByNameAndBuildingName("apple", "buildName"))
                .thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(storedWarehouse)).thenReturn(storedWarehouse);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository).findWarehouseByNameAndBuildingName("apple", "buildName");
        verify(warehouseRepository).save(any(Warehouse.class));
        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedExpensesChangeEquipmentQuantity() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(10L, getBuilding());
        final Equipment storedEquipment = getEquipment(20L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName("laptop", "buildName"))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(storedEquipment)).thenReturn(storedEquipment);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).findEquipmentByNameAndBuildingName("laptop", "buildName");
        verify(equipmentRepository).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedEquipmentServiceThrowEntityWithQuantityException() throws Exception {
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(5L, getBuilding());

        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName("laptop", "buildName"))
                .thenReturn(Optional.of(equipment));

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityWithQuantityException))
                .andExpect(result -> assertEquals("you ary trying to sell more equipment than you have", result.getResolvedException().getMessage()));

        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).findEquipmentByNameAndBuildingName("laptop", "buildName");
        verify(equipmentRepository, never()).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
        verifyNoInteractions(ledgerRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedWarehouseServiceThrowEntityWithQuantityException() throws Exception {
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final Warehouse warehouse = getWarehouse(5L, getBuilding());

        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(1L);
        when(warehouseRepository.findWarehouseByNameAndBuildingName("apple", "buildName"))
                .thenReturn(Optional.of(warehouse));

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityWithQuantityException))
                .andExpect(result -> assertEquals("you are trying to sell more than you have at warehouse", result.getResolvedException().getMessage()));

        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository).findWarehouseByNameAndBuildingName("apple", "buildName");
        verify(warehouseRepository, never()).save(any(Warehouse.class));
        verifyNoInteractions(equipmentRepository);
        verifyNoInteractions(ledgerRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedDeleteWarehouseRecord() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final Warehouse warehouse = getWarehouse(10L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(1L);
        when(warehouseRepository.findWarehouseByNameAndBuildingName("apple", "buildName"))
                .thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).delete(warehouse);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository).findWarehouseByNameAndBuildingName("apple", "buildName");
        verify(warehouseRepository).delete(warehouse);
        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedDeleteEquipmentRecord() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(10L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName("laptop", "buildName"))
                .thenReturn(Optional.of(equipment));
        doNothing().when(equipmentRepository).delete(equipment);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).findEquipmentByNameAndBuildingName("laptop", "buildName");
        verify(equipmentRepository).delete(equipment);
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedIncomeChangeWarehouseQuantity() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.INCOME, ProcurementType.WAREHOUSE);
        final Warehouse warehouse = getWarehouse(15L, getBuilding());
        final Warehouse storedWarehouse = getWarehouse(5L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(warehouseRepository.countWarehouseWithName("apple")).thenReturn(1L);
        when(warehouseRepository.findWarehouseByNameAndBuildingName("apple", "buildName"))
                .thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(storedWarehouse)).thenReturn(storedWarehouse);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(warehouseRepository).countWarehouseWithName("apple");
        verify(warehouseRepository).findWarehouseByNameAndBuildingName("apple", "buildName");
        verify(warehouseRepository).save(any(Warehouse.class));
        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testSaveLedgerExpectedIncomeChangeEquipmentQuantity() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.INCOME, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(15L, getBuilding());
        final Equipment storedEquipment = getEquipment(5L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName("laptop", "buildName"))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(storedEquipment)).thenReturn(storedEquipment);

        mockMvc.perform(postRequest(LEDGER_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).findEquipmentByNameAndBuildingName("laptop", "buildName");
        verify(equipmentRepository).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testUpdateLedger() throws Exception {
        final Ledger ledger = getLedger("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final LedgerDTO request = getLedgerRequest("laptop", Bookkeeping.EXPENSES, ProcurementType.EQUIPMENT);
        final Equipment equipment = getEquipment(10L, getBuilding());
        final Equipment storedEquipment = getEquipment(20L, getBuilding());

        when(ledgerRepository.save(any(Ledger.class))).thenReturn(ledger);
        when(equipmentRepository.countEquipmentWithName("laptop")).thenReturn(1L);
        when(equipmentRepository.findEquipmentByNameAndBuildingName("laptop", "buildName"))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(storedEquipment)).thenReturn(storedEquipment);

        when(ledgerRepository.save(ledger)).thenReturn(ledger);

        mockMvc.perform(putRequest(LEDGER_CONTROLLER_PATH + "1", request))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(request)));

        verify(ledgerRepository).save(any(Ledger.class));
        verify(equipmentRepository).countEquipmentWithName("laptop");
        verify(equipmentRepository).findEquipmentByNameAndBuildingName("laptop", "buildName");
        verify(equipmentRepository).save(any(Equipment.class));
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testUpdateLedgerExpectedBadRequest() throws Exception {
        final LedgerDTO request = getLedgerRequest("apple", Bookkeeping.EXPENSES);

        mockMvc.perform(putRequest(LEDGER_CONTROLLER_PATH + "2", request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "BOOKKEEPER")
    public void testDeleteLedger() throws Exception {
        final Ledger ledger = getLedger("apple", Bookkeeping.EXPENSES);

        when(ledgerRepository.findById(anyLong())).thenReturn(Optional.of(ledger));
        doNothing().when(ledgerRepository).delete(ledger);

        mockMvc.perform(deleteRequest(LEDGER_CONTROLLER_PATH + "1"))
                .andExpect(status().isNoContent());
    }
}
