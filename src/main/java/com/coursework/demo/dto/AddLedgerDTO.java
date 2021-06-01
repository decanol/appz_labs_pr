package com.coursework.demo.dto;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.entity.enums.ProcurementType;
import lombok.Data;

@Data
public class AddLedgerDTO {

    private String name;

    private Long quantity;

    private Bookkeeping bookkeeping;

    private ProcurementType procurementType;

    private String unitOfMeasurement;

    private Long price;

    private Building building;
}
