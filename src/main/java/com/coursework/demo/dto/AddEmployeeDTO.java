package com.coursework.demo.dto;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.enums.TimeType;
import lombok.Data;

@Data
public class AddEmployeeDTO {

    private String name;

    private String surname;

    private String patronymic;

    private String positionName;

    private TimeType timeType;

    private String email;

    private Long salary;

    private Building building;
}
