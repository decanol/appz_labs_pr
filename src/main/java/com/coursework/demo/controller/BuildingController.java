package com.coursework.demo.controller;

import com.coursework.demo.dto.AddBuildingDTO;
import com.coursework.demo.dto.BuildingDTO;
import com.coursework.demo.entity.Building;
import com.coursework.demo.mapper.BuildingMapper;
import com.coursework.demo.service.BuildingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "Building API")
@RequestMapping("/v1/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;

    @Autowired
    public BuildingController(BuildingService buildingService, BuildingMapper buildingMapper) {
        this.buildingService = buildingService;
        this.buildingMapper = buildingMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER')")
    @ApiOperation(value = "Get building info by id")
    public ResponseEntity<BuildingDTO> get(@PathVariable("id") long id) {
        Building building = buildingService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(buildingMapper.convertToDto(building));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Get the list of all buildings")
    public ResponseEntity<List<BuildingDTO>> getPage(@PageableDefault(sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok().body(buildingMapper.convertToDtoList(buildingService.getAll(pageable)));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN, OWNER')")
    @ApiOperation(value = "Create new building")
    public ResponseEntity<BuildingDTO> save(@RequestBody AddBuildingDTO addBuildingDTO) {
        Building building = buildingService.save(buildingMapper.convertToEntity(addBuildingDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingMapper.convertToDto(building));

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER')")
    @ApiOperation(value = "Update existing building by id")
    public ResponseEntity<BuildingDTO> update(@PathVariable("id") long id, @RequestBody BuildingDTO buildingDTO) {
        if (id == buildingDTO.getId()) {
            Building building = buildingService.save(buildingMapper.convertToEntity(buildingDTO));
            return ResponseEntity.status(HttpStatus.OK).body(buildingMapper.convertToDto(building));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER')")
    @ApiOperation(value = "Delete building by id")
    public ResponseEntity delete(@PathVariable("id") long id) {
        Building building = buildingService.getById(id);
        buildingService.delete(building);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
