package com.coursework.demo.dto;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentDTO {
    private Long id;

    private String name;

    private Employee employee;

    private Long quantity;

    private Long price;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Building building;
}
