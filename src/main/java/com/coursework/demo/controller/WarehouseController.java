package com.coursework.demo.controller;

import com.coursework.demo.dto.WarehouseDTO;
import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.mapper.WarehouseMapper;
import com.coursework.demo.service.WarehouseService;
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
@Api(tags = "Warehouse API")
@RequestMapping("/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, WarehouseMapper warehouseMapper) {
        this.warehouseService = warehouseService;
        this.warehouseMapper = warehouseMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get warehouse info by id")
    public ResponseEntity<WarehouseDTO> get(@PathVariable("id") long id){
        Warehouse warehouse = warehouseService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(warehouseMapper.convertToDto(warehouse));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get the list of all warehouses")
    public ResponseEntity<List<WarehouseDTO>> getPage(@PageableDefault(sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok().body(warehouseMapper.convertToDtoList(warehouseService.getAll(pageable)));
    }
}
