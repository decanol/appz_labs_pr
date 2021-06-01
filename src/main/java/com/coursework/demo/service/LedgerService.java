package com.coursework.demo.service;

import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.entity.Ledger;

import java.util.List;

public interface LedgerService extends BasicService<Ledger, Long> {
    List<ExpensesDTO> getExpensesName(String name);
}
