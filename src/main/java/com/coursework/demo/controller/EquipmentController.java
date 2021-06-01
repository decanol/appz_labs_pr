package com.coursework.demo.controller;

import com.coursework.demo.dto.EquipmentDTO;
import com.coursework.demo.entity.Equipment;
import com.coursework.demo.mapper.EquipmentMapper;
import com.coursework.demo.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "Equipment API")
@RequestMapping("/v1/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;

    @Autowired
    public EquipmentController(EquipmentService equipmentService, EquipmentMapper equipmentMapper) {
        this.equipmentService = equipmentService;
        this.equipmentMapper = equipmentMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get equipment info by id")
    public ResponseEntity<EquipmentDTO> get(@PathVariable("id") long id){
        Equipment equipment = equipmentService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(equipmentMapper.convertToDto(equipment));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get the list of all equipments")
    public ResponseEntity<List<EquipmentDTO>> getPage(@PageableDefault(sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok().body(equipmentMapper.convertToDtoList(equipmentService.getAll(pageable)));
    }
}
