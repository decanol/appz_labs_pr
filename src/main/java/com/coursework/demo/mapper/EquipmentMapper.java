package com.coursework.demo.mapper;

import com.coursework.demo.dto.EquipmentDTO;
import com.coursework.demo.entity.Equipment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    EquipmentDTO convertToDto(Equipment equipment);

    Equipment convertToEntity(EquipmentDTO equipmentDTO);

    List<EquipmentDTO> convertToDtoList(List<Equipment> equipment);

}
