package com.coursework.demo.dto;

import com.coursework.demo.entity.enums.ProcurementType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExpensesDTO {
    private ProcurementType name;

    private Long price;
}