package com.coursework.demo.mapper;

import com.coursework.demo.dto.AddBuildingDTO;
import com.coursework.demo.dto.BuildingDTO;
import com.coursework.demo.entity.Building;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    BuildingDTO convertToDto(Building building);

    Building convertToEntity(BuildingDTO buildingDTO);

    Building convertToEntity(AddBuildingDTO buildingDTO);

    List<BuildingDTO> convertToDtoList(List<Building> buildings);

}