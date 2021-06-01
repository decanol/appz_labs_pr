package com.coursework.demo.mapper;

import com.coursework.demo.dto.WarehouseDTO;
import com.coursework.demo.entity.Warehouse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseDTO convertToDto(Warehouse warehouse);

    Warehouse convertToEntity(WarehouseDTO warehouseDTO);

    List<WarehouseDTO> convertToDtoList(List<Warehouse> licens);

}
