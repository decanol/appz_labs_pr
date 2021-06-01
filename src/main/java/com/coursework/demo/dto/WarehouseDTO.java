package com.coursework.demo.dto;

import com.coursework.demo.entity.Building;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDTO {

    private Long id;

    private String name;

    private Long quantity;

    private String unitOfMeasurement;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Building building;
}
