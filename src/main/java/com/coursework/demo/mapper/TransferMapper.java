package com.coursework.demo.mapper;

import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.Ledger;
import com.coursework.demo.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "id", ignore = true)
    Warehouse toWarehouse(Ledger ledger);

    @Mapping(target = "id", ignore = true)
    Equipment toEquipment(Ledger ledger);
}
