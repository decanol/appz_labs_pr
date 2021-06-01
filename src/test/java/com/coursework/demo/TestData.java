package com.coursework.demo;

import com.coursework.demo.dto.BuildingDTO;
import com.coursework.demo.dto.EmployeeDTO;
import com.coursework.demo.dto.EquipmentDTO;
import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.dto.LedgerDTO;
import com.coursework.demo.dto.WarehouseDTO;
import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.Employee;
import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.Ledger;
import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.entity.enums.ProcurementType;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class TestData {

    public static Building getBuilding() {
        return Building.builder()
                .id(1L)
                .name("buildName")
                .geolocation("street")
                .build();
    }

    public static BuildingDTO getBuildingRequest() {
        return BuildingDTO.builder()
                .id(1L)
                .name("buildName")
                .geolocation("street")
                .build();
    }

    public static Employee getEmployee() {
        return Employee.builder()
                .id(1L)
                .name("John")
                .email("email")
                .salary(500L)
                .build();
    }

    public static EmployeeDTO getEmployeeRequest() {
        return EmployeeDTO.builder()
                .id(1L)
                .name("John")
                .email("email")
                .salary(500L)
                .build();
    }

    public static Equipment getEquipment(Long quantity, Building building) {
        return Equipment.builder()
                .name("laptop")
                .quantity(quantity)
                .building(building)
                .price(50L)
                .build();
    }

    public static Equipment getEquipment(Long quantity) {
        return getEquipment(quantity, null);
    }

    public static EquipmentDTO getEquipmentRequest(Long quantity, Building building) {
        return EquipmentDTO.builder()
                .name("laptop")
                .quantity(quantity)
                .building(building)
                .price(50L)
                .build();
    }

    public static EquipmentDTO getEquipmentRequest(Long quantity) {
        return getEquipmentRequest(quantity, null);
    }

    public static Warehouse getWarehouse(Long quantity, Building building) {
        return Warehouse.builder()
                .name("apple")
                .quantity(quantity)
                .building(building)
                .build();
    }

    public static Warehouse getWarehouse(Long quantity) {
        return getWarehouse(quantity, null);
    }

    public static WarehouseDTO getWarehouseRequest(Long quantity, Building building) {
        return WarehouseDTO.builder()
                .name("apple")
                .quantity(quantity)
                .building(building)
                .build();
    }

    public static WarehouseDTO getWarehouseRequest(Long quantity) {
        return getWarehouseRequest(quantity, null);
    }

    public static Ledger getLedger(String name, Bookkeeping bookkeeping) {
        return getLedger(name, bookkeeping, null);
    }

    public static Ledger getLedger(String name, Bookkeeping bookkeeping, ProcurementType procurementType) {
        return Ledger.builder()
                .id(1L)
                .name(name)
                .bookkeeping(bookkeeping)
                .procurementType(procurementType)
                .quantity(10L)
                .price(50L)
                .building(getBuilding())
                .build();
    }

    public static LedgerDTO getLedgerRequest(String name, Bookkeeping bookkeeping) {
        return getLedgerRequest(name, bookkeeping, null);
    }

    public static LedgerDTO getLedgerRequest(String name, Bookkeeping bookkeeping, ProcurementType procurementType) {
        return LedgerDTO.builder()
                .id(1L)
                .name(name)
                .bookkeeping(bookkeeping)
                .procurementType(procurementType)
                .quantity(10L)
                .price(50L)
                .building(getBuilding())
                .build();
    }

    public static List<ExpensesDTO> getExpectedExpensesList() {
        return Arrays.asList(
                new ExpensesDTO(ProcurementType.PERSONAL, 25000L),
                new ExpensesDTO(ProcurementType.WAREHOUSE, 800L),
                new ExpensesDTO(ProcurementType.EQUIPMENT, 10000L),
                new ExpensesDTO(ProcurementType.BILLS, 300L)
        );
    }
}
