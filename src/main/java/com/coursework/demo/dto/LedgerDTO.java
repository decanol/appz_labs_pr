package com.coursework.demo.dto;

import com.coursework.demo.entity.Building;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.entity.enums.ProcurementType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerDTO {

    private Long id;

    private String name;

    private Long quantity;

    private Bookkeeping bookkeeping;

    private ProcurementType procurementType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+2")
    private LocalDateTime dueTime;

    private String unitOfMeasurement;

    private Long price;

    private Building building;
}
