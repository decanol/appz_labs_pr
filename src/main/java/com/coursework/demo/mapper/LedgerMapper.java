package com.coursework.demo.mapper;

import com.coursework.demo.dto.AddLedgerDTO;
import com.coursework.demo.dto.LedgerDTO;
import com.coursework.demo.entity.Ledger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface LedgerMapper {

    LedgerDTO convertToDto(Ledger ledger);

    Ledger convertToEntity(LedgerDTO ledgerDTO);

    @Mapping(target = "dueTime", expression = "java(LocalDateTime.now())")
    Ledger convertToEntity(AddLedgerDTO ledgerDTO);

    List<LedgerDTO> convertToDtoList(List<Ledger> ledgers);

}